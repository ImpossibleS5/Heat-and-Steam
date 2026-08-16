package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.bath.SaunaBenchBlock;
import com.impossibles5.heatandsteam.bath.TubBlock;
import com.impossibles5.heatandsteam.wood.ChoppingBlock;
import com.impossibles5.heatandsteam.wood.DryingRackBlock;
import com.impossibles5.heatandsteam.stove.ChimneyBlock;
import com.impossibles5.heatandsteam.stove.DamperBlock;
import com.impossibles5.heatandsteam.stove.StoveBlock;
import com.impossibles5.heatandsteam.stove.StoveCasingBlock;
import com.impossibles5.heatandsteam.stove.ThermometerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(HeatAndSteam.MODID);

    public static final DeferredBlock<StoveBlock> STOVE = REGISTER.registerBlock(
            "stove",
            StoveBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    public static final DeferredBlock<Block> SOOTY_PLANKS = REGISTER.registerSimpleBlock(
            "sooty_planks",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava());

    public static final DeferredBlock<RotatedPillarBlock> SOOTY_LOG = REGISTER.registerBlock(
            "sooty_log",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava());

    public static final DeferredBlock<StoveCasingBlock> STOVE_CASING = REGISTER.registerBlock(
            "stove_casing",
            StoveCasingBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(2.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    public static final DeferredBlock<ChimneyBlock> CHIMNEY = REGISTER.registerBlock(
            "chimney",
            ChimneyBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    public static final DeferredBlock<DamperBlock> DAMPER = REGISTER.registerBlock(
            "damper",
            DamperBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));

    public static final DeferredBlock<Block> TALCOCHLORITE_ORE = REGISTER.registerSimpleBlock(
            "talcochlorite_ore",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    public static final DeferredBlock<ChoppingBlock> CHOPPING_BLOCK = REGISTER.registerBlock(
            "chopping_block",
            ChoppingBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)

                    .noOcclusion());

    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = REGISTER.registerBlock(
            "drying_rack",
            DryingRackBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    public static final DeferredBlock<SaunaBenchBlock> SAUNA_BENCH = REGISTER.registerBlock(
            "sauna_bench",
            SaunaBenchBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    public static final DeferredBlock<DoorBlock> SAUNA_DOOR = REGISTER.registerBlock(
            "sauna_door",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<TubBlock> TUB = REGISTER.registerBlock(
            "tub",
            TubBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    public static final DeferredBlock<ThermometerBlock> THERMOMETER = REGISTER.registerBlock(
            "thermometer",
            ThermometerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0F)
                    .sound(SoundType.METAL));

    private ModBlocks() {}
}
