package com.impossibles5.heatandsteam;

import com.impossibles5.heatandsteam.compat.ColdSweatCompat;
import com.impossibles5.heatandsteam.datagen.ModDataGenerator;
import com.impossibles5.heatandsteam.network.ModNetwork;
import com.impossibles5.heatandsteam.registry.ModAttachments;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModCreativeTabs;
import com.impossibles5.heatandsteam.registry.ModDataComponents;
import com.impossibles5.heatandsteam.registry.ModEffects;
import com.impossibles5.heatandsteam.registry.ModEntities;
import com.impossibles5.heatandsteam.registry.ModItems;
import com.impossibles5.heatandsteam.registry.ModMenus;
import com.impossibles5.heatandsteam.registry.ModParticles;
import com.impossibles5.heatandsteam.registry.ModSounds;
import com.impossibles5.heatandsteam.registry.ModTriggers;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(HeatAndSteam.MODID)
public final class HeatAndSteam {
    public static final String MODID = "heat_and_steam";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HeatAndSteam(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.REGISTER.register(modEventBus);
        ModBlockEntities.REGISTER.register(modEventBus);
        ModItems.REGISTER.register(modEventBus);
        ModMenus.REGISTER.register(modEventBus);
        ModAttachments.REGISTER.register(modEventBus);
        ModDataComponents.REGISTER.register(modEventBus);
        ModEffects.REGISTER.register(modEventBus);
        ModEntities.REGISTER.register(modEventBus);
        ModSounds.REGISTER.register(modEventBus);
        ModParticles.REGISTER.register(modEventBus);
        ModTriggers.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Config::onLoad);
        modEventBus.addListener(ModDataGenerator::onGatherData);
        modEventBus.addListener(ModNetwork::register);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ColdSweatCompat::init);
        LOGGER.info("Heat & Steam: common setup complete");
    }
}
