package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModItems;
import com.banya.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Stones the каменка accepts, graded by how well they bank heat. Tag-driven so packs can add their
 * own rock; quality is read back in {@link com.banya.stove.StoveStones}.
 */
public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output,
                              CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags,
                              ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Banya.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.FIREWOOD)
                .add(ModItems.FIREWOOD_BIRCH.get(), ModItems.FIREWOOD_OAK.get(), ModItems.FIREWOOD_SPRUCE.get());

        // Prepared stones rather than raw building blocks, the same shape as the firewood chain.
        // Still tags, so KubeJS and other mods can qualify their own rock at any tier.
        tag(ModTags.Items.STONES_LOW).add(ModItems.RIVER_STONE.get());
        tag(ModTags.Items.STONES_MID).add(ModItems.ANDESITE_STONE.get());
        tag(ModTags.Items.STONES_HIGH).add(ModItems.BASALT_STONE.get());
        tag(ModTags.Items.STONES_BEST).add(ModItems.SOAPSTONE.get());
    }
}
