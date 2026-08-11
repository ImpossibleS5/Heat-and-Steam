package com.banya.stove;

import com.banya.item.LadleItem;
import com.banya.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    public StoveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    /**
     * Pouring is handled here rather than in {@code LadleItem#useOn}: a block with a menu consumes
     * the click first, so the item hook would never run and the fuel screen would open instead.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof LadleItem) || !LadleItem.isFilled(stack)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
            pourLadle(level, pos, player, stack, stove);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
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
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
            player.openMenu(stove);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
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
