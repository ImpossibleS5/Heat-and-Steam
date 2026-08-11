package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModBlocks;
import com.banya.registry.ModItems;
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

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.POLOK.get(), 4)
                .pattern("PPP")
                .pattern("S S")
                .define('P', ItemTags.PLANKS)
                .define('S', Items.STICK)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.BANYA_DOOR.get(), 2)
                .pattern("LL")
                .pattern("LL")
                .pattern("LL")
                .define('L', ItemTags.LOGS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(output);

        banyaStone(output, ModItems.RIVER_STONE.get(),
                Blocks.COBBLESTONE, Blocks.STONE, Blocks.GRANITE);
        banyaStone(output, ModItems.ANDESITE_STONE.get(),
                Blocks.ANDESITE, Blocks.DIORITE, Blocks.DEEPSLATE);
        banyaStone(output, ModItems.BASALT_STONE.get(),
                Blocks.BASALT, Blocks.SMOOTH_BASALT, Blocks.BLACKSTONE);

        venik(output, ModItems.VENIK_BIRCH.get(), Blocks.BIRCH_LEAVES);
        venik(output, ModItems.VENIK_OAK.get(), Blocks.OAK_LEAVES);

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

    /** Rock is cut down to banya stones; any of the listed blocks yields the same tier. */
    private static void banyaStone(RecipeOutput output, ItemLike result, ItemLike... sources) {
        for (ItemLike source : sources) {
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(source), RecipeCategory.MISC, result, 2)
                    .unlockedBy("has_stone", has(source))
                    .save(output, ResourceLocation.fromNamespaceAndPath(Banya.MODID,
                            getItemName(result) + "_from_" + getItemName(source)));
        }
    }

    /** Six leaves bound to two sticks with string — the standard venik. */
    private static void venik(RecipeOutput output, ItemLike result, ItemLike leaves) {
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
