package com.banya.datagen;

import com.banya.registry.ModBlocks;
import com.banya.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

/** Drop-self loot tables for all mod blocks. */
public class ModBlockLootProvider extends BlockLootSubProvider {
    protected ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.STOVE.get());
        dropSelf(ModBlocks.THERMOMETER.get());
        dropSelf(ModBlocks.TUB.get());
        dropSelf(ModBlocks.POLOK.get());
        // The ore yields the stone itself, and fortune helps as it does for any ore.
        add(ModBlocks.SOAPSTONE_ORE.get(),
                block -> createOreDrop(block, ModItems.SOAPSTONE.get()));
        dropSelf(ModBlocks.CHOPPING_BLOCK.get());
        dropSelf(ModBlocks.DRYING_RACK.get());
        // A door occupies two blocks; only the lower half should drop an item.
        add(ModBlocks.BANYA_DOOR.get(), block -> createDoorTable(ModBlocks.BANYA_DOOR.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // Only iterate this mod's blocks so the provider validates its own tables.
        return ModBlocks.REGISTER.getEntries().stream()
                .map(holder -> (Block) holder.value())
                .toList();
    }
}
