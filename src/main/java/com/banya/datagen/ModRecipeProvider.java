package com.banya.datagen;

import com.banya.registry.ModBlocks;
import com.banya.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

/**
 * Crafting recipes. Inputs go through tags so other mods' equivalents work too.
 *
 * <p>The felt hat is a placeholder recipe for the MVP: the full wool -> felting in a hot tub ->
 * felt -> hat chain arrives with the tub in Phase 2.
 */
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.FELT_HAT.get())
                .pattern("WWW")
                .pattern("W W")
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.STOVE.get())
                .pattern("SSS")
                .pattern("S S")
                .pattern("SIS")
                .define('S', Blocks.STONE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_stone", has(Blocks.STONE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.THERMOMETER.get())
                .pattern("GI")
                .pattern("GI")
                .define('G', Blocks.GLASS)
                .define('I', Items.IRON_NUGGET)
                .unlockedBy("has_glass", has(Blocks.GLASS))
                .save(output);
    }
}
