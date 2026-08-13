package com.banya.item;

import com.banya.registry.ModDataComponents;
import com.banya.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The ladle (ковш). Scoop water from any water source, then right-click the stove to throw it on the
 * stones — the поддача that turns a dry hot box into a proper parnaya.
 *
 * <p>Water state is a data component rather than a second item, so the ladle keeps its identity
 * (and any future durability) across fills.
 *
 * <p>Filling happens in {@link #use} rather than {@code useOn}: the crosshair never targets fluids,
 * so a ladle aimed at water produces no block hit at all. Like the vanilla bucket, this does its own
 * raycast that stops at fluid sources. Pouring lives on the stove itself — see
 * {@code StoveBlock#useItemOn} — because a block with a menu would otherwise swallow the click.
 */
public class LadleItem extends Item {
    public LadleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isFilled(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = hit.getBlockPos();
        if (!level.getFluidState(pos).is(Fluids.WATER) || !level.mayInteract(player, pos)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide()) {
            setFilled(stack, true);
        }
        level.playSound(player, pos, ModSounds.LADLE_FILL.get(), SoundSource.PLAYERS, 0.6F, 1.4F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FILLED.get(), false);
    }

    public static void setFilled(ItemStack stack, boolean filled) {
        stack.set(ModDataComponents.FILLED.get(), filled);
    }
}
