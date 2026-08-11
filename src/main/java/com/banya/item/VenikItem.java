package com.banya.item;

import com.banya.Config;
import com.banya.climate.StoveLocator;
import com.banya.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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

import java.util.Comparator;
import java.util.List;

/**
 * A venik — the bundle of leafy twigs used to whisk a bather. Steep it in a hot tub first; a dry one
 * crumbles and scratches, so steaming with it is refused.
 *
 * <p>Whisking is a three-second channel. Whoever you are looking at receives the effect, and doing
 * it for someone else is stronger than doing it for yourself — the social pull the design is after.
 */
public class VenikItem extends Item {
    /** How far ahead to look for the person being whisked. */
    private static final double TARGET_REACH = 3.0;
    /** Width of a full vanilla item bar, in pixels. */
    private static final int BAR_SEGMENTS = 13;
    /** Steam blue, so the steeping bar never reads as durability. */
    private static final int STEAM_BAR_COLOR = 0x4FC3F7;

    private final VenikSpecies species;

    public VenikItem(VenikSpecies species, Properties properties) {
        super(properties);
        this.species = species;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isSteeped(stack)) {
            return refuse(level, player, stack, "message.banya.venik.dry");
        }
        if (StoveLocator.heatIndexAt(level, player.blockPosition()) < Config.VENIK_HEAT_INDEX.get()) {
            return refuse(level, player, stack, "message.banya.venik.too_cold");
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
            double multiplier = target == null ? 1.0 : Config.VENIK_OTHER_PLAYER_MULTIPLIER.get();
            LivingEntity receiver = target == null ? player : target;

            this.species.applyTo(receiver, multiplier);
            consumeCharge(stack);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            if (!isSteeped(stack)) {
                player.displayClientMessage(Component.translatable("message.banya.venik.dried_out")
                        .withStyle(ChatFormatting.GRAY), true);
            }

            level.playSound(null, receiver.blockPosition(), SoundEvents.GRASS_BREAK,
                    SoundSource.PLAYERS, 0.9F, 1.2F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                        receiver.getX(), receiver.getY() + 1.0, receiver.getZ(),
                        18, 0.4, 0.6, 0.4, 0.01);
            }
            if (target != null) {
                target.sendSystemMessage(Component.translatable("message.banya.venik.received",
                        player.getDisplayName()).withStyle(ChatFormatting.GOLD));
            }
        }
        return stack;
    }

    /** The player being looked at, if any — whisking someone else is the stronger use. */
    @Nullable
    private static LivingEntity findTarget(Level level, Player player) {
        Vec3 look = player.getLookAngle();
        AABB search = player.getBoundingBox().expandTowards(look.scale(TARGET_REACH)).inflate(1.0);
        List<Player> candidates = level.getEntitiesOfClass(Player.class, search,
                other -> other != player && other.isAlive());
        return candidates.stream()
                .min(Comparator.comparingDouble(player::distanceToSqr))
                .orElse(null);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return Config.VENIK_CHANNEL_TICKS.get();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    /**
     * Being steeped is part of what the venik <em>is</em>, so it reads in the name rather than on a
     * tooltip line below it.
     */
    @Override
    public Component getName(ItemStack stack) {
        return isSteeped(stack)
                ? Component.translatable(this.getDescriptionId(stack) + ".steeped")
                : super.getName(stack);
    }

    /**
     * While steeped, the item bar counts down the whisks left before the venik dries out — the
     * short cycle the bather actually plans around. Once dry it falls back to the vanilla wear bar,
     * which tracks the venik's whole life.
     */
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isSteeped(stack) || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!isSteeped(stack)) {
            return super.getBarWidth(stack);
        }
        int max = Math.max(1, Config.VENIK_STEEP_USES.get());
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

    /** Whisks left before the venik dries out and needs steeping again. */
    public static int steepCharges(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STEEP_CHARGES.get(), 0);
    }

    /** Called by the tub: one steeping is good for several whisks. */
    public static void steep(ItemStack stack) {
        stack.set(ModDataComponents.STEEP_CHARGES.get(), Config.VENIK_STEEP_USES.get());
    }

    private static void consumeCharge(ItemStack stack) {
        stack.set(ModDataComponents.STEEP_CHARGES.get(), Math.max(0, steepCharges(stack) - 1));
    }
}
