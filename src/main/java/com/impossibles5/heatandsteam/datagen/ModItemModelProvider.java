package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HeatAndSteam.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        feltHat();

        ItemModelBuilder filled = basicItem(
                ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "ladle_filled"));
        basicItem(ModItems.LADLE.get())
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "filled"), 1.0F)
                .model(filled)
                .end();

        whisk(ModItems.WHISK_BIRCH.getId().getPath());
        whisk(ModItems.WHISK_OAK.getId().getPath());

        basicItem(ModItems.SAUNA_DOOR.getId());

        saunaStone(ModItems.RIVER_STONE.getId().getPath());
        saunaStone(ModItems.ANDESITE_STONE.getId().getPath());
        saunaStone(ModItems.BASALT_STONE.getId().getPath());
        saunaStone(ModItems.TALCOCHLORITE_STONE.getId().getPath());

        firewood(ModItems.FIREWOOD_BIRCH.getId().getPath());
        firewood(ModItems.FIREWOOD_OAK.getId().getPath());
        firewood(ModItems.FIREWOOD_SPRUCE.getId().getPath());
    }

    private void feltHat() {
        ItemModelBuilder hat = withExistingParent("felt_hat", mcLoc("block/block"))
                .texture("felt", modLoc("block/felt"))
                .texture("shade", modLoc("block/felt_dark"))
                .texture("particle", modLoc("block/felt"));

        ring(hat, -0.25F, 2.0F, 16.25F, 4.0F, 1, 15, true);
        ring(hat, 0.25F, 4.0F, 15.75F, 7.5F, 1, 15, false);
        felted(hat, 1.0F, 7.5F, 1.0F, 15.0F, 10.0F, 15.0F, false);
        felted(hat, 2.25F, 10.0F, 2.25F, 13.75F, 12.0F, 13.75F, false);
        felted(hat, 3.75F, 12.0F, 3.75F, 12.25F, 13.5F, 12.25F, false);

        hat.transforms()

                .transform(ItemDisplayContext.HEAD).translation(0, 7.5F, 0).scale(1.0F).end()

                .transform(ItemDisplayContext.GUI)
                .rotation(30, 225, 0).translation(0, 0.5F, 0).scale(0.74F).end()
                .transform(ItemDisplayContext.GROUND)
                .translation(0, 3, 0).scale(0.35F).end()
                .transform(ItemDisplayContext.FIXED).scale(0.6F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.4F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, 45, 0).scale(0.4F).end()
                .end();
    }

    private void ring(ItemModelBuilder model, float outer, float bottom, float outerMax, float top,
                      float inner, float innerMax, boolean shaded) {
        float[][] walls = {
                {outer, outer, outerMax, inner},
                {outer, innerMax, outerMax, outerMax},
                {outer, inner, inner, innerMax},
                {innerMax, inner, outerMax, innerMax},
        };
        for (float[] wall : walls) {
            felted(model, wall[0], bottom, wall[1], wall[2], top, wall[3], shaded);
        }
    }

    private void felted(ItemModelBuilder model, float x0, float y0, float z0,
                        float x1, float y1, float z1, boolean shaded) {
        float width = Math.min(16.0F, x1 - x0);
        float depth = Math.min(16.0F, z1 - z0);
        float height = Math.min(16.0F, y1 - y0);
        model.element().from(x0, y0, z0).to(x1, y1, z1)
                .allFaces((dir, face) -> face
                        .texture(shaded && dir == Direction.DOWN ? "#shade" : "#felt")
                        .uvs(0, 0,
                                dir.getAxis() == Direction.Axis.X ? depth : width,
                                dir.getAxis().isVertical() ? depth : height))
                .end();
    }

    private void saunaStone(String name) {
        ItemModelBuilder stage1 = basicItem(
                ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, name + "_cracked1"));
        ItemModelBuilder stage2 = basicItem(
                ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, name + "_cracked2"));
        ResourceLocation cracks = ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "cracks");
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name))
                .override().predicate(cracks, 1.0F).model(stage1).end()
                .override().predicate(cracks, 2.0F).model(stage2).end();
    }

    private void firewood(String name) {
        ItemModelBuilder dry = basicItem(
                ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, name + "_dry"));
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name))
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "dry"), 1.0F)
                .model(dry)
                .end();
    }

    private void whisk(String name) {
        ItemModelBuilder steeped = basicItem(
                ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, name + "_soaked"));
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name))
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "soaked"), 1.0F)
                .model(steeped)
                .end();
    }
}
