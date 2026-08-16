package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagProvider extends DamageTypeTagsProvider {
    public ModDamageTypeTagProvider(PackOutput output,
                                    CompletableFuture<HolderLookup.Provider> registries,
                                    ExistingFileHelper existingFileHelper) {
        super(output, registries, HeatAndSteam.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.SMOKE_POISONING);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(ModDamageTypes.SMOKE_POISONING);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ModDamageTypes.SMOKE_POISONING);

        tag(DamageTypeTags.NO_KNOCKBACK).add(ModDamageTypes.SMOKE_POISONING);
    }
}
