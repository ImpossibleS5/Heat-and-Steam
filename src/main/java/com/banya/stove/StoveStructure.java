package com.banya.stove;

import com.banya.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/**
 * Works out which stove has been built around the firebox.
 *
 * <p>Deliberately not a master-and-proxy multiblock: the tier is <em>derived</em> from what stands
 * around the core, checked cheaply each simulation step. Nothing is stored, so nothing can go stale
 * — knock a course of masonry out and the stove simply reports a lower tier on its next step, with
 * no assembly or disassembly bookkeeping to get wrong.
 *
 * <pre>
 *   T2: a ring of casing around the firebox   T3: T2 plus a second ring above it
 *       C C C                                     C C C
 *       C ▣ C   (▣ = firebox)                     C   C   (centre left open)
 *       C C C                                     C C C
 * </pre>
 *
 * The centre of the upper ring stays free on purpose: that is where the flue leaves the stove, so
 * requiring masonry there would have made a massive stove and a chimney mutually exclusive.
 */
public final class StoveStructure {
    private StoveStructure() {}

    public static StoveTier detect(LevelReader level, BlockPos corePos) {
        if (!ringIsCasing(level, corePos)) {
            return StoveTier.T1;
        }
        return ringIsCasing(level, corePos.above()) ? StoveTier.T3 : StoveTier.T2;
    }

    /** The eight blocks surrounding a centre, leaving that centre alone. */
    private static boolean ringIsCasing(LevelReader level, BlockPos centre) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (!isCasing(level, centre.offset(dx, 0, dz))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isCasing(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.STOVE_CASING.get());
    }
}
