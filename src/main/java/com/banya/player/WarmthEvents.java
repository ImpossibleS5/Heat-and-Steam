package com.banya.player;

import com.banya.Banya;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Drives {@link PlayerWarmth} on the game bus. Warmth advances once per second to match the stove's
 * climate step, so the two stay in lockstep and the per-tick cost stays negligible.
 */
@EventBusSubscriber(modid = Banya.MODID)
public final class WarmthEvents {
    private static final int SIMULATION_INTERVAL_TICKS = 20;

    private WarmthEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        // Offset by entity id so players don't all recompute on the same tick.
        if ((player.tickCount + player.getId()) % SIMULATION_INTERVAL_TICKS != 0) {
            return;
        }
        PlayerWarmth.tick(player);
    }
}
