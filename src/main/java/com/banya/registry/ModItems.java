package com.banya.registry;

import com.banya.Banya;
import com.banya.item.FeltHatItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Item registry, including the {@code BlockItem}s for blocks declared in {@link ModBlocks}.
 */
public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(Banya.MODID);

    public static final DeferredItem<BlockItem> STOVE = REGISTER.registerSimpleBlockItem(ModBlocks.STOVE);
    public static final DeferredItem<BlockItem> THERMOMETER = REGISTER.registerSimpleBlockItem(ModBlocks.THERMOMETER);

    /** Head-slot cosmetic that slows Warmth gain in a hot parnaya. */
    public static final DeferredItem<FeltHatItem> FELT_HAT = REGISTER.registerItem(
            "felt_hat", FeltHatItem::new, new Item.Properties().stacksTo(1));

    private ModItems() {}
}
