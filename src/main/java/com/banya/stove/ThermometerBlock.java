package com.banya.stove;

import com.banya.climate.RoomClimate;
import com.banya.climate.StoveLocator;
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

    public ThermometerBlock(Properties properties) {
        super(properties);
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
        // Perceived heat is what the body reacts to, so show it: without this the humidity number
        // looks decorative even though it is doing half the work.
        Component reading = Component.translatable("message.banya.thermometer.reading",
                Math.round(stove.getTemperature()),
                Math.round(stove.getHumidity()),
                Math.round(RoomClimate.heatIndex(stove.getTemperature(), stove.getHumidity())));
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
