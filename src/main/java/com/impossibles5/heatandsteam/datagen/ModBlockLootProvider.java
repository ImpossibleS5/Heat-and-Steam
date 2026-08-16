package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {
    protected ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.STOVE.get());
        dropSelf(ModBlocks.THERMOMETER.get());
        dropSelf(ModBlocks.TUB.get());
        dropSelf(ModBlocks.SAUNA_BENCH.get());

        add(ModBlocks.TALCOCHLORITE_ORE.get(),
                block -> createOreDrop(block, ModItems.TALCOCHLORITE_STONE.get()));
        dropSelf(ModBlocks.SOOTY_PLANKS.get());
        dropSelf(ModBlocks.SOOTY_LOG.get());
        dropSelf(ModBlocks.STOVE_CASING.get());
        dropSelf(ModBlocks.CHIMNEY.get());
        dropSelf(ModBlocks.DAMPER.get());
        dropSelf(ModBlocks.CHOPPING_BLOCK.get());
        dropSelf(ModBlocks.DRYING_RACK.get());

        add(ModBlocks.SAUNA_DOOR.get(), block -> createDoorTable(ModBlocks.SAUNA_DOOR.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.REGISTER.getEntries().stream()
                .map(holder -> (Block) holder.value())
                .toList();
    }
}
