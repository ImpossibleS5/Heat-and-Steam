package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FluidTagsProvider {
    public ModFluidTagProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> registries,
                               ExistingFileHelper existingFileHelper) {
        super(output, registries, HeatAndSteam.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Fluids.COOLS_STONES).addTag(FluidTags.WATER);
    }
}
