package com.banya.item;

import com.banya.registry.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

/**
 * Split firewood. Freshly split wood is damp: it burns short and cool until it has spent time on a
 * drying rack, which is the whole point of the woodshed.
 *
 * <p>Burn time is reported straight from the item, so the stove (and any vanilla furnace) sees the
 * species and dryness without extra plumbing.
 */
public class FirewoodItem extends Item {
    private final WoodSpecies species;

    public FirewoodItem(WoodSpecies species, Properties properties) {
        super(properties);
        this.species = species;
    }

    public WoodSpecies species() {
        return this.species;
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return this.species.burnTicks(isDry(stack));
    }

    /** How hard this piece drives the stove while it burns. */
    public double heatFactor(ItemStack stack) {
        return this.species.heatFactor(isDry(stack));
    }

    /**
     * Dryness is part of what the item <em>is</em>, so it belongs in the name rather than on a
     * separate tooltip line: "Dry Birch Firewood", not "Birch Firewood" plus a note underneath.
     */
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack) + (isDry(stack) ? ".dry" : ".wet"));
    }

    public static boolean isDry(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.DRY.get(), false);
    }

    public static void setDry(ItemStack stack, boolean dry) {
        stack.set(ModDataComponents.DRY.get(), dry);
    }
}
