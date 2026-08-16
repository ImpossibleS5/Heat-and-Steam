package com.impossibles5.heatandsteam.item;

import com.impossibles5.heatandsteam.registry.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

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

    public double heatFactor(ItemStack stack) {
        return this.species.heatFactor(isDry(stack));
    }

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
