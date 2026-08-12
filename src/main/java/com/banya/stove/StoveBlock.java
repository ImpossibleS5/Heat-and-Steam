package com.banya.stove;

import com.banya.item.LadleItem;
import com.banya.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * T1 stove. Hosts the {@link StoveBlockEntity} which owns the room microclimate; right-clicking
 * opens the fuel screen. {@link #LIT} tracks whether it is currently burning.
 */
public class StoveBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    /**
     * Coals still glowing after the flames are out. This is the damper mini-game's tell: shut the
     * flue while it is set and the fumes have nowhere to go but the room.
     */
    public static final BooleanProperty EMBERS = BooleanProperty.create("embers");
    /** Which way the firebox faces, so the stove reads like a real one. */
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public StoveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(EMBERS, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, EMBERS, FACING);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(EMBERS)) {
            return;
        }
        // Smouldering coals: the visible cue that it is too soon to close the damper.
        level.addParticle(ParticleTypes.SMOKE,
                pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
                pos.getY() + 1.0,
                pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
                0.0, 0.02, 0.0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Firebox towards whoever placed it.
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /**
     * Pouring is handled here rather than in {@code LadleItem#useOn}: a block with a menu consumes
     * the click first, so the item hook would never run and the fuel screen would open instead.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult poured = pourOnto(stack, level, pos, player);
        return poured == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                ? super.useItemOn(stack, state, level, pos, player, hand, hitResult)
                : poured;
    }

    /**
     * Throws a filled ladle onto the stove standing at {@code stovePos}.
     *
     * <p>Takes the stove's position rather than reading it off the clicked block, so the masonry
     * around a built-up stove can hand the same interaction through — see {@link StoveCasingBlock}.
     *
     * @return {@code PASS_TO_DEFAULT_BLOCK_INTERACTION} when the held stack is not a filled ladle
     */
    static ItemInteractionResult pourOnto(ItemStack stack, Level level, BlockPos stovePos, Player player) {
        if (!(stack.getItem() instanceof LadleItem) || !LadleItem.isFilled(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide() && level.getBlockEntity(stovePos) instanceof StoveBlockEntity stove) {
            pourLadle(level, stovePos, player, stack, stove);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    /** Opens the fuel screen of the stove standing at {@code stovePos}. */
    static InteractionResult openScreen(Level level, BlockPos stovePos, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(stovePos) instanceof StoveBlockEntity stove) {
            // The tier travels with the open packet so the screen can lay out the right basket.
            player.openMenu(stove, buf -> buf.writeByte(stove.getTier().ordinal()));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static void pourLadle(Level level, BlockPos pos, Player player, ItemStack stack,
                                  StoveBlockEntity stove) {
        boolean lightSteam = stove.pourWater();
        LadleItem.setFilled(stack, false);

        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                0.8F, lightSteam ? 1.6F : 1.1F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                    lightSteam ? 30 : 10, 0.3, 0.3, 0.3, 0.02);
        }
        if (!lightSteam) {
            player.displayClientMessage(
                    Component.translatable("message.banya.steam.heavy").withStyle(ChatFormatting.GRAY), true);
        } else if (stove.consumeCrackedFlag()) {
            level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.7F, 1.3F);
            player.displayClientMessage(
                    Component.translatable("message.banya.stone.cracked").withStyle(ChatFormatting.GRAY), true);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        return openScreen(level, pos, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
            stove.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
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
