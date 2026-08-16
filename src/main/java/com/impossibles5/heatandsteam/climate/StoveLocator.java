package com.impossibles5.heatandsteam.climate;

import com.impossibles5.heatandsteam.stove.StoveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class StoveLocator {
    public static final int DEFAULT_RADIUS = 8;

    private StoveLocator() {}

    @Nullable
    public static StoveBlockEntity findNearest(Level level, BlockPos origin) {
        return findNearest(level, origin, DEFAULT_RADIUS);
    }

    public static boolean roomContains(StoveBlockEntity stove, BlockPos pos) {
        return contains(stove.getRoom(), pos);
    }

    public static boolean contains(@Nullable RoomShape room, BlockPos pos) {
        return room != null && (room.interior().contains(pos) || room.walls().contains(pos));
    }

    public static double heatIndexAt(Level level, BlockPos pos) {
        StoveBlockEntity stove = findNearest(level, pos);
        if (stove == null || !roomContains(stove, pos)) {
            return 0.0;
        }
        return RoomClimate.heatIndex(stove.getTemperature(), stove.getHumidity());
    }

    @Nullable
    public static StoveBlockEntity findNearest(Level level, BlockPos origin, int radius) {
        StoveBlockEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
                double distance = pos.distSqr(origin);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = stove;
                }
            }
        }
        return nearest;
    }
}
