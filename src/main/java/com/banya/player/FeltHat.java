package com.banya.player;

import com.banya.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

/** Helper for the banya felt hat, which slows Warmth gain once the room gets dangerous. */
public final class FeltHat {

    private FeltHat() {}

    public static boolean isWornBy(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.FELT_HAT.get());
    }
}
