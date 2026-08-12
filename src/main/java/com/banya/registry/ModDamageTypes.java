package com.banya.registry;

import com.banya.Banya;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.level.Level;

/**
 * Damage types the mod deals in. These live in the datapack registry rather than a
 * {@link net.neoforged.neoforge.registries.DeferredRegister}, which is what lets a pack tweak them
 * and what lets them carry tags.
 *
 * <p>Угар gets its own type because the vanilla ones would not do: {@code magic} ignores armour but
 * not the Resistance effect, so a bather could sit in a smoke-filled parnaya and take nothing.
 * Choking on carbon monoxide is not something a breastplate or a potion helps with, and the tags on
 * this type say exactly that.
 */
public final class ModDamageTypes {
    public static final ResourceKey<DamageType> SMOKE_POISONING = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Banya.MODID, "smoke_poisoning"));

    private ModDamageTypes() {}

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(SMOKE_POISONING, new DamageType(
                // Death message key: death.attack.banya.smoke_poisoning
                Banya.MODID + ".smoke_poisoning",
                // The fumes are as thick on Peaceful as on Hard; difficulty does not thin them.
                DamageScaling.NEVER,
                // No exhaustion: choking does not make you hungry, and warmth already drains food.
                0.0F,
                DamageEffects.HURT,
                DeathMessageType.DEFAULT));
    }

    /** Resolves the damage source for угар. Needs a level, since datapack registries live there. */
    public static DamageSource smokePoisoning(Level level) {
        Holder<DamageType> type = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(SMOKE_POISONING);
        return new DamageSource(type);
    }
}
