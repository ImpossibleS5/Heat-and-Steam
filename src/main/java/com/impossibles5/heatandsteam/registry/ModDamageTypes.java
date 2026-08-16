package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
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

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> SMOKE_POISONING = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "smoke_poisoning"));

    private ModDamageTypes() {}

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(SMOKE_POISONING, new DamageType(

                HeatAndSteam.MODID + ".smoke_poisoning",

                DamageScaling.NEVER,

                0.0F,
                DamageEffects.HURT,
                DeathMessageType.DEFAULT));
    }

    public static DamageSource smokePoisoning(Level level) {
        Holder<DamageType> type = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(SMOKE_POISONING);
        return new DamageSource(type);
    }
}
