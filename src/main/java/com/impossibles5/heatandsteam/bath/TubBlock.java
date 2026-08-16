package com.impossibles5.heatandsteam.bath;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.climate.StoveLocator;
import com.impossibles5.heatandsteam.item.LadleItem;
import com.impossibles5.heatandsteam.item.WhiskItem;
import com.impossibles5.heatandsteam.registry.ModSounds;
import com.impossibles5.heatandsteam.stove.StoveBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TubBlock extends Block {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);

    public TubBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
                                  BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (!state.getValue(FILLED)) {
            return fill(stack, state, level, pos, player, hand);
        }
        if (stack.getItem() instanceof WhiskItem) {
            return steep(stack, state, level, pos, player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private ItemInteractionResult fill(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand) {
        boolean fromBucket = stack.is(Items.WATER_BUCKET);
        boolean fromLadle = stack.getItem() instanceof LadleItem && LadleItem.isFilled(stack);
        if (!fromBucket && !fromLadle) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide()) {
            if (fromBucket) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
            } else {
                LadleItem.setFilled(stack, false);
            }
            level.setBlockAndUpdate(pos, state.setValue(FILLED, true));
            level.playSound(null, pos, ModSounds.TUB_FILL.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private ItemInteractionResult steep(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        if (WhiskItem.isSteeped(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide()) {
            StoveBlockEntity stove = StoveLocator.findNearest(level, pos);
            double water = stove != null && StoveLocator.roomContains(stove, pos)
                    ? stove.getTemperature()
                    : Config.AMBIENT_TEMPERATURE.get();

            if (water < Config.TUB_STEEP_TEMPERATURE.get()) {
                player.displayClientMessage(Component.translatable("message.heat_and_steam.tub.cold")
                        .withStyle(ChatFormatting.GRAY), true);
                return ItemInteractionResult.sidedSuccess(false);
            }

            WhiskItem.steep(stack);
            level.setBlockAndUpdate(pos, state.setValue(FILLED, false));
            level.playSound(null, pos, ModSounds.WHISK_SOAK.get(), SoundSource.BLOCKS, 0.7F, 1.2F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
}
