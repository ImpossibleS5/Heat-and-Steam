package com.banya.climate;

import com.banya.Config;
import com.banya.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

/**
 * Soot on the walls of a banya fired without a chimney.
 *
 * <p>It is the mark of a seasoned parnaya rather than damage: a well-blackened room gives the best
 * steam there is, which is the whole reason anyone puts up with the smoke. Wood only — stone and
 * glass take no patina.
 */
public final class Soot {
    /** Steps of seasoning between a clean room and a fully earned bonus. */
    public static final int MAX_BAND = 3;

    private Soot() {}

    /** The blackened counterpart of a wall block, or {@code null} if it does not take soot. */
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
            // Keep the log lying the way the builder placed it.
            return state.hasProperty(BlockStateProperties.AXIS)
                    ? sooty.setValue(RotatedPillarBlock.AXIS, state.getValue(BlockStateProperties.AXIS))
                    : sooty;
        }
        return null;
    }

    public static boolean isSooted(BlockState state) {
        return state.is(ModBlocks.SOOTY_PLANKS.get()) || state.is(ModBlocks.SOOTY_LOG.get());
    }

    /** Blackens one wall block if it can take soot. */
    public static boolean darken(Level level, BlockPos pos) {
        BlockState sooted = sootedForm(level.getBlockState(pos));
        if (sooted == null) {
            return false;
        }
        level.setBlockAndUpdate(pos, sooted);
        return true;
    }

    /**
     * Share of the room's walls that have blackened, 0..1.
     *
     * <p>Counted against <em>every</em> wall, not only the ones that could take soot. A stone floor,
     * a window and the stove itself are as much a part of the parnaya as its timber, so a room half
     * built of masonry is genuinely only half a black banya — measuring against the timber alone
     * gave a stone bathhouse with one wooden wall the full bonus for blackening that one wall.
     */
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

    /**
     * How far the seasoning has got, as one of four steps.
     *
     * <p>Steps rather than a sliding scale: the bonus is a property of the room, and a room should
     * not read a degree warmer because one more plank went black while the bather sat in it.
     *
     * @return 0 for a clean room, up to {@link #MAX_BAND} for a thoroughly seasoned one
     */
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

    /** How much of the full bonus this room has earned: none, a third, two thirds or all of it. */
    public static double bonusFactor(double fraction) {
        return band(fraction) / (double) MAX_BAND;
    }

    /**
     * Heat loss multiplier for a seasoned room. Soot on the timber insulates whatever the damper is
     * doing, so unlike the steam bonus this one does not care where the smoke goes.
     */
    public static double insulationMultiplier(double fraction) {
        return 1.0 - bonusFactor(fraction) * Config.SOOT_INSULATION_BONUS.get();
    }
}
