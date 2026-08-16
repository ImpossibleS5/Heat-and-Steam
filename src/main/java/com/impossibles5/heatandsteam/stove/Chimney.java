package com.impossibles5.heatandsteam.stove;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class Chimney {
    private static final int MAX_HEIGHT = 32;

    private Chimney() {}

    public static ChimneyState detect(Level level, List<BlockPos> bases) {
        ChimneyState best = ChimneyState.NONE;
        for (BlockPos base : bases) {
            ChimneyState found = detectOne(level, base);
            if (found == ChimneyState.OPEN) {
                return found;
            }
            if (rank(found) > rank(best)) {
                best = found;
            }
        }
        return best;
    }

    @Nullable
    public static BlockPos ventOutlet(Level level, List<BlockPos> bases) {
        for (BlockPos base : bases) {
            BlockPos outlet = null;
            for (int offset = 0; offset < MAX_HEIGHT; offset++) {
                BlockPos pos = base.above(offset);
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof ChimneyBlock || state.getBlock() instanceof DamperBlock) {
                    outlet = pos;
                    continue;
                }
                if (offset == 0 && state.getBlock() instanceof StoveCasingBlock) {
                    continue;
                }
                break;
            }
            if (outlet != null && reachesSky(level, outlet.above())) {
                return outlet;
            }
        }
        return null;
    }

    private static int rank(ChimneyState state) {
        return switch (state) {
            case OPEN -> 2;
            case CLOSED -> 1;
            case NONE -> 0;
        };
    }

    private static ChimneyState detectOne(Level level, BlockPos base) {
        boolean damperClosed = false;

        for (int offset = 0; offset < MAX_HEIGHT; offset++) {
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

            if (offset == 0 && state.getBlock() instanceof StoveCasingBlock) {
                continue;
            }

            return reachesSky(level, pos) ? (damperClosed ? ChimneyState.CLOSED : ChimneyState.OPEN)
                    : ChimneyState.NONE;
        }
        return ChimneyState.NONE;
    }

    private static boolean reachesSky(LevelReader level, BlockPos pos) {
        return level.canSeeSky(pos);
    }
}
