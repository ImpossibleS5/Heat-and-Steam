package com.banya.datagen;

import com.banya.Banya;
import com.banya.bath.TubBlock;
import com.banya.registry.ModBlocks;
import com.banya.stove.StoveBlock;
import com.banya.wood.ChoppingBlock;
import com.banya.wood.DryingRackBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.DoorBlock;
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

        // The tub is a short open box, so it gets a bottom-anchored model per water state.
        ModelFile tubEmpty = tubModel("tub", "block/tub_top");
        ModelFile tubFilled = tubModel("tub_filled", "block/tub_top_filled");
        getVariantBuilder(ModBlocks.TUB.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(TubBlock.FILLED) ? tubFilled : tubEmpty)
                        .build());
        simpleBlockItem(ModBlocks.TUB.get(), tubEmpty);

        ModelFile polok = models().withExistingParent("polok", mcLoc("block/block"))
                .texture("texture", mcLoc("block/spruce_planks"))
                .texture("particle", mcLoc("block/spruce_planks"))
                .element().from(0, 0, 0).to(16, 6, 16).allFaces((dir, face) -> face.texture("#texture")).end();
        simpleBlockWithItem(ModBlocks.POLOK.get(), polok);

        doorBlockWithRenderType((DoorBlock) ModBlocks.BANYA_DOOR.get(),
                modLoc("block/banya_door_bottom"), modLoc("block/banya_door_top"), "cutout");

        // The chopping block shows whether a log is waiting on it.
        ModelFile choppingEmpty = models().cubeBottomTop("chopping_block",
                mcLoc("block/oak_log"), mcLoc("block/oak_log_top"), modLoc("block/chopping_block_top"));
        ModelFile choppingLoaded = models().cubeBottomTop("chopping_block_loaded",
                mcLoc("block/oak_log"), mcLoc("block/oak_log_top"), mcLoc("block/oak_log_top"));
        getVariantBuilder(ModBlocks.CHOPPING_BLOCK.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(ChoppingBlock.LOADED) ? choppingLoaded : choppingEmpty)
                        .build());
        simpleBlockItem(ModBlocks.CHOPPING_BLOCK.get(), choppingEmpty);

        // The rack wears its contents on the outside: bare, damp, or ready.
        ModelFile rackEmpty = rackModel("drying_rack", "block/drying_rack_empty");
        ModelFile rackDrying = rackModel("drying_rack_drying", "block/drying_rack_drying");
        ModelFile rackDry = rackModel("drying_rack_dry", "block/drying_rack_dry");
        getVariantBuilder(ModBlocks.DRYING_RACK.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(switch (state.getValue(DryingRackBlock.STATE)) {
                            case EMPTY -> rackEmpty;
                            case DRYING -> rackDrying;
                            case DRY -> rackDry;
                        })
                        .build());
        simpleBlockItem(ModBlocks.DRYING_RACK.get(), rackEmpty);
    }

    /** A waist-high rack, 12 blocks tall, textured on every side. */
    private ModelFile rackModel(String name, String texture) {
        return models().withExistingParent(name, mcLoc("block/block"))
                .texture("all", modLoc(texture))
                .texture("particle", modLoc(texture))
                .element().from(0, 0, 0).to(16, 12, 16)
                .allFaces((dir, face) -> face.texture("#all")).end();
    }

    /** A 12x10x12 open tub matching {@code TubBlock}'s collision shape. */
    private ModelFile tubModel(String name, String topTexture) {
        return models().withExistingParent(name, mcLoc("block/block"))
                .texture("side", modLoc("block/tub_side"))
                .texture("top", modLoc(topTexture))
                .texture("particle", modLoc("block/tub_side"))
                .element()
                .from(2, 0, 2).to(14, 10, 14)
                .face(Direction.DOWN).texture("#side").end()
                .face(Direction.UP).texture("#top").end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end()
                .end();
    }
}
