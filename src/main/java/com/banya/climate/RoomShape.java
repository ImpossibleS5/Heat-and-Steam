package com.banya.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.Set;

/**
 * Result of a successful room scan: the enclosed interior air cells, the wall cells bounding them,
 * and an axis-aligned bounding box. Immutable; produced by {@link RoomScanner}.
 *
 * @param interior passable cells that make up the parnaya's air volume
 * @param walls    solid cells directly enclosing the interior (used later for insulation)
 * @param bounds   AABB spanning the interior (watched for lazy rescans)
 */
public record RoomShape(Set<BlockPos> interior, Set<BlockPos> walls, AABB bounds) {
    public int volume() {
        return interior.size();
    }
}
