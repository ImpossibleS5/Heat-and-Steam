package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModBlocks;
import com.banya.stove.StoveBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Generates blockstates + block models + block-item models. Textures are vanilla placeholders
 * for Phase 1 (replaced with custom art in the polish phase); the lit stove borrows the magma
 * texture so its burning state is obvious in-game.
 */
public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile stoveOff = models().cubeAll("stove", mcLoc("block/polished_andesite"));
        ModelFile stoveOn = models().cubeAll("stove_lit", mcLoc("block/magma"));
        getVariantBuilder(ModBlocks.STOVE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(StoveBlock.LIT) ? stoveOn : stoveOff)
                        .build());
        simpleBlockItem(ModBlocks.STOVE.get(), stoveOff);

        simpleBlockWithItem(ModBlocks.THERMOMETER.get(),
                models().cubeAll("thermometer", mcLoc("block/iron_block")));
    }
}
