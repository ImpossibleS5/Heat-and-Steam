package com.banya.registry;

import com.banya.Banya;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Moments the advancements listen for.
 *
 * <p>All of them are vanilla's {@link PlayerTrigger}: it carries nothing but "this happened to this
 * player", which is exactly what each of these is, and registering fresh instances under our own ids
 * costs no codec of our own. The gameplay code decides what counts and calls {@code trigger}.
 */
public final class ModTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> REGISTER =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Banya.MODID);

    /** Warmed through for the first time — the banya has worked. */
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FIRST_STEAM =
            register("first_steam");
    /** A venik round finished on a properly hot bather. */
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> LIGHT_STEAM =
            register("light_steam");
    /** Steamed in a banya that earned the soot bonus. */
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> BLACK_BANYA =
            register("black_banya");
    /** Passed out in a room full of smoke. */
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CHOKED =
            register("choked");

    private static DeferredHolder<CriterionTrigger<?>, PlayerTrigger> register(String name) {
        return REGISTER.register(name, PlayerTrigger::new);
    }

    private ModTriggers() {}
}
