package com.impossibles5.heatandsteam.compat;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.fml.ModList;

import java.util.Optional;

public final class ColdSweatCompat {
    private static final String MODID = "cold_sweat";

    public static void init() {
        if (!ModList.get().isLoaded(MODID)) {
            return;
        }

        int wired = hardening("freezing_point", -Config.HARDENING_FREEZING_POINT_DROP.get())
                + hardening("cold_resistance", Config.HARDENING_COLD_RESISTANCE.get());
        HeatAndSteam.LOGGER.info("Heat & Steam: Cold Sweat present, {} Hardening attribute modifiers wired", wired);
    }

    private static int hardening(String attributePath, double amount) {
        if (amount == 0.0) {
            return 0;
        }
        ResourceLocation attribute = ResourceLocation.fromNamespaceAndPath(MODID, attributePath);
        Optional<Holder.Reference<Attribute>> holder = BuiltInRegistries.ATTRIBUTE.getHolder(attribute);
        if (holder.isEmpty()) {
            HeatAndSteam.LOGGER.warn("Heat & Steam: Cold Sweat is loaded but has no {} attribute; Hardening leaves it alone",
                    attribute);
            return 0;
        }
        ModEffects.HARDENING.value().addAttributeModifier(
                holder.get(),
                ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "hardening/" + attributePath),
                amount,
                AttributeModifier.Operation.ADD_VALUE);
        return 1;
    }

    private ColdSweatCompat() {}
}
