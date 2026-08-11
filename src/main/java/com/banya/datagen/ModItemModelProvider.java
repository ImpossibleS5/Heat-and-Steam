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

        venik(ModItems.VENIK_BIRCH.getId().getPath());
        venik(ModItems.VENIK_OAK.getId().getPath());

        // A door's block model cannot be held, so its item is a flat icon.
        basicItem(ModItems.BANYA_DOOR.getId());

        banyaStone(ModItems.RIVER_STONE.getId().getPath());
        banyaStone(ModItems.ANDESITE_STONE.getId().getPath());
        banyaStone(ModItems.BASALT_STONE.getId().getPath());
        banyaStone(ModItems.SOAPSTONE.getId().getPath());

        firewood(ModItems.FIREWOOD_BIRCH.getId().getPath());
        firewood(ModItems.FIREWOOD_OAK.getId().getPath());
        firewood(ModItems.FIREWOOD_SPRUCE.getId().getPath());
    }

    /** A stone shows its cracks: two damage stages before it gives out entirely. */
    private void banyaStone(String name) {
        ItemModelBuilder stage1 = basicItem(
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, name + "_cracked1"));
        ItemModelBuilder stage2 = basicItem(
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, name + "_cracked2"));
        ResourceLocation cracks = ResourceLocation.fromNamespaceAndPath(Banya.MODID, "cracks");
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name))
                .override().predicate(cracks, 1.0F).model(stage1).end()
                .override().predicate(cracks, 2.0F).model(stage2).end();
    }

    /** Damp and dry firewood look different, selected by the banya:dry predicate. */
    private void firewood(String name) {
        ItemModelBuilder dry = basicItem(
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, name + "_dry"));
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name))
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(Banya.MODID, "dry"), 1.0F)
                .model(dry)
                .end();
    }

    /** A venik shows its steeped state through the same predicate trick as the ladle. */
    private void venik(String name) {
        ItemModelBuilder steeped = basicItem(
                ResourceLocation.fromNamespaceAndPath(Banya.MODID, name + "_steeped"));
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name))
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(Banya.MODID, "steeped"), 1.0F)
                .model(steeped)
                .end();
    }
}
