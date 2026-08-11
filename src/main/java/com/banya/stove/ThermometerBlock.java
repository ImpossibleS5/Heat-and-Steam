package com.banya.stove;

import com.banya.climate.RoomClimate;
import com.banya.climate.StoveLocator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Reads out the microclimate of the nearest stove. Until humidity and smoke exist (Phase 2/3) this
 * shows temperature and whether the room is sealed.
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
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.displayClientMessage(describe(StoveLocator.findNearest(level, pos)), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static Component describe(@Nullable StoveBlockEntity stove) {
        if (stove == null) {
            return Component.translatable("message.banya.thermometer.no_stove")
                    .withStyle(ChatFormatting.GRAY);
        }
        // Temperature and humidity always show. The perceived-heat figure only joins them when
        // there is steam: in dry air it equals the temperature, and printing the same number twice
        // is the clutter, not the information.
        long temperature = Math.round(stove.getTemperature());
        long humidity = Math.round(stove.getHumidity());
        Component reading = RoomClimate.isHumid(stove.getHumidity())
                ? Component.translatable("message.banya.thermometer.humid", temperature, humidity,
                        Math.round(RoomClimate.heatIndex(stove.getTemperature(), stove.getHumidity())))
                : Component.translatable("message.banya.thermometer.dry", temperature, humidity);
        if (stove.getRoom() == null) {
            return Component.empty()
                    .append(reading.copy().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" "))
                    .append(Component.translatable("message.banya.thermometer.leaking")
                            .withStyle(ChatFormatting.RED));
        }
        return reading.copy().withStyle(ChatFormatting.GOLD);
    }

}
