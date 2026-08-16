package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModItems;
import com.impossibles5.heatandsteam.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output,
                              CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags,
                              ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, HeatAndSteam.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.FIREWOOD)
                .add(ModItems.FIREWOOD_BIRCH.get(), ModItems.FIREWOOD_OAK.get(), ModItems.FIREWOOD_SPRUCE.get());

        tag(ModTags.Items.STONES_TIER_1).add(ModItems.RIVER_STONE.get());
        tag(ModTags.Items.STONES_TIER_2).add(ModItems.ANDESITE_STONE.get());
        tag(ModTags.Items.STONES_TIER_3).add(ModItems.BASALT_STONE.get());
        tag(ModTags.Items.STONES_TIER_4).add(ModItems.TALCOCHLORITE_STONE.get());

        tag(Tags.Items.ORES).add(ModItems.TALCOCHLORITE_ORE.get());
        tag(ModTags.Items.ORES_TALCOCHLORITE).add(ModItems.TALCOCHLORITE_ORE.get());
    }
}
