package com.banya.stove;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Reads out the microclimate of the nearest stove. Until humidity and smoke exist (Phase 2/3) this
 * shows temperature and whether the room is sealed.
 */
public class ThermometerBlock extends Block {
    /** How far to look for the stove that owns this room. */
    private static final int SEARCH_RADIUS = 8;

    public ThermometerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            StoveBlockEntity stove = findNearestStove(level, pos);
            player.displayClientMessage(describe(stove), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static Component describe(@Nullable StoveBlockEntity stove) {
        if (stove == null) {
            return Component.translatable("message.banya.thermometer.no_stove")
                    .withStyle(ChatFormatting.GRAY);
        }
        Component reading = Component.translatable("message.banya.thermometer.reading",
                Math.round(stove.getTemperature()), Math.round(stove.getHumidity()));
        if (stove.getRoom() == null) {
            return Component.empty()
                    .append(reading.copy().withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" "))
                    .append(Component.translatable("message.banya.thermometer.leaking")
                            .withStyle(ChatFormatting.RED));
        }
        return reading.copy().withStyle(ChatFormatting.GOLD);
    }

    /**
     * Scans the surrounding cube for a stove. Only runs on interaction, never on a tick, so the
     * brute-force search is cheap enough and avoids caching an owner reference.
     */
    @Nullable
    private static StoveBlockEntity findNearestStove(Level level, BlockPos origin) {
        StoveBlockEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                origin.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
                double distance = pos.distSqr(origin);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = stove;
                }
            }
        }
        return nearest;
    }
}
