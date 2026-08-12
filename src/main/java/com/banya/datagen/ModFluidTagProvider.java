package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/** Which fluids a red-hot stone can be quenched in. */
public class ModFluidTagProvider extends FluidTagsProvider {
    public ModFluidTagProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> registries,
                               ExistingFileHelper existingFileHelper) {
        super(output, registries, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Water, still and flowing. Lava is pointedly absent — it is not a quench, and a stone
        // thrown into it should not come out cool.
        tag(ModTags.Fluids.COOLS_STONES).addTag(FluidTags.WATER);
    }
}
