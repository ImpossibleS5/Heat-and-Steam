package com.banya.stove;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Finds the flue above a stove: a column of chimney blocks, optionally carrying one damper, running
 * up to open sky.
 *
 * <p>Checked on the stove's simulation step rather than cached, because a chimney is a handful of
 * block lookups and caching it would mean invalidating on every block change nearby.
 */
public final class Chimney {
    /** Tall enough for any sensible bathhouse, short enough that the check stays trivial. */
    private static final int MAX_HEIGHT = 32;

    private Chimney() {}

    public static ChimneyState detect(Level level, BlockPos stovePos) {
        boolean damperClosed = false;

        for (int offset = 1; offset <= MAX_HEIGHT; offset++) {
            BlockPos pos = stovePos.above(offset);
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof DamperBlock) {
                if (!state.getValue(DamperBlock.OPEN)) {
                    damperClosed = true;
                }
                continue;
            }
            if (state.getBlock() instanceof ChimneyBlock) {
                continue;
            }
            // The column has ended. It only counts as a flue if it broke out into the open.
            return reachesSky(level, pos) ? (damperClosed ? ChimneyState.CLOSED : ChimneyState.OPEN)
                    : ChimneyState.NONE;
        }
        return ChimneyState.NONE;
    }

    private static boolean reachesSky(LevelReader level, BlockPos pos) {
        return level.canSeeSky(pos);
    }
}
