package com.banya.registry;

import com.banya.Banya;
import com.banya.item.FeltHatItem;
import com.banya.item.LadleItem;
import com.banya.item.VenikItem;
import com.banya.item.VenikSpecies;
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
    public static final DeferredItem<BlockItem> TUB = REGISTER.registerSimpleBlockItem(ModBlocks.TUB);
    public static final DeferredItem<BlockItem> POLOK = REGISTER.registerSimpleBlockItem(ModBlocks.POLOK);
    public static final DeferredItem<BlockItem> BANYA_DOOR = REGISTER.registerSimpleBlockItem(ModBlocks.BANYA_DOOR);

    /** Head-slot cosmetic that slows Warmth gain in a hot parnaya. */
    public static final DeferredItem<FeltHatItem> FELT_HAT = REGISTER.registerItem(
            "felt_hat", FeltHatItem::new, new Item.Properties().stacksTo(1));

    /** Scoops water and throws it on the stones (поддача). */
    public static final DeferredItem<LadleItem> LADLE = REGISTER.registerItem(
            "ladle", LadleItem::new, new Item.Properties().stacksTo(1));

    /** Birch venik — the all-rounder; leaves fall off with use, hence the durability. */
    public static final DeferredItem<VenikItem> VENIK_BIRCH = REGISTER.registerItem(
            "venik_birch",
            properties -> new VenikItem(VenikSpecies.BIRCH, properties),
            new Item.Properties().stacksTo(1).durability(48));

    /** Oak venik — sturdier bundle. */
    public static final DeferredItem<VenikItem> VENIK_OAK = REGISTER.registerItem(
            "venik_oak",
            properties -> new VenikItem(VenikSpecies.OAK, properties),
            new Item.Properties().stacksTo(1).durability(64));

    private ModItems() {}
}
