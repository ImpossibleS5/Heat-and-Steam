package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> REGISTER =
            DeferredRegister.create(Registries.TRIGGER_TYPE, HeatAndSteam.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FIRST_STEAM =
            register("first_steam");

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> LIGHT_STEAM =
            register("light_steam");

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> SMOKE_SAUNA =
            register("smoke_sauna");

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CHOKED =
            register("choked");

    private static DeferredHolder<CriterionTrigger<?>, PlayerTrigger> register(String name) {
        return REGISTER.register(name, PlayerTrigger::new);
    }

    private ModTriggers() {}
}
