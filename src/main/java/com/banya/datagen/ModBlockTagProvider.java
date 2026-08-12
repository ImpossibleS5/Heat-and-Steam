package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModBlocks;
import com.banya.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Mining tags plus the insulation tiers that drive heat loss. Insulation is data-driven on purpose:
 * KubeJS and other mods can classify their own blocks without touching Java.
 */
public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.STOVE.get(), ModBlocks.THERMOMETER.get(), ModBlocks.SOAPSTONE_ORE.get(),
                        ModBlocks.CHIMNEY.get(), ModBlocks.DAMPER.get(), ModBlocks.STOVE_CASING.get());
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.STOVE.get(), ModBlocks.SOAPSTONE_ORE.get());

        // Thick timber holds heat best — the classic banya srub. The banya door earns its place
        // here: a low door with a threshold is exactly what keeps the heat in.
        tag(ModTags.Blocks.INSULATION_HIGH)
                .addTag(BlockTags.LOGS)
                .addTag(BlockTags.WOOL)
                .add(ModBlocks.BANYA_DOOR.get(), ModBlocks.SOOTY_LOG.get());

        tag(ModTags.Blocks.INSULATION_MID)
                .addTag(BlockTags.PLANKS)
                .addTag(BlockTags.WOODEN_SLABS)
                .addTag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.SOOTY_PLANKS.get());

        // Soot changes the look, not the timber: blackened walls insulate as they always did.
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SOOTY_PLANKS.get(), ModBlocks.SOOTY_LOG.get());

        tag(ModTags.Blocks.INSULATION_LOW)
                .addTag(BlockTags.STONE_BRICKS)
                .add(Blocks.STONE, Blocks.COBBLESTONE, Blocks.BRICKS, Blocks.GLASS,
                        Blocks.DEEPSLATE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE);
    }
}
