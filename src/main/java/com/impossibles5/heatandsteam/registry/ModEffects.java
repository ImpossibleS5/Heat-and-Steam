package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.player.HardeningEffect;
import com.impossibles5.heatandsteam.player.SmokePoisoningEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> REGISTER =
            DeferredRegister.create(Registries.MOB_EFFECT, HeatAndSteam.MODID);

    public static final DeferredHolder<MobEffect, HardeningEffect> HARDENING =
            REGISTER.register("hardening", HardeningEffect::new);

    public static final DeferredHolder<MobEffect, SmokePoisoningEffect> SMOKE_POISONING =
            REGISTER.register("smoke_poisoning", SmokePoisoningEffect::new);

    private ModEffects() {}
}
