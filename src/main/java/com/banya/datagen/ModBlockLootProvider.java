package com.banya.datagen;

import com.banya.registry.ModBlocks;
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
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // Only iterate this mod's blocks so the provider validates its own tables.
        return ModBlocks.REGISTER.getEntries().stream()
                .map(holder -> (Block) holder.value())
                .toList();
    }
}
