package com.banya.compat;

import com.banya.Banya;
import com.banya.Config;
import com.banya.registry.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.fml.ModList;

import java.util.Optional;

/**
 * Makes Закалка mean something to Cold Sweat's temperature model: a hardened bather tolerates a
 * colder world and takes less from the cold that does reach them.
 *
 * <p>Nothing here references a Cold Sweat class. The attributes are looked up in the vanilla
 * registry by id, so the other mod's jar is never touched and its absence cannot throw — the
 * {@link ModList} check below is for the log line, not for safety.
 *
 * <p>The lookup has to happen at common setup rather than in the effect's constructor: the
 * MOB_EFFECT registry is filled before ATTRIBUTE, so at registration time no third-party attribute
 * exists yet.
 */
public final class ColdSweatCompat {
    private static final String MODID = "cold_sweat";

    public static void init() {
        if (!ModList.get().isLoaded(MODID)) {
            return;
        }
        // Cold Sweat states the freezing point in its own "MC units" (1 unit ≈ 23 °C); a lower
        // point means the world has to get colder before it starts hurting. Resistance is a plain
        // 0–1 share of the incoming cold damage that is blocked.
        int wired = hardening("freezing_point", -Config.HARDENING_FREEZING_POINT_DROP.get())
                + hardening("cold_resistance", Config.HARDENING_COLD_RESISTANCE.get());
        Banya.LOGGER.info("Banya: Cold Sweat present, {} Hardening attribute modifiers wired", wired);
    }

    /**
     * @param amount per effect level — the game multiplies it by (amplifier + 1), and Hardening's
     *               amplifier is the contrast cycle count, so three laps are worth three times one
     * @return 1 if the modifier was attached, 0 if the attribute was missing or turned off
     */
    private static int hardening(String attributePath, double amount) {
        if (amount == 0.0) {
            return 0;
        }
        ResourceLocation attribute = ResourceLocation.fromNamespaceAndPath(MODID, attributePath);
        Optional<Holder.Reference<Attribute>> holder = BuiltInRegistries.ATTRIBUTE.getHolder(attribute);
        if (holder.isEmpty()) {
            Banya.LOGGER.warn("Banya: Cold Sweat is loaded but has no {} attribute; Hardening leaves it alone",
                    attribute);
            return 0;
        }
        ModEffects.HARDENING.value().addAttributeModifier(
                holder.get(),
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, "hardening/" + attributePath),
                amount,
                AttributeModifier.Operation.ADD_VALUE);
        return 1;
    }

    private ColdSweatCompat() {}
}
