package com.banya.stove;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The damper (вьюшка) that sits in the flue. Open, the smoke leaves and takes heat with it; shut,
 * the heat stays — but so does anything the fire is still producing.
 *
 * <p>Knowing when to shut it is the point: too early and the room fills with fumes, too late and the
 * banya has gone cold.
 */
public class DamperBlock extends Block {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public DamperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        boolean open = !state.getValue(OPEN);
        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, state.setValue(OPEN, open));
            level.playSound(null, pos, open ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE,
                    SoundSource.BLOCKS, 0.8F, 1.2F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
