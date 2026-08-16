package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.function.Supplier;

public class ModSoundProvider extends SoundDefinitionsProvider {
    public ModSoundProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, HeatAndSteam.MODID, helper);
    }

    @Override
    public void registerSounds() {
        borrow(ModSounds.STEAM_HISS, SoundEvents.FIRE_EXTINGUISH, "steam_hiss");
        borrow(ModSounds.STONE_CRACK, SoundEvents.STONE_BREAK, "stone_crack");
        borrow(ModSounds.STOVE_CRACKLE, SoundEvents.CAMPFIRE_CRACKLE, "stove_crackle");
        borrow(ModSounds.DAMPER_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, "damper_open");
        borrow(ModSounds.DAMPER_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE, "damper_close");

        borrow(ModSounds.WHISK_SWISH, SoundEvents.GRASS_BREAK, "whisk_swish");
        borrow(ModSounds.WHISK_SOAK, SoundEvents.BREWING_STAND_BREW, "whisk_soak");
        borrow(ModSounds.TUB_FILL, SoundEvents.BUCKET_EMPTY, "tub_fill");
        borrow(ModSounds.LADLE_FILL, SoundEvents.BUCKET_FILL, "ladle_fill");

        borrow(ModSounds.STONE_SCALD, SoundEvents.FIRE_EXTINGUISH, "stone_scald");
        borrow(ModSounds.STONE_QUENCH, SoundEvents.FIRE_EXTINGUISH, "stone_quench");
        borrow(ModSounds.HARDENING, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, "hardening");

        add(ModSounds.FELT_HAT_EQUIP, definition()
                .with(sound(SoundEvents.ARMOR_EQUIP_LEATHER.value().getLocation(),
                        SoundDefinition.SoundType.EVENT))
                .subtitle(subtitleKey("felt_hat_equip")));

        borrow(ModSounds.CHOP, SoundEvents.WOOD_BREAK, "chop");
        borrow(ModSounds.WOOD_LOAD, SoundEvents.WOOD_PLACE, "wood_load");
    }

    private void borrow(Supplier<SoundEvent> ours, SoundEvent vanilla, String name) {
        add(ours, definition()
                .with(sound(vanilla.getLocation(), SoundDefinition.SoundType.EVENT))
                .subtitle(subtitleKey(name)));
    }

    private static String subtitleKey(String name) {
        return "subtitles." + HeatAndSteam.MODID + "." + name;
    }
}
