package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.function.Supplier;

/**
 * Points every {@code banya:} sound event at a vanilla one.
 *
 * <p>The mod ships no audio of its own, so each definition borrows a vanilla <em>event</em> rather
 * than naming a file: that way the borrowed sound keeps all of its variants, and the reference is
 * checked at datagen time against the sound registry, so a name that stopped existing fails the
 * build instead of going quiet in-game. Swapping in real recordings later means editing the borrowed
 * side of these lines and nothing else — no gameplay code names a vanilla sound.
 */
public class ModSoundProvider extends SoundDefinitionsProvider {
    public ModSoundProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, Banya.MODID, helper);
    }

    @Override
    public void registerSounds() {
        // The stove and its steam.
        borrow(ModSounds.STEAM_HISS, SoundEvents.FIRE_EXTINGUISH, "steam_hiss");
        borrow(ModSounds.STONE_CRACK, SoundEvents.STONE_BREAK, "stone_crack");
        borrow(ModSounds.STOVE_CRACKLE, SoundEvents.CAMPFIRE_CRACKLE, "stove_crackle");
        borrow(ModSounds.DAMPER_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, "damper_open");
        borrow(ModSounds.DAMPER_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE, "damper_close");

        // Veniks, tub and ladle.
        borrow(ModSounds.VENIK_WHISK, SoundEvents.GRASS_BREAK, "venik_whisk");
        borrow(ModSounds.VENIK_STEEP, SoundEvents.BREWING_STAND_BREW, "venik_steep");
        borrow(ModSounds.TUB_FILL, SoundEvents.BUCKET_EMPTY, "tub_fill");
        borrow(ModSounds.LADLE_FILL, SoundEvents.BUCKET_FILL, "ladle_fill");

        // What happens to the bather.
        borrow(ModSounds.STONE_SCALD, SoundEvents.FIRE_EXTINGUISH, "stone_scald");
        borrow(ModSounds.STONE_QUENCH, SoundEvents.FIRE_EXTINGUISH, "stone_quench");
        borrow(ModSounds.HARDENING, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, "hardening");
        // The only borrowed sound held as a Holder rather than a bare event.
        add(ModSounds.FELT_HAT_EQUIP, definition()
                .with(sound(SoundEvents.ARMOR_EQUIP_LEATHER.value().getLocation(),
                        SoundDefinition.SoundType.EVENT))
                .subtitle(subtitleKey("felt_hat_equip")));

        // Firewood.
        borrow(ModSounds.CHOP, SoundEvents.WOOD_BREAK, "chop");
        borrow(ModSounds.WOOD_LOAD, SoundEvents.WOOD_PLACE, "wood_load");
    }

    private void borrow(Supplier<SoundEvent> ours, SoundEvent vanilla, String name) {
        add(ours, definition()
                .with(sound(vanilla.getLocation(), SoundDefinition.SoundType.EVENT))
                .subtitle(subtitleKey(name)));
    }

    private static String subtitleKey(String name) {
        return "subtitles." + Banya.MODID + "." + name;
    }
}
