package com.banya.stove;

import com.banya.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * T1 stove. Hosts the {@link StoveBlockEntity} which owns the room microclimate.
 * The server ticker drives the climate simulation; it is wired here but the
 * simulation body is filled in the climate sub-slice.
 */
public class StoveBlock extends Block implements EntityBlock {
    public StoveBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoveBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        // Server-only ticker; no client animation needed yet.
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.STOVE.get(), StoveBlockEntity::serverTick);
    }

    /**
     * Guards the ticker so it only fires for our BlockEntity type, mirroring the vanilla
     * {@code BaseEntityBlock.createTickerHelper} helper (which is protected on that class).
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private static <A extends BlockEntity, E extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
    }
}
