package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.bath.SaunaBenchBlock;
import com.impossibles5.heatandsteam.bath.TubBlock;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.stove.DamperBlock;
import com.impossibles5.heatandsteam.stove.StoveBlock;
import com.impossibles5.heatandsteam.stove.ThermometerBlock;
import com.impossibles5.heatandsteam.wood.ChoppingBlock;
import com.impossibles5.heatandsteam.wood.DryingRackBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    private static final ResourceLocation BRICKS =
            ResourceLocation.withDefaultNamespace("block/bricks");

    private static final float PROUD = 0.5F;

    private static final float COLLAR = 1.25F;

    private static final float APRON = 2.0F;

    private static final float COLLAR_TOP = 4.0F;
    private static final float COLLAR_BOTTOM = 12.0F;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HeatAndSteam.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        stove();
        masonry();
        damper();
        thermometer();
        tub();
        saunaBench();
        choppingBlock();
        dryingRack();

        simpleBlockWithItem(ModBlocks.SOOTY_PLANKS.get(),
                models().cubeAll("sooty_planks", modLoc("block/sooty_planks")));
        axisBlock(ModBlocks.SOOTY_LOG.get(), modLoc("block/sooty_log"), modLoc("block/sooty_log_top"));
        simpleBlockItem(ModBlocks.SOOTY_LOG.get(),
                models().getExistingFile(modLoc("block/sooty_log")));

        saunaDoor();

        simpleBlockWithItem(ModBlocks.TALCOCHLORITE_ORE.get(),
                models().cubeAll("talcochlorite_ore", modLoc("block/talcochlorite_ore")));
    }

    private void stove() {
        ModelFile cold = stoveModel("stove", false);
        ModelFile lit = stoveModel("stove_lit", true);
        getVariantBuilder(ModBlocks.STOVE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(StoveBlock.LIT) ? lit : cold)
                        .rotationY(horizontalAngle(state.getValue(StoveBlock.FACING)))
                        .build());
        simpleBlockItem(ModBlocks.STOVE.get(), cold);
    }

    private ModelFile stoveModel(String name, boolean lit) {
        BlockModelBuilder model = models().withExistingParent(name, mcLoc("block/block"))
                .texture("bricks", BRICKS)
                .texture("top", modLoc("block/stove_top"))
                .texture("door", modLoc("block/stove_door" + (lit ? "_on" : "")))
                .texture("particle", BRICKS);
        brickBody(model, "#top", "#bricks");

        model.element()
                .from(3, 2, -PROUD).to(13, 12, PROUD)
                .face(Direction.NORTH).texture("#door").uvs(0, 0, 16, 16).end()
                .face(Direction.UP).texture("#door").uvs(2, 0, 14, 2).end()
                .face(Direction.DOWN).texture("#door").uvs(2, 14, 14, 16).end()
                .face(Direction.WEST).texture("#door").uvs(0, 0, 2, 16).end()
                .face(Direction.EAST).texture("#door").uvs(14, 0, 16, 16).end()
                .end();
        return model;
    }

    private void brickBody(BlockModelBuilder model, String topTexture, String bottomTexture) {
        model.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.UP).texture(topTexture).cullface(Direction.UP).end()
                .face(Direction.DOWN).texture(bottomTexture).cullface(Direction.DOWN).end()
                .face(Direction.NORTH).texture("#bricks").cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).texture("#bricks").cullface(Direction.SOUTH).end()
                .face(Direction.WEST).texture("#bricks").cullface(Direction.WEST).end()
                .face(Direction.EAST).texture("#bricks").cullface(Direction.EAST).end()
                .end();
    }

    private void masonry() {
        ModelFile brick = models().cubeAll("stove_casing", BRICKS);
        simpleBlockWithItem(ModBlocks.STOVE_CASING.get(), brick);

        simpleBlockWithItem(ModBlocks.CHIMNEY.get(),
                models().cubeBottomTop("chimney", BRICKS, flueMouth(), flueMouth()));
    }

    private ResourceLocation flueMouth() {
        return modLoc("block/flue_mouth");
    }

    private void damper() {
        ModelFile open = damperModel("damper", true);
        ModelFile shut = damperModel("damper_closed", false);
        getVariantBuilder(ModBlocks.DAMPER.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(DamperBlock.OPEN) ? open : shut)
                        .build());
        simpleBlockItem(ModBlocks.DAMPER.get(), open);
    }

    private ModelFile damperModel(String name, boolean open) {
        String suffix = open ? "" : "_closed";
        BlockModelBuilder model = models().withExistingParent(name, mcLoc("block/block"))
                .texture("bricks", BRICKS)
                .texture("top", modLoc("block/damper_plate" + suffix))

                .texture("bottom", flueMouth())
                .texture("band", modLoc("block/damper_band" + suffix))
                .texture("particle", modLoc("block/damper_band" + suffix));
        brickBody(model, "#top", "#bottom");
        collar(model);
        return model;
    }

    private void collar(BlockModelBuilder model) {
        collarPlate(model, -COLLAR, -COLLAR, 16 + COLLAR, 0, Direction.NORTH);
        collarPlate(model, -COLLAR, 16, 16 + COLLAR, 16 + COLLAR, Direction.SOUTH);
        collarPlate(model, -COLLAR, 0, 0, 16, Direction.WEST);
        collarPlate(model, 16, 0, 16 + COLLAR, 16, Direction.EAST);
    }

    private void collarPlate(BlockModelBuilder model, float x0, float z0, float x1, float z1,
                             Direction outward) {
        boolean alongX = outward.getAxis() == Direction.Axis.Z;
        float rimU = alongX ? 16.0F : COLLAR;
        float rimV = alongX ? COLLAR : 16.0F;
        BlockModelBuilder.ElementBuilder plate = model.element()
                .from(x0, COLLAR_TOP, z0).to(x1, COLLAR_BOTTOM, z1)

                .face(outward).texture("#band").uvs(0, COLLAR_TOP, 16, COLLAR_BOTTOM).end()

                .face(Direction.UP).texture("#band").uvs(0, 0, rimU, rimV).end()
                .face(Direction.DOWN).texture("#band").uvs(0, 16 - rimV, rimU, 16).end();
        if (alongX) {
            plate.face(Direction.WEST).texture("#band")
                    .uvs(0, COLLAR_TOP, COLLAR, COLLAR_BOTTOM).end();
            plate.face(Direction.EAST).texture("#band")
                    .uvs(0, COLLAR_TOP, COLLAR, COLLAR_BOTTOM).end();
        }
        plate.end();
    }

    private void thermometer() {
        ModelFile thermometer = models().withExistingParent("thermometer", mcLoc("block/block"))
                .texture("front", modLoc("block/thermometer_front"))
                .texture("side", modLoc("block/thermometer_side"))
                .texture("particle", modLoc("block/thermometer_side"))
                .element().from(1, 1, 14).to(15, 15, 16)
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
    }

    private void tub() {
        ModelFile empty = tubModel("tub", false);
        ModelFile filled = tubModel("tub_filled", true);
        getVariantBuilder(ModBlocks.TUB.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(TubBlock.FILLED) ? filled : empty)
                        .build());
        simpleBlockItem(ModBlocks.TUB.get(), empty);
    }

    private ModelFile tubModel(String name, boolean filled) {
        BlockModelBuilder model = models().withExistingParent(name, mcLoc("block/block"))
                .texture("side", modLoc("block/tub_side"))
                .texture("inner", modLoc("block/tub_inner"))
                .texture("bottom", modLoc("block/tub_bottom"))
                .texture("particle", modLoc("block/tub_side"));

        model.element().from(4, 0, 4).to(12, 2, 12)
                .face(Direction.UP).texture("#bottom").end()
                .face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
                .end();

        tubWall(model, 2, 2, 14, 4, Direction.NORTH);
        tubWall(model, 2, 12, 14, 14, Direction.SOUTH);
        tubWall(model, 2, 4, 4, 12, Direction.WEST);
        tubWall(model, 12, 4, 14, 12, Direction.EAST);

        if (filled) {
            model.texture("water", modLoc("block/tub_water"));

            model.element().from(4, 2, 4).to(12, 8, 12)
                    .face(Direction.UP).texture("#water").end()
                    .end();
        }
        return model;
    }

    private void tubWall(BlockModelBuilder model, int x0, int z0, int x1, int z1, Direction outward) {
        model.element().from(x0, 0, z0).to(x1, 10, z1)
                .allFaces((dir, face) -> face.texture(dir == outward || dir == Direction.UP
                        ? "#side"
                        : "#inner"))
                .end();
    }

    private void saunaBench() {
        ModelFile seat = benchSeat();
        ModelFile apron = benchApron();
        ModelFile post = benchPost();

        MultiPartBlockStateBuilder builder = getMultipartBuilder(ModBlocks.SAUNA_BENCH.get());

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            builder.part().modelFile(seat).rotationY(horizontalAngle(facing)).addModel()
                    .condition(SaunaBenchBlock.FACING, facing).end();
        }
        record Side(BooleanProperty property, int rotation) {}
        Side[] sides = {
                new Side(SaunaBenchBlock.NORTH, 0), new Side(SaunaBenchBlock.EAST, 90),
                new Side(SaunaBenchBlock.SOUTH, 180), new Side(SaunaBenchBlock.WEST, 270)};
        for (Side side : sides) {
            builder.part().modelFile(apron).rotationY(side.rotation()).addModel()
                    .condition(side.property(), false).end();
        }

        for (int corner = 0; corner < 4; corner++) {
            Side a = sides[corner];
            Side b = sides[(corner + 3) % 4];
            builder.part().modelFile(post).rotationY(corner * 90).addModel()
                    .useOr()
                    .condition(a.property(), false)
                    .condition(b.property(), false)
                    .end();
        }
        simpleBlockItem(ModBlocks.SAUNA_BENCH.get(), models().getExistingFile(modLoc("block/sauna_bench")));
    }

    private ModelFile benchSeat() {
        BlockModelBuilder seat = models().withExistingParent("sauna_bench", mcLoc("block/block"))
                .texture("top", modLoc("block/sauna_bench_top"))
                .texture("side", modLoc("block/sauna_bench_side"))
                .texture("particle", modLoc("block/sauna_bench_top"));
        seat.element().from(0, 4, 0).to(16, 6, 16)
                .face(Direction.UP).texture("#top").cullface(Direction.UP).end()
                .face(Direction.DOWN).texture("#top").end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end()
                .end();

        return seat;
    }

    private ModelFile benchApron() {
        BlockModelBuilder apron = models().withExistingParent("sauna_bench_apron", mcLoc("block/block"))
                .texture("side", modLoc("block/sauna_bench_side"))
                .texture("particle", modLoc("block/sauna_bench_side"));
        apron.element().from(APRON, 0, 0).to(16 - APRON, 4, APRON)
                .allFaces((dir, face) -> {
                    if (dir != Direction.UP) {
                        face.texture("#side");
                    }
                }).end();
        return apron;
    }

    private ModelFile benchPost() {
        BlockModelBuilder post = models().withExistingParent("sauna_bench_post", mcLoc("block/block"))
                .texture("side", modLoc("block/sauna_bench_side"))
                .texture("particle", modLoc("block/sauna_bench_side"));
        post.element().from(0, 0, 0).to(APRON, 4, APRON)
                .allFaces((dir, face) -> {
                    if (dir != Direction.UP) {
                        face.texture("#side");
                    }
                }).end();
        return post;
    }

    private void choppingBlock() {
        BlockModelBuilder stump = models().withExistingParent("chopping_block", mcLoc("block/block"))
                .texture("bark", modLoc("block/chopping_block_side"))
                .texture("top", modLoc("block/chopping_block_top"))
                .texture("bottom", modLoc("block/chopping_block_bottom"))
                .texture("particle", modLoc("block/chopping_block_side"));
        stump.element().from(1, 0, 1).to(15, 12, 15)
                .face(Direction.UP).texture("#top").end()
                .face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
                .face(Direction.NORTH).texture("#bark").end()
                .face(Direction.SOUTH).texture("#bark").end()
                .face(Direction.WEST).texture("#bark").end()
                .face(Direction.EAST).texture("#bark").end()
                .end();
        getVariantBuilder(ModBlocks.CHOPPING_BLOCK.get())
                .forAllStates(state -> ConfiguredModel.builder().modelFile(stump).build());
        simpleBlockItem(ModBlocks.CHOPPING_BLOCK.get(), stump);
    }

    private void dryingRack() {
        ModelFile empty = rackModel("drying_rack", null);
        ModelFile drying = rackModel("drying_rack_drying", "block/billet_damp");
        ModelFile dry = rackModel("drying_rack_dry", "block/billet_dry");
        getVariantBuilder(ModBlocks.DRYING_RACK.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(switch (state.getValue(DryingRackBlock.STATE)) {
                            case EMPTY -> empty;
                            case DRYING -> drying;
                            case DRY -> dry;
                        })
                        .rotationY(horizontalAngle(state.getValue(DryingRackBlock.FACING)))
                        .build());
        simpleBlockItem(ModBlocks.DRYING_RACK.get(), empty);
    }

    private ModelFile rackModel(String name, String billet) {
        BlockModelBuilder model = models().withExistingParent(name, mcLoc("block/block"))
                .texture("post", modLoc("block/rack_post"))
                .texture("rail", modLoc("block/rack_rail"))
                .texture("particle", modLoc("block/rack_post"))

                .ao(false);

        for (int x : new int[] {1, 13}) {
            for (int z : new int[] {1, 13}) {
                model.element().from(x, 0, z).to(x + 2, 12, z + 2)
                        .allFaces((dir, face) -> face.texture("#post")).end();
            }
        }

        for (int z : new int[] {1, 13}) {
            for (int y : new int[] {2, 10}) {
                model.element().from(3, y, z).to(13, y + 2, z + 2)
                        .allFaces((dir, face) -> face.texture("#rail")).end();
            }
        }

        for (int x : new int[] {4, 10}) {
            model.element().from(x, 2, 3).to(x + 2, 4, 13)
                    .allFaces((dir, face) -> face.texture("#rail")).end();
        }
        if (billet != null) {
            model.texture("billet", modLoc(billet))
                    .texture("billet_end", modLoc("block/billet_top"));

            stackBillet(model, 2, 4, 3);
            stackBillet(model, 2, 4, 9);
            stackBillet(model, -0.5F, 7, 6);
        }
        return model;
    }

    private void stackBillet(BlockModelBuilder model, float x0, float y0, float z0) {
        model.element().from(x0, y0, z0).to(x0 + 12, y0 + 3, z0 + 4)
                .face(Direction.WEST).texture("#billet_end").uvs(3, 4, 13, 12).end()
                .face(Direction.EAST).texture("#billet_end").uvs(3, 4, 13, 12).end()
                .face(Direction.UP).texture("#billet").uvs(0, 5, 16, 9).end()
                .face(Direction.DOWN).texture("#billet").uvs(0, 5, 16, 9).end()
                .face(Direction.NORTH).texture("#billet").uvs(0, 6, 16, 9).end()
                .face(Direction.SOUTH).texture("#billet").uvs(0, 6, 16, 9).end()
                .end();
    }

    private void saunaDoor() {
        ModelFile bottomLeft = doorLeaf("sauna_door_bottom_left", false, false, false);
        ModelFile bottomLeftOpen = doorLeaf("sauna_door_bottom_left_open", false, false, true);
        ModelFile bottomRight = doorLeaf("sauna_door_bottom_right", false, true, false);
        ModelFile bottomRightOpen = doorLeaf("sauna_door_bottom_right_open", false, true, true);
        ModelFile topLeft = doorLeaf("sauna_door_top_left", true, false, false);
        ModelFile topLeftOpen = doorLeaf("sauna_door_top_left_open", true, false, true);
        ModelFile topRight = doorLeaf("sauna_door_top_right", true, true, false);
        ModelFile topRightOpen = doorLeaf("sauna_door_top_right_open", true, true, true);
        doorBlock((DoorBlock) ModBlocks.SAUNA_DOOR.get(), bottomLeft, bottomLeftOpen,
                bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
    }

    private ModelFile doorLeaf(String name, boolean top, boolean rightHinge, boolean open) {
        String key = top ? "#top" : "#bottom";
        BlockModelBuilder model = models().withExistingParent(name, mcLoc("block/block"))
                .texture("bottom", modLoc("block/sauna_door_bottom"))
                .texture("top", modLoc("block/sauna_door_top"))
                .texture("particle", modLoc("block/sauna_door_bottom"))

                .ao(false)
                .renderType("cutout");

        boolean straightWest = rightHinge == open;
        var leaf = model.element().from(0, 0, 0).to(3, 16, 16);
        face(leaf, Direction.WEST, key, straightWest, 0, 16).cullface(Direction.WEST).end();
        face(leaf, Direction.EAST, key, !straightWest, 0, 16).end();

        boolean hingeSouth = !straightWest;
        face(leaf, Direction.NORTH, key, open && !rightHinge, hingeSouth ? 6 : 0, hingeSouth ? 9 : 3)
                .cullface(Direction.NORTH).end();
        face(leaf, Direction.SOUTH, key, !(open && rightHinge), hingeSouth ? 0 : 6, hingeSouth ? 3 : 9)
                .cullface(Direction.SOUTH).end();

        edge(leaf, top, rightHinge, open);
        leaf.end();
        return model;
    }

    private BlockModelBuilder.ElementBuilder.FaceBuilder face(
            BlockModelBuilder.ElementBuilder element, Direction dir, String texture,
            boolean straight, float from, float to) {
        return element.face(dir).texture(texture)
                .uvs(straight ? from : to, 0, straight ? to : from, 16);
    }

    private static final int TOP_EDGE_ROW = 8;

    private void edge(BlockModelBuilder.ElementBuilder leaf, boolean top, boolean rightHinge,
                      boolean open) {
        if (top) {
            boolean straight = !rightHinge;
            int near = TOP_EDGE_ROW, far = TOP_EDGE_ROW + 3;
            leaf.face(Direction.UP).texture("#top")
                    .uvs(0, straight ? far : near, 16, straight ? near : far)
                    .rotation(rightHinge == open
                            ? ModelBuilder.FaceRotation.CLOCKWISE_90
                            : ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90)
                    .cullface(Direction.UP).end();
        } else {
            boolean mirrored = rightHinge == open;
            leaf.face(Direction.DOWN).texture("#bottom")
                    .uvs(mirrored ? 16 : 0, open ? 16 : 13, mirrored ? 0 : 16, open ? 13 : 16)
                    .rotation(ModelBuilder.FaceRotation.CLOCKWISE_90)
                    .cullface(Direction.DOWN).end();
        }
    }

    private static int horizontalAngle(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
