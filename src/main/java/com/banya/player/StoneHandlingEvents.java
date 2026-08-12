package com.banya.player;

import com.banya.Banya;
import com.banya.stove.StoveStones;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

/**
 * Keeps scalding stones on the ground.
 *
 * <p>Without this the burn is a treadmill: the stone leaves your pack, you walk over it, it goes
 * straight back in and burns you again. Refusing the pickup makes the rule legible — a stone out of
 * the fire is left to cool, or quenched, before it can be carried.
 *
 * <p>Judged by the stone tags rather than by item class, so another mod's rock is exactly as hot to
 * the touch as ours.
 */
@EventBusSubscriber(modid = Banya.MODID)
public final class StoneHandlingEvents {
    private StoneHandlingEvents() {}

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        var stack = event.getItemEntity().getItem();
        if (!StoveStones.isStone(stack)) {
            return;
        }
        if (StoveStones.isScalding(stack, event.getPlayer().level().getGameTime())) {
            event.setCanPickup(TriState.FALSE);
        }
    }
}
