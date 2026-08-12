package com.banya.datagen;

import com.banya.Banya;
import com.banya.bath.TubBlock;
import com.banya.registry.ModBlocks;
import com.banya.stove.DamperBlock;
import com.banya.stove.StoveBlock;
import com.banya.stove.ThermometerBlock;
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
        // Fired brick with an iron plate on top and an arched firebox mouth at the front; the lit
        // front doubles as the burning indicator.
        ModelFile stoveOff = models().orientable("stove",
                modLoc("block/stove_side"), modLoc("block/stove_front"), modLoc("block/stove_top"));
        ModelFile stoveOn = models().orientable("stove_lit",
                modLoc("block/stove_side"), modLoc("block/stove_front_on"), modLoc("block/stove_top"));
        getVariantBuilder(ModBlocks.STOVE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(StoveBlock.LIT) ? stoveOn : stoveOff)
                        .rotationY(horizontalAngle(state.getValue(StoveBlock.FACING)))
                        .build());
        simpleBlockItem(ModBlocks.STOVE.get(), stoveOff);

        simpleBlockWithItem(ModBlocks.CHIMNEY.get(),
                models().cubeAll("chimney", modLoc("block/chimney")));
        simpleBlockWithItem(ModBlocks.STOVE_CASING.get(),
                models().cubeAll("stove_casing", modLoc("block/stove_casing")));

        simpleBlockWithItem(ModBlocks.SOOTY_PLANKS.get(),
                models().cubeAll("sooty_planks", modLoc("block/sooty_planks")));
        axisBlock(ModBlocks.SOOTY_LOG.get(), modLoc("block/sooty_log"), modLoc("block/sooty_log_top"));
        simpleBlockItem(ModBlocks.SOOTY_LOG.get(),
                models().getExistingFile(modLoc("block/sooty_log")));

        // The damper reads open or shut at a glance from outside the flue.
        ModelFile damperOpen = models().cubeBottomTop("damper",
                modLoc("block/damper_side"), modLoc("block/damper_side"), modLoc("block/damper_top"));
        ModelFile damperShut = models().cubeBottomTop("damper_closed",
                modLoc("block/damper_side"), modLoc("block/damper_side"),
                modLoc("block/damper_top_closed"));
        getVariantBuilder(ModBlocks.DAMPER.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(DamperBlock.OPEN) ? damperOpen : damperShut)
                        .build());
        simpleBlockItem(ModBlocks.DAMPER.get(), damperOpen);

        // A gauge on the wall rather than a block of iron.
        ModelFile thermometer = models().withExistingParent("thermometer", mcLoc("block/block"))
                .texture("front", modLoc("block/thermometer_front"))
                .texture("side", modLoc("block/thermometer_side"))
                .texture("particle", modLoc("block/thermometer_side"))
                .element().from(3, 2, 14).to(13, 14, 16)
                .face(Direction.NORTH).texture("#front").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.UP).texture("#side").end()
                .face(Direction.DOWN).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end()
                .end();
        getVariantBuilder(ModBlocks.THERMOMETER.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(thermometer)
                        .rotationY(horizontalAngle(state.getValue(ThermometerBlock.FACING)))
                        .build());
        simpleBlockItem(ModBlocks.THERMOMETER.get(), thermometer);

        // The tub is a short open box, so it gets a bottom-anchored model per water state.
        ModelFile tubEmpty = tubModel("tub", "block/tub_top");
        ModelFile tubFilled = tubModel("tub_filled", "block/tub_top_filled");
        getVariantBuilder(ModBlocks.TUB.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(TubBlock.FILLED) ? tubFilled : tubEmpty)
                        .build());
        simpleBlockItem(ModBlocks.TUB.get(), tubEmpty);

        ModelFile polok = models().withExistingParent("polok", mcLoc("block/block"))
                .texture("texture", modLoc("block/polok"))
                .texture("particle", modLoc("block/polok"))
                .element().from(0, 0, 0).to(16, 6, 16).allFaces((dir, face) -> face.texture("#texture")).end();
        simpleBlockWithItem(ModBlocks.POLOK.get(), polok);

        doorBlockWithRenderType((DoorBlock) ModBlocks.BANYA_DOOR.get(),
                modLoc("block/banya_door_bottom"), modLoc("block/banya_door_top"), "cutout");

        simpleBlockWithItem(ModBlocks.SOAPSTONE_ORE.get(),
                models().cubeAll("soapstone_ore", modLoc("block/soapstone_ore")));

        // The chopping block shows whether a log is waiting on it.
        ModelFile choppingEmpty = models().cubeBottomTop("chopping_block",
                modLoc("block/chopping_block_side"), modLoc("block/chopping_block_bottom"),
                modLoc("block/chopping_block_top"));
        ModelFile choppingLoaded = models().cubeBottomTop("chopping_block_loaded",
                modLoc("block/chopping_block_side"), modLoc("block/chopping_block_bottom"),
                modLoc("block/chopping_block_top_loaded"));
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

    /** Model Y-rotation for a horizontal facing, given models are authored facing north. */
    private static int horizontalAngle(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
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
