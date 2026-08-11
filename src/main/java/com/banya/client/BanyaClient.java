package com.banya.client;

import com.banya.Banya;
import com.banya.item.LadleItem;
import com.banya.player.WarmthHudData;
import com.banya.registry.ModItems;
import com.banya.registry.ModMenus;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Client-only entry point. Never loaded on a dedicated server, so referencing client classes here is safe.
 * HUD, particles and render code live under this {@code client/} package.
 */
@Mod(value = Banya.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Banya.MODID, value = Dist.CLIENT)
public final class BanyaClient {
    private static final ResourceLocation WARMTH_LAYER =
            ResourceLocation.fromNamespaceAndPath(Banya.MODID, "warmth");

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        // Item properties touch a non-thread-safe map, so they must run on the main thread.
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.LADLE.get(),
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, "filled"),
                (stack, level, entity, seed) -> LadleItem.isFilled(stack) ? 1.0F : 0.0F));
        Banya.LOGGER.info("Banya: client setup complete");
    }

    @SubscribeEvent
    static void onRegisterScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenus.STOVE.get(), StoveScreen::new);
    }

    @SubscribeEvent
    static void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, WARMTH_LAYER, new WarmthHudLayer());
    }

    @SubscribeEvent
    static void onLoggedOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        // Otherwise a stale bar could carry into the next world joined this session.
        WarmthHudData.reset();
    }
}
