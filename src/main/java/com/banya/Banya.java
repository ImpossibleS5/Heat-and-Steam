package com.banya;

import com.banya.compat.ColdSweatCompat;
import com.banya.datagen.ModDataGenerator;
import com.banya.network.ModNetwork;
import com.banya.registry.ModAttachments;
import com.banya.registry.ModBlockEntities;
import com.banya.registry.ModBlocks;
import com.banya.registry.ModCreativeTabs;
import com.banya.registry.ModDataComponents;
import com.banya.registry.ModEffects;
import com.banya.registry.ModEntities;
import com.banya.registry.ModItems;
import com.banya.registry.ModMenus;
import com.banya.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * Main mod entry point. Wires the {@link net.neoforged.neoforge.registries.DeferredRegister}s to the
 * mod event bus. Gameplay systems (climate simulation, player warmth, networking) are added under
 * their own packages in later phases.
 */
@Mod(Banya.MODID)
public final class Banya {
    public static final String MODID = "banya";
    public static final Logger LOGGER = LogUtils.getLogger();

    // FML injects IEventBus (the mod event bus) and ModContainer by parameter type.
    public Banya(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.REGISTER.register(modEventBus);
        ModBlockEntities.REGISTER.register(modEventBus);
        ModItems.REGISTER.register(modEventBus);
        ModMenus.REGISTER.register(modEventBus);
        ModAttachments.REGISTER.register(modEventBus);
        ModDataComponents.REGISTER.register(modEventBus);
        ModEffects.REGISTER.register(modEventBus);
        ModEntities.REGISTER.register(modEventBus);
        ModSounds.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModDataGenerator::onGatherData);
        modEventBus.addListener(ModNetwork::register);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Off the parallel dispatch: this mutates the Hardening effect's modifier table.
        event.enqueueWork(ColdSweatCompat::init);
        LOGGER.info("Banya: common setup complete");
    }
}
