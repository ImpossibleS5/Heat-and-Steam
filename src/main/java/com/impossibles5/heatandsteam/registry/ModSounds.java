package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTER =
            DeferredRegister.create(Registries.SOUND_EVENT, HeatAndSteam.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> STEAM_HISS = register("steam_hiss");

    public static final DeferredHolder<SoundEvent, SoundEvent> STONE_CRACK = register("stone_crack");

    public static final DeferredHolder<SoundEvent, SoundEvent> STOVE_CRACKLE = register("stove_crackle");
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMPER_OPEN = register("damper_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMPER_CLOSE = register("damper_close");

    public static final DeferredHolder<SoundEvent, SoundEvent> WHISK_SWISH = register("whisk_swish");

    public static final DeferredHolder<SoundEvent, SoundEvent> WHISK_SOAK = register("whisk_soak");
    public static final DeferredHolder<SoundEvent, SoundEvent> TUB_FILL = register("tub_fill");
    public static final DeferredHolder<SoundEvent, SoundEvent> LADLE_FILL = register("ladle_fill");

    public static final DeferredHolder<SoundEvent, SoundEvent> STONE_SCALD = register("stone_scald");

    public static final DeferredHolder<SoundEvent, SoundEvent> STONE_QUENCH = register("stone_quench");

    public static final DeferredHolder<SoundEvent, SoundEvent> HARDENING = register("hardening");
    public static final DeferredHolder<SoundEvent, SoundEvent> FELT_HAT_EQUIP = register("felt_hat_equip");

    public static final DeferredHolder<SoundEvent, SoundEvent> CHOP = register("chop");

    public static final DeferredHolder<SoundEvent, SoundEvent> WOOD_LOAD = register("wood_load");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return REGISTER.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, name)));
    }

    private ModSounds() {}
}
