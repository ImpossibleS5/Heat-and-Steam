package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** Tags that decide what does and does not soften the mod's damage. */
public class ModDamageTypeTagProvider extends DamageTypeTagsProvider {
    public ModDamageTypeTagProvider(PackOutput output,
                                    CompletableFuture<HolderLookup.Provider> registries,
                                    ExistingFileHelper existingFileHelper) {
        super(output, registries, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Nothing you can wear or drink helps against breathing carbon monoxide, so угар bypasses
        // the lot: armour, the Resistance effect, and Protection enchantments alike. Without this it
        // is trivially ignorable, which was the whole complaint.
        tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.SMOKE_POISONING);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(ModDamageTypes.SMOKE_POISONING);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ModDamageTypes.SMOKE_POISONING);
    }
}
