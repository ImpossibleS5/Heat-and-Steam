package com.impossibles5.heatandsteam.network;

import com.impossibles5.heatandsteam.player.WarmthHudData;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private ModNetwork() {}

    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                WarmthSyncPayload.TYPE,
                WarmthSyncPayload.STREAM_CODEC,

                (payload, context) -> WarmthHudData.set(
                        payload.warmth(), payload.strain(), payload.strainRising(), payload.inSauna()));
    }
}
