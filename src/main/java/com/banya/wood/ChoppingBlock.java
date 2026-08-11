package com.banya.wood;

import com.banya.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * The chopping block (колода). Set a log on it, then split it with an axe to get firewood — damp
 * firewood, until it has dried.
 *
 * <p>Two steps rather than one so the log sitting on the block is visible and the axe swing is a
 * deliberate act, which is how the real thing works.
 */
public class ChoppingBlock extends Block implements EntityBlock {
    public static final BooleanProperty LOADED = BooleanProperty.create("loaded");

    public ChoppingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LOADED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOADED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChoppingBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ChoppingBlockEntity chopping)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!state.getValue(LOADED)) {
            return loadLog(stack, state, level, pos, player, chopping);
        }
        if (stack.getItem() instanceof AxeItem) {
            return chop(stack, state, level, pos, player, chopping);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private ItemInteractionResult loadLog(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, ChoppingBlockEntity chopping) {
        if (!stack.is(ItemTags.LOGS)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide()) {
            chopping.setLog(stack.copyWithCount(1));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.setBlockAndUpdate(pos, state.setValue(LOADED, true));
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.8F, 0.9F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private ItemInteractionResult chop(ItemStack axe, BlockState state, Level level, BlockPos pos,
                                       Player player, ChoppingBlockEntity chopping) {
        if (!level.isClientSide()) {
            ItemStack firewood = chopping.splitLog();
            chopping.setLog(ItemStack.EMPTY);
            level.setBlockAndUpdate(pos, state.setValue(LOADED, false));

            if (!firewood.isEmpty()) {
                popResource(level, pos.above(), firewood);
            }
            axe.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 14, 0.3, 0.2, 0.3, 0.05);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ChoppingBlockEntity chopping) {
            chopping.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
