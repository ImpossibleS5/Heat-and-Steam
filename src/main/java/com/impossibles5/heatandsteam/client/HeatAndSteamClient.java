package com.impossibles5.heatandsteam.client;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.item.FirewoodItem;
import com.impossibles5.heatandsteam.item.LadleItem;
import com.impossibles5.heatandsteam.item.WhiskItem;
import com.impossibles5.heatandsteam.player.WarmthHudData;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import com.impossibles5.heatandsteam.registry.ModEntities;
import com.impossibles5.heatandsteam.registry.ModItems;
import com.impossibles5.heatandsteam.registry.ModMenus;
import com.impossibles5.heatandsteam.registry.ModParticles;
import com.impossibles5.heatandsteam.stove.DisplayClock;
import com.impossibles5.heatandsteam.stove.StoveStones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.List;

@Mod(value = HeatAndSteam.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = HeatAndSteam.MODID, value = Dist.CLIENT)
public final class HeatAndSteamClient {
    private static final ResourceLocation WARMTH_LAYER =
            ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "warmth");

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.LADLE.get(),
                    ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "filled"),
                    (stack, level, entity, seed) -> LadleItem.isFilled(stack) ? 1.0F : 0.0F);

            ResourceLocation steeped = ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "soaked");
            for (var whisk : List.of(ModItems.WHISK_BIRCH.get(), ModItems.WHISK_OAK.get())) {
                ItemProperties.register(whisk, steeped,
                        (stack, level, entity, seed) -> WhiskItem.isSteeped(stack) ? 1.0F : 0.0F);
            }

            ResourceLocation cracks = ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "cracks");
            for (var stone : List.of(ModItems.RIVER_STONE.get(), ModItems.ANDESITE_STONE.get(),
                    ModItems.BASALT_STONE.get(), ModItems.TALCOCHLORITE_STONE.get())) {
                ItemProperties.register(stone, cracks,
                        (stack, level, entity, seed) -> StoveStones.cracksOf(stack));
            }

            ResourceLocation dry = ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "dry");
            for (var firewood : List.of(ModItems.FIREWOOD_BIRCH.get(), ModItems.FIREWOOD_OAK.get(),
                    ModItems.FIREWOOD_SPRUCE.get())) {
                ItemProperties.register(firewood, dry,
                        (stack, level, entity, seed) -> FirewoodItem.isDry(stack) ? 1.0F : 0.0F);
            }
        });
        HeatAndSteam.LOGGER.info("Heat & Steam: client setup complete");
    }

    @SubscribeEvent
    static void onRegisterScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenus.STOVE.get(), StoveScreen::new);
        event.register(ModMenus.THERMOMETER.get(), ThermometerScreen::new);
    }

    @SubscribeEvent
    static void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, WARMTH_LAYER, new WarmthHudLayer());
    }

    @SubscribeEvent
    static void onRegisterParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.STEAM.get(), SteamParticle.Provider::new);
    }

    @SubscribeEvent
    static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.CHOPPING_BLOCK.get(),
                ChoppingBlockRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.THERMOMETER.get(),
                ThermometerRenderer::new);
    }

    @SubscribeEvent
    static void onClientTick(final ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            DisplayClock.set(level.getGameTime());
        }
    }

    @SubscribeEvent
    static void onLoggedOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        WarmthHudData.reset();
    }

    @SubscribeEvent
    static void onRespawn(final ClientPlayerNetworkEvent.Clone event) {
        WarmthHudData.reset();
    }
}
