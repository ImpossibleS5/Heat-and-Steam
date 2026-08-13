package com.banya.registry;

import com.banya.Banya;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Every noise the banya makes, under the mod's own ids.
 *
 * <p>The sounds themselves are borrowed from vanilla by {@code sounds.json} — see
 * {@code datagen/ModSoundProvider}. Registering our own events anyway is what makes them
 * replaceable: gameplay code never names a vanilla sound, so giving the banya real audio later, or
 * letting a resource pack re-skin one moment of it, is a change to that one file.
 */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTER =
            DeferredRegister.create(Registries.SOUND_EVENT, Banya.MODID);

    /** Water hitting hot stones — the поддача itself. */
    public static final DeferredHolder<SoundEvent, SoundEvent> STEAM_HISS = register("steam_hiss");
    /** A stone giving up and splitting after one pour too many. */
    public static final DeferredHolder<SoundEvent, SoundEvent> STONE_CRACK = register("stone_crack");
    /** Fire working in the firebox, heard now and then rather than looped. */
    public static final DeferredHolder<SoundEvent, SoundEvent> STOVE_CRACKLE = register("stove_crackle");
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMPER_OPEN = register("damper_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMPER_CLOSE = register("damper_close");

    /** A venik landing on somebody's back. */
    public static final DeferredHolder<SoundEvent, SoundEvent> VENIK_WHISK = register("venik_whisk");
    /** A venik going into hot water to soften. */
    public static final DeferredHolder<SoundEvent, SoundEvent> VENIK_STEEP = register("venik_steep");
    public static final DeferredHolder<SoundEvent, SoundEvent> TUB_FILL = register("tub_fill");
    public static final DeferredHolder<SoundEvent, SoundEvent> LADLE_FILL = register("ladle_fill");

    /** Grabbing a stone that was far too hot to grab. */
    public static final DeferredHolder<SoundEvent, SoundEvent> STONE_SCALD = register("stone_scald");
    /** The same stone hitting water. */
    public static final DeferredHolder<SoundEvent, SoundEvent> STONE_QUENCH = register("stone_quench");
    /** The plunge that earns Закалка. */
    public static final DeferredHolder<SoundEvent, SoundEvent> HARDENING = register("hardening");
    public static final DeferredHolder<SoundEvent, SoundEvent> FELT_HAT_EQUIP = register("felt_hat_equip");

    public static final DeferredHolder<SoundEvent, SoundEvent> CHOP = register("chop");
    /** A log going onto the chopping block, firewood onto the drying rack. */
    public static final DeferredHolder<SoundEvent, SoundEvent> WOOD_LOAD = register("wood_load");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return REGISTER.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(Banya.MODID, name)));
    }

    private ModSounds() {}
}
