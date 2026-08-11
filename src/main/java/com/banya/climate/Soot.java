package com.banya.climate;

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

    /** Share of the room's walls that have blackened, 0..1. */
    public static double fractionOf(LevelReader level, RoomShape room) {
        if (room.walls().isEmpty()) {
            return 0.0;
        }
        int sooted = 0;
        int eligible = 0;
        for (BlockPos wall : room.walls()) {
            BlockState state = level.getBlockState(wall);
            if (isSooted(state)) {
                sooted++;
                eligible++;
            } else if (sootedForm(state) != null) {
                eligible++;
            }
        }
        return eligible == 0 ? 0.0 : (double) sooted / eligible;
    }
}
