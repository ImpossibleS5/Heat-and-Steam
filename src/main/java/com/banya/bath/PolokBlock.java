package com.banya.bath;

import com.banya.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The polok — the tiered bench you sit on to bathe. Sitting is the point: heat rises, so the upper
 * tier is the hottest seat, and the bench itself speeds the warming further.
 */
public class PolokBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public PolokBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (player.isPassenger() || player.isCrouching()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && !isOccupied(level, pos)) {
            SeatEntity.sit(level, pos, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static boolean isOccupied(Level level, BlockPos pos) {
        return !level.getEntitiesOfClass(SeatEntity.class,
                new net.minecraft.world.phys.AABB(pos)).isEmpty();
    }

    /** Whether this entity is currently perched on a polok, for the Warmth bonus. */
    public static boolean isSittingOnPolok(Entity entity) {
        return entity.getVehicle() instanceof SeatEntity seat
                && entity.level().getBlockState(seat.blockPosition()).is(ModBlocks.POLOK.get());
    }
}
