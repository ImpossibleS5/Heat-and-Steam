package com.banya.climate;

import com.banya.stove.StoveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Finds the stove that owns the microclimate around a given block.
 *
 * <p>Brute-force over a small cube on purpose: this only ever runs on a player interaction, never on
 * a tick, so it stays cheap and avoids every piece of banya furniture caching a stove reference that
 * could go stale when the stove is broken.
 */
public final class StoveLocator {
    /** Far enough to cover a normal parnaya without scanning half the chunk. */
    public static final int DEFAULT_RADIUS = 8;

    private StoveLocator() {}

    @Nullable
    public static StoveBlockEntity findNearest(Level level, BlockPos origin) {
        return findNearest(level, origin, DEFAULT_RADIUS);
    }

    /**
     * Perceived heat at a position, or 0 when it is not inside a sealed parnaya. Used by
     * interactions that are only meaningful in a working banya.
     */
    public static double heatIndexAt(Level level, BlockPos pos) {
        StoveBlockEntity stove = findNearest(level, pos);
        if (stove == null || stove.getRoom() == null || !stove.getRoom().interior().contains(pos)) {
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
