package com.banya.registry;

import com.banya.Banya;
import com.banya.bath.PolokBlock;
import com.banya.bath.TubBlock;
import com.banya.wood.ChoppingBlock;
import com.banya.wood.DryingRackBlock;
import com.banya.stove.StoveBlock;
import com.banya.stove.ThermometerBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Block registry. A matching {@code BlockItem} is registered for each block in {@link ModItems}.
 * Placeholder vanilla textures are used in Phase 1; custom art comes in the polish phase.
 */
public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(Banya.MODID);

    /** T1 single-block stove — burns fuel and owns the room microclimate. */
    public static final DeferredBlock<StoveBlock> STOVE = REGISTER.registerBlock(
            "stove",
            StoveBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(StoveBlock.LIT) ? 13 : 0));

    /** Set a log on it and split it with an axe. */
    public static final DeferredBlock<ChoppingBlock> CHOPPING_BLOCK = REGISTER.registerBlock(
            "chopping_block",
            ChoppingBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD));

    /** Woodshed rack: damp firewood left here dries out. */
    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = REGISTER.registerBlock(
            "drying_rack",
            DryingRackBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    /** Tiered bench. Sitting high in the room warms you fastest. */
    public static final DeferredBlock<PolokBlock> POLOK = REGISTER.registerBlock(
            "polok",
            PolokBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    /** Low banya door with a threshold; holds heat better than a plain wooden one. */
    public static final DeferredBlock<DoorBlock> BANYA_DOOR = REGISTER.registerBlock(
            "banya_door",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY));

    /** Wooden tub of water the parnaya heats; used to steep veniks. */
    public static final DeferredBlock<TubBlock> TUB = REGISTER.registerBlock(
            "tub",
            TubBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    /** Reads out the microclimate of the nearest stove. */
    public static final DeferredBlock<ThermometerBlock> THERMOMETER = REGISTER.registerBlock(
            "thermometer",
            ThermometerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0F)
                    .sound(SoundType.METAL));

    private ModBlocks() {}
}
