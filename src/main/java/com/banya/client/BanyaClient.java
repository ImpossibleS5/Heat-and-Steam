package com.banya.client;

import com.banya.Banya;
import com.banya.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-only entry point. Never loaded on a dedicated server, so referencing client classes here is safe.
 * HUD, particles and render code live under this {@code client/} package.
 */
@Mod(value = Banya.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Banya.MODID, value = Dist.CLIENT)
public final class BanyaClient {

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        Banya.LOGGER.info("Banya: client setup complete");
    }

    @SubscribeEvent
    static void onRegisterScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenus.STOVE.get(), StoveScreen::new);
    }
}
