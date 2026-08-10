package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Generates blockstates + block models + block-item models. Textures are vanilla placeholders
 * for Phase 1 (replaced with custom art in the polish phase).
 */
public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.STOVE.get(),
                models().cubeAll("stove", mcLoc("block/polished_andesite")));
        simpleBlockWithItem(ModBlocks.THERMOMETER.get(),
                models().cubeAll("thermometer", mcLoc("block/iron_block")));
    }
}
