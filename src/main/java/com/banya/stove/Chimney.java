package com.banya.stove;

import com.banya.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Finds the flue above a stove: a column of chimney blocks, optionally carrying one damper, running
 * up to open sky.
 *
 * <p>Checked on the stove's simulation step rather than cached, because a chimney is a handful of
 * block lookups and caching it would mean invalidating on every block change nearby.
 */
public final class Chimney {
    private Chimney() {}

    /**
     * Picks the best flue among the columns a stove may vent through.
     *
     * <p>A stove offers more than one candidate column, so that upgrading it with masonry cannot
     * move the flue out from under an already-built chimney. An open path wins over a shut one, and
     * any real flue wins over none: if smoke has a way out anywhere, it takes it.
     *
     * @param bases where a flue could start — see {@link StoveStructure#chimneyBases}
     */
    public static ChimneyState detect(Level level, List<BlockPos> bases) {
        ChimneyState best = ChimneyState.NONE;
        for (BlockPos base : bases) {
            ChimneyState found = detectOne(level, base);
            if (rank(found) > rank(best)) {
                best = found;
            }
        }
        return best;
    }

    /** How much a state counts as "venting", for picking between columns. */
    private static int rank(ChimneyState state) {
        return switch (state) {
            case OPEN -> 2;
            case CLOSED -> 1;
            case NONE -> 0;
        };
    }

    private static ChimneyState detectOne(Level level, BlockPos base) {
        boolean damperClosed = false;

        for (int offset = 0; offset < Config.CHIMNEY_MAX_HEIGHT.get(); offset++) {
            BlockPos pos = base.above(offset);
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
