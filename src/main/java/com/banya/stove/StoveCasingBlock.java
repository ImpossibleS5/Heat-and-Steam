package com.banya.stove;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Masonry built around a firebox. Ordinary stone in every respect but one: if it belongs to a
 * stove's body it hands its clicks through to that stove, so a built-up каменка can be opened,
 * fuelled and ladled from whichever side the bather happens to be standing on.
 *
 * <p>The block stays dumb — no BlockEntity, no cached owner. The stove is looked up on the click,
 * which keeps this in line with the rest of the stove: nothing stored, so nothing can go stale.
 */
public class StoveCasingBlock extends Block {
    public StoveCasingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        Optional<BlockPos> firebox = StoveStructure.findFirebox(level, pos);
        if (firebox.isEmpty()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        // Same ordering as the firebox itself: the ladle gets first refusal, and anything else falls
        // through to useWithoutItem and opens the screen.
        ItemInteractionResult poured = StoveBlock.pourOnto(stack, level, firebox.get(), player);
        return poured == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                ? super.useItemOn(stack, state, level, pos, player, hand, hitResult)
                : poured;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        return StoveStructure.findFirebox(level, pos)
                .map(firebox -> StoveBlock.openScreen(level, firebox, player))
                .orElse(InteractionResult.PASS);
    }
}
