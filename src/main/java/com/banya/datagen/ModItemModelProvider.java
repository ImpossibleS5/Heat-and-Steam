package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Item models for standalone items; block items are handled by {@link ModBlockStateProvider}. */
public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.FELT_HAT.get());

        // The filled model is selected at runtime by the "banya:filled" item property
        // registered in BanyaClient, so one item shows both states.
        ItemModelBuilder filled = basicItem(
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, "ladle_filled"));
        basicItem(ModItems.LADLE.get())
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(Banya.MODID, "filled"), 1.0F)
                .model(filled)
                .end();
    }
}
