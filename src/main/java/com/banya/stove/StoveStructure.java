package com.banya.stove;

import com.banya.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;

/**
 * Works out which stove has been built behind the firebox.
 *
 * <p>Deliberately not a master-and-proxy multiblock: the tier is <em>derived</em> from what stands
 * around the firebox, checked cheaply each simulation step. Nothing is stored, so nothing can go
 * stale — knock a course of masonry out and the stove reports a lower tier on its next step, with
 * no assembly or disassembly bookkeeping to get wrong.
 *
 * <p>The firebox sits at the <em>front</em> of the body, not buried in the middle of it. Walling it
 * in on all sides left it unreachable and gave the flue nowhere sensible to go; from the front you
 * can open it, and the chimney rises from the centre of the body behind it, which is where a
 * real stove's flue goes.
 *
 * <pre>
 *   T2, seen from above            T3 adds a course on top
 *     К К К                          К К К
 *     К К К                          К · К    · = the flue's way out
 *     К ▣ К   ▣ = firebox, facing    К К К
 *             the player
 * </pre>
 */
public final class StoveStructure {
    private StoveStructure() {}

    public static StoveTier detect(LevelReader level, BlockPos firebox, Direction facing) {
        if (!bodyIsCasing(level, firebox, facing, 0, false)) {
            return StoveTier.T1;
        }
        return bodyIsCasing(level, firebox, facing, 1, true) ? StoveTier.T3 : StoveTier.T2;
    }

    /**
     * Where the flue leaves the stove: straight up from a bare firebox, and up out of the middle of
     * the body once there is one.
     */
    public static BlockPos chimneyBase(BlockPos firebox, Direction facing, StoveTier tier) {
        return tier == StoveTier.T1
                ? firebox.above()
                : centre(firebox, facing).above();
    }

    /** The middle of the three-by-three body, one block behind the firebox. */
    private static BlockPos centre(BlockPos firebox, Direction facing) {
        return firebox.relative(facing.getOpposite());
    }

    /**
     * Checks the three-by-three body at the given height.
     *
     * @param height     0 for the firebox's own level, 1 for the course above it
     * @param skipCentre true on the upper course, where the centre is the flue's way out
     */
    private static boolean bodyIsCasing(LevelReader level, BlockPos firebox, Direction facing,
                                        int height, boolean skipCentre) {
        Direction back = facing.getOpposite();
        Direction right = back.getClockWise();

        for (int depth = 0; depth <= 2; depth++) {
            for (int side = -1; side <= 1; side++) {
                boolean isFirebox = depth == 0 && side == 0;
                boolean isCentre = depth == 1 && side == 0;
                if ((height == 0 && isFirebox) || (skipCentre && isCentre)) {
                    continue;
                }
                BlockPos pos = firebox.relative(back, depth).relative(right, side).above(height);
                if (!level.getBlockState(pos).is(ModBlocks.STOVE_CASING.get())) {
                    return false;
                }
            }
        }
        return true;
    }
}
