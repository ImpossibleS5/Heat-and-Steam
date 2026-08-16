package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class StoveStructure {
    private static final int BODY_REACH = 2;

    private StoveStructure() {}

    public static StoveTier detect(LevelReader level, BlockPos firebox, Direction facing) {
        if (!bodyIsCasing(level, firebox, facing, 0, false)) {
            return StoveTier.T1;
        }
        return bodyIsCasing(level, firebox, facing, 1, true) ? StoveTier.T3 : StoveTier.T2;
    }

    public static List<BlockPos> chimneyBases(LevelReader level, BlockPos firebox, Direction facing) {
        Direction back = facing.getOpposite();
        Direction right = back.getClockWise();
        List<BlockPos> bases = new ArrayList<>();

        for (int depth = 0; depth <= 2; depth++) {
            for (int side = -1; side <= 1; side++) {
                BlockPos cell = firebox.relative(back, depth).relative(right, side);
                if (cell.equals(firebox)
                        || level.getBlockState(cell).is(ModBlocks.STOVE_CASING.get())) {
                    bases.add(cell.above());
                }
            }
        }
        return bases;
    }

    public static Optional<BlockPos> findFirebox(LevelReader level, BlockPos bodyCell) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int dx = -BODY_REACH; dx <= BODY_REACH; dx++) {
            for (int dz = -BODY_REACH; dz <= BODY_REACH; dz++) {
                for (int dy = -1; dy <= 0; dy++) {
                    BlockPos candidate = bodyCell.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(candidate);
                    if (!state.is(ModBlocks.STOVE.get())) {
                        continue;
                    }
                    Direction facing = state.getValue(StoveBlock.FACING);
                    if (!isBodyCell(candidate, facing, bodyCell)
                            || detect(level, candidate, facing) == StoveTier.T1) {
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

    private static boolean isBodyCell(BlockPos firebox, Direction facing, BlockPos target) {
        Direction back = facing.getOpposite();
        Direction right = back.getClockWise();

        for (int height = 0; height <= 1; height++) {
            for (int depth = 0; depth <= 2; depth++) {
                for (int side = -1; side <= 1; side++) {
                    if (height == 0 && depth == 0 && side == 0) {
                        continue;
                    }
                    if (target.equals(firebox.relative(back, depth).relative(right, side).above(height))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

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
