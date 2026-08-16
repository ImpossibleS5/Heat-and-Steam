package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HeatAndSteam.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.STOVE.get(), ModBlocks.THERMOMETER.get(), ModBlocks.TALCOCHLORITE_ORE.get(),
                        ModBlocks.CHIMNEY.get(), ModBlocks.DAMPER.get(), ModBlocks.STOVE_CASING.get());
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.STOVE.get(), ModBlocks.TALCOCHLORITE_ORE.get());

        tag(Tags.Blocks.ORES).add(ModBlocks.TALCOCHLORITE_ORE.get());
        tag(ModTags.Blocks.ORES_TALCOCHLORITE).add(ModBlocks.TALCOCHLORITE_ORE.get());

        tag(ModTags.Blocks.INSULATION_TIER_3)
                .addTag(BlockTags.LOGS)
                .addTag(BlockTags.WOOL)
                .add(ModBlocks.SAUNA_DOOR.get(), ModBlocks.SOOTY_LOG.get());

        tag(ModTags.Blocks.INSULATION_TIER_2)
                .addTag(BlockTags.PLANKS)
                .addTag(BlockTags.WOODEN_SLABS)
                .addTag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.SOOTY_PLANKS.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SOOTY_PLANKS.get(), ModBlocks.SOOTY_LOG.get());

        tag(ModTags.Blocks.INSULATION_TIER_1)
                .addTag(BlockTags.STONE_BRICKS)
                .add(Blocks.STONE, Blocks.COBBLESTONE, Blocks.BRICKS, Blocks.GLASS,
                        Blocks.DEEPSLATE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE);
    }
}
