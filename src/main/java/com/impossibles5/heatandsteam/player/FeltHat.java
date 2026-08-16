package com.impossibles5.heatandsteam.player;

import com.impossibles5.heatandsteam.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public final class FeltHat {
    private FeltHat() {}

    public static boolean isWornBy(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.FELT_HAT.get());
    }
}
