package com.impossibles5.heatandsteam.climate;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public final class Soot {
    public static final int MAX_BAND = 3;

    private Soot() {}

    @Nullable
    public static BlockState sootedForm(BlockState state) {
        if (isSooted(state)) {
            return null;
        }
        if (state.is(BlockTags.PLANKS)) {
            return ModBlocks.SOOTY_PLANKS.get().defaultBlockState();
        }
        if (state.is(BlockTags.LOGS)) {
            BlockState sooty = ModBlocks.SOOTY_LOG.get().defaultBlockState();

            return state.hasProperty(BlockStateProperties.AXIS)
                    ? sooty.setValue(RotatedPillarBlock.AXIS, state.getValue(BlockStateProperties.AXIS))
                    : sooty;
        }
        return null;
    }

    public static boolean isSooted(BlockState state) {
        return state.is(ModBlocks.SOOTY_PLANKS.get()) || state.is(ModBlocks.SOOTY_LOG.get());
    }

    public static boolean darken(Level level, BlockPos pos) {
        BlockState sooted = sootedForm(level.getBlockState(pos));
        if (sooted == null) {
            return false;
        }
        level.setBlockAndUpdate(pos, sooted);
        return true;
    }

    public static double fractionOf(LevelReader level, RoomShape room) {
        if (room.walls().isEmpty()) {
            return 0.0;
        }
        int sooted = 0;
        for (BlockPos wall : room.walls()) {
            if (isSooted(level.getBlockState(wall))) {
                sooted++;
            }
        }
        return (double) sooted / room.walls().size();
    }

    public static int band(double fraction) {
        if (fraction >= Config.SOOT_BAND_HEAVY.get()) {
            return 3;
        }
        if (fraction >= Config.SOOT_BAND_MEDIUM.get()) {
            return 2;
        }
        if (fraction >= Config.SOOT_BAND_LIGHT.get()) {
            return 1;
        }
        return 0;
    }

    public static double bonusFactor(double fraction) {
        return band(fraction) / (double) MAX_BAND;
    }

    public static double insulationMultiplier(double fraction) {
        return 1.0 - bonusFactor(fraction) * Config.SOOT_INSULATION_BONUS.get();
    }
}
