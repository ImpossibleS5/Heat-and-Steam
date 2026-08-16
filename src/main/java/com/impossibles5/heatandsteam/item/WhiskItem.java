package com.impossibles5.heatandsteam.item;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.climate.StoveLocator;
import com.impossibles5.heatandsteam.player.PlayerWarmth;
import com.impossibles5.heatandsteam.player.WarmthZone;
import com.impossibles5.heatandsteam.registry.ModDataComponents;
import com.impossibles5.heatandsteam.registry.ModSounds;
import com.impossibles5.heatandsteam.registry.ModTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WhiskItem extends Item {
    private static final double TARGET_REACH = 3.0;
    private static final double AIM_TOLERANCE = 0.75;

    private static final int BAR_SEGMENTS = 13;

    private static final int STEAM_BAR_COLOR = 0x4FC3F7;

    private final WhiskSpecies species;

    public WhiskItem(WhiskSpecies species, Properties properties) {
        super(properties);
        this.species = species;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isSteeped(stack)) {
            return refuse(level, player, stack, "message.heat_and_steam.whisk.dry");
        }
        if (StoveLocator.heatIndexAt(level, player.blockPosition()) < Config.WHISK_HEAT_INDEX.get()) {
            return refuse(level, player, stack, "message.heat_and_steam.whisk.too_cold");
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    private InteractionResultHolder<ItemStack> refuse(Level level, Player player, ItemStack stack, String message) {
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable(message).withStyle(ChatFormatting.GRAY), true);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }
        if (!level.isClientSide()) {
            LivingEntity target = findTarget(level, player);
            double multiplier = target == null ? 1.0 : Config.WHISK_OTHER_PLAYER_MULTIPLIER.get();
            LivingEntity receiver = target == null ? player : target;

            this.species.applyTo(receiver, multiplier);

            if (receiver instanceof ServerPlayer served
                    && WarmthZone.of(PlayerWarmth.get(served)) != WarmthZone.NEUTRAL) {
                ModTriggers.LIGHT_STEAM.get().trigger(served);
            }
            consumeCharge(stack, player);

            if (!isSteeped(stack)) {
                player.displayClientMessage(Component.translatable("message.heat_and_steam.whisk.dried_out")
                        .withStyle(ChatFormatting.GRAY), true);
            }

            level.playSound(null, receiver.blockPosition(), ModSounds.WHISK_SWISH.get(),
                    SoundSource.PLAYERS, 0.9F, 1.2F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                        receiver.getX(), receiver.getY() + 1.0, receiver.getZ(),
                        18, 0.4, 0.6, 0.4, 0.01);
            }
            if (target != null) {
                target.sendSystemMessage(Component.translatable("message.heat_and_steam.whisk.received",
                        player.getDisplayName()).withStyle(ChatFormatting.GOLD));
            }
        }
        return stack;
    }

    @Nullable
    private static LivingEntity findTarget(Level level, Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        AABB search = player.getBoundingBox().expandTowards(look.scale(TARGET_REACH)).inflate(1.0);
        List<Player> candidates = level.getEntitiesOfClass(Player.class, search,
                other -> other != player && other.isAlive() && !other.isSpectator());

        LivingEntity best = null;
        double bestAim = AIM_TOLERANCE;
        for (Player other : candidates) {
            double aim = other.getEyePosition().subtract(eye).normalize().dot(look);
            if (aim >= bestAim && player.hasLineOfSight(other)) {
                bestAim = aim;
                best = other;
            }
        }
        return best;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return Config.WHISK_CHANNEL_TICKS.get();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public Component getName(ItemStack stack) {
        return isSteeped(stack)
                ? Component.translatable(this.getDescriptionId(stack) + ".soaked")
                : super.getName(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isSteeped(stack) || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!isSteeped(stack)) {
            return super.getBarWidth(stack);
        }
        int max = Math.max(1, Config.WHISK_SOAK_USES.get());
        int charges = Math.min(steepCharges(stack), max);
        return Math.max(1, Math.round(BAR_SEGMENTS * charges / (float) max));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return isSteeped(stack) ? STEAM_BAR_COLOR : super.getBarColor(stack);
    }

    public static boolean isSteeped(ItemStack stack) {
        return steepCharges(stack) > 0;
    }

    public static int steepCharges(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STEEP_CHARGES.get(), 0);
    }

    public static void steep(ItemStack stack) {
        stack.set(ModDataComponents.STEEP_CHARGES.get(), Config.WHISK_SOAK_USES.get());
    }

    private static void consumeCharge(ItemStack stack, Player player) {
        int left = Math.max(0, steepCharges(stack) - 1);
        stack.set(ModDataComponents.STEEP_CHARGES.get(), left);
        if (left == 0) {
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }
}
