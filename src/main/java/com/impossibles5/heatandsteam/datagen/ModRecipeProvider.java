package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

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

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.LADLE.get())
                .pattern("  S")
                .pattern("P P")
                .pattern(" P ")
                .define('S', Items.STICK)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.STOVE.get())
                .pattern("SSS")
                .pattern("S S")
                .pattern("SIS")
                .define('S', Blocks.STONE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_stone", has(Blocks.STONE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TUB.get())
                .pattern("P P")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHOPPING_BLOCK.get())
                .pattern("L")
                .pattern("L")
                .define('L', ItemTags.LOGS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DRYING_RACK.get())
                .pattern("SSS")
                .pattern("P P")
                .pattern("P P")
                .define('S', Items.STICK)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SAUNA_BENCH.get(), 4)
                .pattern("PPP")
                .pattern("S S")
                .define('P', ItemTags.PLANKS)
                .define('S', Items.STICK)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.SAUNA_DOOR.get(), 2)
                .pattern("LL")
                .pattern("LL")
                .pattern("LL")
                .define('L', ItemTags.LOGS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(output);

        saunaStone(output, ModItems.RIVER_STONE.get(),
                Blocks.COBBLESTONE, Blocks.STONE, Blocks.GRANITE);
        saunaStone(output, ModItems.ANDESITE_STONE.get(),
                Blocks.ANDESITE, Blocks.DIORITE, Blocks.DEEPSLATE);
        saunaStone(output, ModItems.BASALT_STONE.get(),
                Blocks.BASALT, Blocks.SMOOTH_BASALT, Blocks.BLACKSTONE);

        whisk(output, ModItems.WHISK_BIRCH.get(), Blocks.BIRCH_LEAVES);
        whisk(output, ModItems.WHISK_OAK.get(), Blocks.OAK_LEAVES);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STOVE_CASING.get(), 4)
                .pattern("BS")
                .pattern("SB")
                .define('B', Items.BRICK)
                .define('S', Blocks.STONE)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CHIMNEY.get(), 4)
                .pattern("BB")
                .pattern("BB")
                .define('B', Items.BRICK)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.DAMPER.get())
                .pattern("I")
                .pattern("C")
                .define('I', Items.IRON_INGOT)
                .define('C', ModBlocks.CHIMNEY.get())
                .unlockedBy("has_chimney", has(ModBlocks.CHIMNEY.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.THERMOMETER.get())
                .pattern("GI")
                .pattern("GI")
                .define('G', Blocks.GLASS)
                .define('I', Items.IRON_NUGGET)
                .unlockedBy("has_glass", has(Blocks.GLASS))
                .save(output);
    }

    private static void saunaStone(RecipeOutput output, ItemLike result, ItemLike... sources) {
        for (ItemLike source : sources) {
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(source), RecipeCategory.MISC, result, 2)
                    .unlockedBy("has_stone", has(source))
                    .save(output, ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID,
                            getItemName(result) + "_from_" + getItemName(source)));
        }
    }

    private static void whisk(RecipeOutput output, ItemLike result, ItemLike leaves) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result)
                .pattern("LLL")
                .pattern("LLL")
                .pattern("SKS")
                .define('L', leaves)
                .define('S', Items.STICK)
                .define('K', Items.STRING)
                .unlockedBy("has_leaves", has(leaves))
                .save(output);
    }
}
