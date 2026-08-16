package com.impossibles5.heatandsteam.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.Set;

public record RoomShape(Set<BlockPos> interior, Set<BlockPos> walls, int shell, AABB bounds) {
    public int volume() {
        return interior.size();
    }
}
