package com.impossibles5.heatandsteam.bath;

import com.impossibles5.heatandsteam.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class SaunaBenchBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    private static final Map<Direction, BooleanProperty> SIDES = Map.of(
            Direction.NORTH, NORTH, Direction.SOUTH, SOUTH,
            Direction.EAST, EAST, Direction.WEST, WEST);

    public SaunaBenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
        for (Map.Entry<Direction, BooleanProperty> side : SIDES.entrySet()) {
            state = state.setValue(side.getValue(),
                    joins(context.getLevel(), context.getClickedPos().relative(side.getKey())));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbour,
                                     LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        BooleanProperty side = SIDES.get(direction);
        return side == null ? state : state.setValue(side, neighbour.is(this));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState turned = state.setValue(FACING, rotation.rotate(state.getValue(FACING)));

        for (Map.Entry<Direction, BooleanProperty> side : SIDES.entrySet()) {
            turned = turned.setValue(SIDES.get(rotation.rotate(side.getKey())),
                    state.getValue(side.getValue()));
        }
        return turned;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private static boolean joins(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.SAUNA_BENCH.get());
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

    public static boolean isSittingOnBench(Entity entity) {
        return entity.getVehicle() instanceof SeatEntity seat
                && entity.level().getBlockState(seat.blockPosition()).is(ModBlocks.SAUNA_BENCH.get());
    }
}
