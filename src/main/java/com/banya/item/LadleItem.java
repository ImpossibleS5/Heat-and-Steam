package com.banya.item;

import com.banya.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import com.banya.stove.StoveBlockEntity;

/**
 * The ladle (ковш). Scoop water from any water source, then right-click the stove to throw it on the
 * stones — the поддача that turns a dry hot box into a proper parnaya.
 *
 * <p>Water state is a data component rather than a second item, so the ladle keeps its identity
 * (and any future durability) across fills.
 */
public class LadleItem extends Item {
    public LadleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (isFilled(stack)) {
            return pourOnStove(level, pos, player, stack);
        }
        return fillFrom(level, pos, player, stack);
    }

    private InteractionResult fillFrom(Level level, BlockPos pos, Player player, ItemStack stack) {
        BlockState state = level.getBlockState(pos);
        if (!state.getFluidState().is(Fluids.WATER)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            setFilled(stack, true);
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 0.6F, 1.4F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult pourOnStove(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof StoveBlockEntity stove)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            boolean lightSteam = stove.pourWater();
            setFilled(stack, false);

            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                    0.8F, lightSteam ? 1.6F : 1.1F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                        lightSteam ? 30 : 10, 0.3, 0.3, 0.3, 0.02);
            }
            if (!lightSteam) {
                player.displayClientMessage(
                        Component.translatable("message.banya.steam.heavy").withStyle(ChatFormatting.GRAY),
                        true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FILLED.get(), false);
    }

    private static void setFilled(ItemStack stack, boolean filled) {
        stack.set(ModDataComponents.FILLED.get(), filled);
    }
}
