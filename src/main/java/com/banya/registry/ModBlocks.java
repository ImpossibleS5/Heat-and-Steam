package com.banya.registry;

import com.banya.Banya;
import com.banya.stove.StoveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Block registry. A matching {@code BlockItem} is registered for each block in {@link ModItems}.
 * Placeholder vanilla textures are used in Phase 1; custom art comes in the polish phase.
 */
public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(Banya.MODID);

    /** T1 single-block stove — owns the room microclimate (simulation added in a later sub-slice). */
    public static final DeferredBlock<StoveBlock> STOVE = REGISTER.registerBlock(
            "stove",
            StoveBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    /** Wall/utility block that reads and displays the room temperature (readout added in a later sub-slice). */
    public static final DeferredBlock<Block> THERMOMETER = REGISTER.registerSimpleBlock(
            "thermometer",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0F)
                    .sound(SoundType.METAL));

    private ModBlocks() {}
}
