package com.impossibles5.heatandsteam.player;

import com.impossibles5.heatandsteam.HeatAndSteam;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HeatAndSteam.MODID)
public final class WarmthEvents {
    private static final int SIMULATION_INTERVAL_TICKS = 20;

    private WarmthEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if ((player.tickCount + player.getId()) % SIMULATION_INTERVAL_TICKS != 0) {
            return;
        }
        PlayerWarmth.tick(player);
    }
}
