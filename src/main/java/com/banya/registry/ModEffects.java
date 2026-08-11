package com.banya.registry;

import com.banya.Banya;
import com.banya.player.HardeningEffect;
import com.banya.player.SmokePoisoningEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Status effects owned by the mod. */
public final class ModEffects {
    public static final DeferredRegister<MobEffect> REGISTER =
            DeferredRegister.create(Registries.MOB_EFFECT, Banya.MODID);

    /** "Закалка" — earned by plunging into cold water straight out of the parnaya. */
    public static final DeferredHolder<MobEffect, HardeningEffect> HARDENING =
            REGISTER.register("hardening", HardeningEffect::new);

    /** "Угар" — what a smoky parnaya does to you if you stay in it. */
    public static final DeferredHolder<MobEffect, SmokePoisoningEffect> SMOKE_POISONING =
            REGISTER.register("smoke_poisoning", SmokePoisoningEffect::new);

    private ModEffects() {}
}
