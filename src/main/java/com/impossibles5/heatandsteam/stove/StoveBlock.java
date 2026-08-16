package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.item.LadleItem;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import com.impossibles5.heatandsteam.registry.ModParticles;
import com.impossibles5.heatandsteam.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

public class StoveBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final BooleanProperty EMBERS = BooleanProperty.create("embers");

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final int CRACKLE_ONE_IN = 40;

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
        if (state.getValue(LIT) && random.nextInt(CRACKLE_ONE_IN) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.STOVE_CRACKLE.get(), SoundSource.BLOCKS,
                    0.7F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.2F, false);
        }
        if (!state.getValue(EMBERS)) {
            return;
        }

        level.addParticle(ParticleTypes.SMOKE,
                pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
                pos.getY() + 1.0,
                pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
                0.0, 0.02, 0.0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult poured = pourOnto(stack, level, pos, player);
        return poured == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                ? super.useItemOn(stack, state, level, pos, player, hand, hitResult)
                : poured;
    }

    static ItemInteractionResult pourOnto(ItemStack stack, Level level, BlockPos stovePos, Player player) {
        if (!(stack.getItem() instanceof LadleItem) || !LadleItem.isFilled(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide() && level.getBlockEntity(stovePos) instanceof StoveBlockEntity stove) {
            pourLadle(level, stovePos, player, stack, stove);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    static InteractionResult openScreen(Level level, BlockPos stovePos, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(stovePos) instanceof StoveBlockEntity stove) {
            player.openMenu(stove, buf -> buf.writeByte(stove.getTier().ordinal()));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static void pourLadle(Level level, BlockPos pos, Player player, ItemStack stack,
                                  StoveBlockEntity stove) {
        boolean lightSteam = stove.pourWater();
        LadleItem.setFilled(stack, false);

        level.playSound(null, pos, ModSounds.STEAM_HISS.get(), SoundSource.BLOCKS,
                0.8F, lightSteam ? 1.6F : 1.1F);

        BlockPos steamCell = StoveBlockEntity.particleCellAbove(level, pos);
        if (level instanceof ServerLevel serverLevel && steamCell != null) {
            serverLevel.sendParticles(ModParticles.STEAM.get(),
                    steamCell.getX() + 0.5, steamCell.getY() + 0.1, steamCell.getZ() + 0.5,
                    lightSteam ? 30 : 10, 0.3, 0.3, 0.3, 0.02);
        }
        if (!lightSteam) {
            player.displayClientMessage(
                    Component.translatable("message.heat_and_steam.steam.heavy").withStyle(ChatFormatting.GRAY), true);
        } else if (stove.consumeCrackedFlag()) {
            level.playSound(null, pos, ModSounds.STONE_CRACK.get(), SoundSource.BLOCKS, 0.7F, 1.3F);
            player.displayClientMessage(
                    Component.translatable("message.heat_and_steam.stone.cracked").withStyle(ChatFormatting.GRAY), true);
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
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.STOVE.get(), StoveBlockEntity::serverTick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <A extends BlockEntity, E extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
    }
}
