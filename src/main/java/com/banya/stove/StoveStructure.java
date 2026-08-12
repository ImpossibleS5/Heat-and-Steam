package com.banya.stove;

import com.banya.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

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
    /** How far the firebox can sit from any cell of its own body, horizontally. */
    private static final int BODY_REACH = 2;

    private StoveStructure() {}

    public static StoveTier detect(LevelReader level, BlockPos firebox, Direction facing) {
        if (!bodyIsCasing(level, firebox, facing, 0, false)) {
            return StoveTier.T1;
        }
        return bodyIsCasing(level, firebox, facing, 1, true) ? StoveTier.T3 : StoveTier.T2;
    }

    /**
     * The columns a flue may rise from: straight up out of the firebox, or up out of the middle of
     * the body behind it.
     *
     * <p>Deliberately <em>not</em> keyed on the tier. It used to be — a bare firebox vented upwards,
     * a built-up one out of the centre — and that quietly broke every stove the player upgraded: the
     * moment masonry made it a T2 the base jumped to the other column, found air under the roof, and
     * the stove turned into a banya po-chornomu with its chimney left standing as dead brickwork.
     * Both columns are checked instead, which costs a handful of block lookups and cannot go wrong
     * in either direction. A false positive is not possible: a T3 requires casing directly above the
     * firebox, and casing is neither a chimney nor open to the sky.
     */
    public static List<BlockPos> chimneyBases(BlockPos firebox, Direction facing) {
        return List.of(firebox.above(), centre(firebox, facing).above());
    }

    /** The middle of the three-by-three body, one block behind the firebox. */
    private static BlockPos centre(BlockPos firebox, Direction facing) {
        return firebox.relative(facing.getOpposite());
    }

    /**
     * Finds the firebox that owns a piece of masonry, so the stove can be opened from any side
     * rather than only from the one face its firebox happens to look out of.
     *
     * <p>A straight sweep of the small box the firebox could possibly sit in, not a flood fill: the
     * body is three deep, three wide and two tall, so from any of its cells the firebox is within
     * two blocks horizontally and one below. Fifty block lookups on a click is nothing, and it
     * cannot wander off down a decorative wall the way a fill would.
     *
     * <p>Masonry outside a stove's body finds no owner and stays an ordinary block — build a wall
     * out of casing and it will not open anything.
     */
    public static Optional<BlockPos> findFirebox(LevelReader level, BlockPos bodyCell) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int dx = -BODY_REACH; dx <= BODY_REACH; dx++) {
            for (int dz = -BODY_REACH; dz <= BODY_REACH; dz++) {
                // The body rises one course above the firebox, so the firebox is level or below.
                for (int dy = -1; dy <= 0; dy++) {
                    BlockPos candidate = bodyCell.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(candidate);
                    if (!state.is(ModBlocks.STOVE.get())
                            || !isBodyCell(candidate, state.getValue(StoveBlock.FACING), bodyCell)) {
                        continue;
                    }
                    double distance = candidate.distSqr(bodyCell);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = candidate;
                    }
                }
            }
        }
        return Optional.ofNullable(best);
    }

    /**
     * Whether {@code target} is one of the cells this firebox's body occupies — the same footprint
     * {@link #bodyIsCasing} walks, minus the firebox itself.
     *
     * <p>Deliberately not gated on the tier: a half-built stove should still answer to its masonry,
     * or the player loses access to the screen exactly while rebuilding.
     */
    private static boolean isBodyCell(BlockPos firebox, Direction facing, BlockPos target) {
        Direction back = facing.getOpposite();
        Direction right = back.getClockWise();

        for (int height = 0; height <= 1; height++) {
            for (int depth = 0; depth <= 2; depth++) {
                for (int side = -1; side <= 1; side++) {
                    if (height == 0 && depth == 0 && side == 0) {
                        continue; // the firebox itself, not part of its masonry
                    }
                    if (target.equals(firebox.relative(back, depth).relative(right, side).above(height))) {
                        return true;
                    }
                }
            }
        }
        return false;
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
