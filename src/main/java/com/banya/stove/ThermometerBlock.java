package com.banya.stove;

import com.banya.climate.StoveLocator;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Wall gauge for the room's microclimate. Opening it shows a proper readout screen rather than a
 * line of text over the hotbar: temperature, humidity, smoke and the perceived heat that actually
 * drives Warmth all belong together, and a one-line message could never hold them.
 */
public class ThermometerBlock extends Block {
    /** The wall the gauge hangs on; the dial faces the opposite way. */
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape NORTH = Block.box(3.0, 2.0, 14.0, 13.0, 14.0, 16.0);
    private static final VoxelShape SOUTH = Block.box(3.0, 2.0, 0.0, 13.0, 14.0, 2.0);
    private static final VoxelShape WEST = Block.box(14.0, 2.0, 3.0, 16.0, 14.0, 13.0);
    private static final VoxelShape EAST = Block.box(0.0, 2.0, 3.0, 2.0, 14.0, 13.0);

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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        StoveBlockEntity stove = StoveLocator.findNearest(level, pos);
        if (stove == null) {
            // Nothing to read: say so rather than opening an empty instrument.
            player.displayClientMessage(
                    Component.translatable("message.banya.thermometer.no_stove").withStyle(ChatFormatting.GRAY),
                    true);
            return InteractionResult.CONSUME;
        }

        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, opener) -> new ThermometerMenu(
                        containerId, stove.getClimateData(), ContainerLevelAccess.create(level, pos)),
                Component.translatable("container.banya.thermometer")));
        return InteractionResult.CONSUME;
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
