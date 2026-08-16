package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.climate.RoomShape;
import com.impossibles5.heatandsteam.climate.StoveLocator;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ThermometerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape NORTH = Block.box(1.0, 1.0, 14.0, 15.0, 15.0, 16.0);
    private static final VoxelShape SOUTH = Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 2.0);
    private static final VoxelShape WEST = Block.box(14.0, 1.0, 1.0, 16.0, 15.0, 15.0);
    private static final VoxelShape EAST = Block.box(0.0, 1.0, 1.0, 2.0, 15.0, 15.0);

    public ThermometerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> NORTH;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction facing = clicked.getAxis().isHorizontal()
                ? clicked
                : context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    public static boolean reads(StoveBlockEntity stove, BlockState state, BlockPos pos) {
        RoomShape room = stove.getRoomOrLastSealed();
        return StoveLocator.contains(room, pos)
                || StoveLocator.contains(room, pos.relative(state.getValue(FACING).getOpposite()));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        StoveBlockEntity stove = StoveLocator.findNearest(level, pos);
        if (stove == null) {
            player.displayClientMessage(
                    Component.translatable("message.heat_and_steam.thermometer.no_stove").withStyle(ChatFormatting.GRAY),
                    true);
            return InteractionResult.CONSUME;
        }

        if (!reads(stove, state, pos)) {
            player.displayClientMessage(
                    Component.translatable("message.heat_and_steam.thermometer.outside").withStyle(ChatFormatting.GRAY),
                    true);
            return InteractionResult.CONSUME;
        }

        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, opener) -> new ThermometerMenu(
                        containerId, stove.getClimateData(), ContainerLevelAccess.create(level, pos)),
                Component.translatable("container.heat_and_steam.thermometer")));
        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThermometerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || type != ModBlockEntities.THERMOMETER.get()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        BlockEntityTicker<T> ticker =
                (BlockEntityTicker<T>) (BlockEntityTicker<ThermometerBlockEntity>)
                        ThermometerBlockEntity::serverTick;
        return ticker;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
