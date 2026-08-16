package com.impossibles5.heatandsteam.climate;

import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.stove.StoveStructure;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RoomScanner {
    private static final Direction[] DIRECTIONS = Direction.values();

    private static final int MAX_STRUCTURE_CELLS = 64;

    private RoomScanner() {}

    @Nullable
    public static RoomShape scan(LevelReader level, BlockPos stovePos, int maxVolume) {
        Set<BlockPos> interior = new HashSet<>();
        Set<BlockPos> walls = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

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
            return null;
        }

        List<Face> faces = new ArrayList<>();

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (Direction dir : DIRECTIONS) {
                BlockPos next = pos.relative(dir);
                if (interior.contains(next)) {
                    continue;
                }
                if (structure.contains(next)) {
                    continue;
                }
                if (isPassable(level, next)) {
                    if (interior.size() >= maxVolume) {
                        return null;
                    }
                    interior.add(next);
                    queue.add(next);
                } else {
                    walls.add(next);
                    faces.add(new Face(next, next.relative(dir)));
                }
            }
        }

        return new RoomShape(Set.copyOf(interior), Set.copyOf(walls), countShell(faces, interior),
                computeBounds(interior));
    }

    private record Face(BlockPos wall, BlockPos beyond) {}

    private static int countShell(List<Face> faces, Set<BlockPos> interior) {
        int shell = 0;
        for (Face face : faces) {
            if (!interior.contains(face.beyond())) {
                shell++;
            }
        }
        return shell;
    }

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
        return level.getBlockState(pos).is(ModBlocks.STOVE_CASING.get())
                && StoveStructure.findFirebox(level, pos).isPresent();
    }

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
