package com.banya.climate;

import com.banya.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * Detects the enclosed room (parnaya) around a stove by flood-filling the surrounding air volume.
 *
 * <p>The fill is bounded by {@code maxVolume}: if the passable volume reaches the cap before it
 * closes, the space is considered open/leaking (or simply too large) and {@code null} is returned.
 * A hole to the outside makes the volume overflow the cap, so no explicit "open to sky" check is
 * needed. The scan is a pure function of the {@link LevelReader}, which keeps it testable.
 */
public final class RoomScanner {
    private static final Direction[] DIRECTIONS = Direction.values();
    /** Enough for a massive stove and a tall flue, small enough that a stray casing wall cannot sprawl. */
    private static final int MAX_STRUCTURE_CELLS = 64;

    private RoomScanner() {}

    /**
     * @return the enclosed {@link RoomShape}, or {@code null} if the stove is not inside a sealed
     *         room within the volume cap.
     */
    @Nullable
    public static RoomShape scan(LevelReader level, BlockPos stovePos, int maxVolume) {
        Set<BlockPos> interior = new HashSet<>();
        Set<BlockPos> walls = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        // Seed from everything the stove is made of, not just its core. A stove with masonry round
        // it and a flue on top has no passable neighbour at all, and seeding from the core alone
        // reported every built-up stove as "encased" — which killed the room outright.
        Set<BlockPos> structure = collectStructure(level, stovePos);
        for (BlockPos cell : structure) {
            for (Direction dir : DIRECTIONS) {
                BlockPos seed = cell.relative(dir);
                if (!structure.contains(seed) && isPassable(level, seed) && interior.add(seed)) {
                    queue.add(seed);
                }
            }
        }
        if (interior.isEmpty()) {
            return null; // walled in on every side — no room to heat
        }

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (Direction dir : DIRECTIONS) {
                BlockPos next = pos.relative(dir);
                if (interior.contains(next)) {
                    continue;
                }
                if (structure.contains(next)) {
                    walls.add(next);
                    continue;
                }
                if (isPassable(level, next)) {
                    if (interior.size() >= maxVolume) {
                        return null; // overflowed the cap — open or too large
                    }
                    interior.add(next);
                    queue.add(next);
                } else {
                    walls.add(next);
                }
            }
        }

        return new RoomShape(Set.copyOf(interior), Set.copyOf(walls), computeBounds(interior));
    }

    /**
     * The blocks that make up the stove itself: the firebox, any masonry built onto it, and the
     * flue. Gathered so the room can be found from around the whole thing.
     */
    private static Set<BlockPos> collectStructure(LevelReader level, BlockPos stovePos) {
        Set<BlockPos> structure = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        structure.add(stovePos);
        queue.add(stovePos);

        while (!queue.isEmpty() && structure.size() < MAX_STRUCTURE_CELLS) {
            BlockPos pos = queue.poll();
            for (Direction dir : DIRECTIONS) {
                BlockPos next = pos.relative(dir);
                if (structure.size() >= MAX_STRUCTURE_CELLS) {
                    break;
                }
                if (!structure.contains(next) && isStovePart(level, next)) {
                    structure.add(next);
                    queue.add(next);
                }
            }
        }
        return structure;
    }

    private static boolean isStovePart(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModBlocks.STOVE_CASING.get())
                || state.is(ModBlocks.CHIMNEY.get())
                || state.is(ModBlocks.DAMPER.get());
    }

    /**
     * Whether heat and steam can move through a cell.
     *
     * <p>Anything with collision holds the climate in — a closed door, a pane, a fence. Doors,
     * trapdoors and gates are judged by whether they stand open, which is the whole point of
     * shutting the banya door. Note this is deliberately not "is it a full cube": a closed door is
     * only three pixels thick, and treating it as open let every room leak straight through it.
     */
    private static boolean isPassable(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return true;
        }
        if (state.getBlock() instanceof DoorBlock
                || state.getBlock() instanceof TrapDoorBlock
                || state.getBlock() instanceof FenceGateBlock) {
            return state.getValue(BlockStateProperties.OPEN);
        }
        return state.getCollisionShape(level, pos).isEmpty();
    }

    private static AABB computeBounds(Set<BlockPos> cells) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : cells) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        return new AABB(minX, minY, minZ, maxX + 1.0, maxY + 1.0, maxZ + 1.0);
    }
}
