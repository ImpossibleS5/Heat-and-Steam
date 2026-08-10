package com.banya.registry;

import com.banya.Banya;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Item registry, including the {@code BlockItem}s for blocks declared in {@link ModBlocks}.
 * Standalone items (felt hat, ladle, veniks) are added in later sub-slices.
 */
public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(Banya.MODID);

    public static final DeferredItem<BlockItem> STOVE = REGISTER.registerSimpleBlockItem(ModBlocks.STOVE);
    public static final DeferredItem<BlockItem> THERMOMETER = REGISTER.registerSimpleBlockItem(ModBlocks.THERMOMETER);

    private ModItems() {}
}
