package com.banya.network;

import com.banya.player.WarmthHudData;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Payload registration. Bumping the version string invalidates old clients on connect. */
public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private ModNetwork() {}

    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                WarmthSyncPayload.TYPE,
                WarmthSyncPayload.STREAM_CODEC,
                // Runs on the client main thread; it only caches values for the HUD to read.
                (payload, context) -> WarmthHudData.set(payload.warmth(), payload.inBanya()));
    }
}
