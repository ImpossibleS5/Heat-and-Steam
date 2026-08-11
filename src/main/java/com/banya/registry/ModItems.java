package com.banya.registry;

import com.banya.Banya;
import com.banya.item.FeltHatItem;
import com.banya.item.FirewoodItem;
import com.banya.item.LadleItem;
import com.banya.item.VenikItem;
import com.banya.item.VenikSpecies;
import com.banya.item.WoodSpecies;
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

    // Banya stones are items rather than raw building blocks, the same way firewood is: what goes
    // in the basket is prepared stone, and each tier is one recognisable item.
    /** Field and river stone — what you start with. */
    public static final DeferredItem<Item> RIVER_STONE = REGISTER.registerSimpleItem("river_stone");
    /** Denser rock, holds heat better. */
    public static final DeferredItem<Item> ANDESITE_STONE = REGISTER.registerSimpleItem("andesite_stone");
    /** Volcanic rock, the best you will find without a mountain expedition. */
    public static final DeferredItem<Item> BASALT_STONE = REGISTER.registerSimpleItem("basalt_stone");
    /** Soapstone: the best banya stone there is, and the reason to go up the mountains. */
    public static final DeferredItem<Item> SOAPSTONE = REGISTER.registerSimpleItem("soapstone");

    /** Scoops water and throws it on the stones (поддача). */
    public static final DeferredItem<LadleItem> LADLE = REGISTER.registerItem(
            "ladle", LadleItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<BlockItem> SOAPSTONE_ORE = REGISTER.registerSimpleBlockItem(ModBlocks.SOAPSTONE_ORE);
    public static final DeferredItem<BlockItem> CHIMNEY = REGISTER.registerSimpleBlockItem(ModBlocks.CHIMNEY);
    public static final DeferredItem<BlockItem> DAMPER = REGISTER.registerSimpleBlockItem(ModBlocks.DAMPER);
    public static final DeferredItem<BlockItem> CHOPPING_BLOCK = REGISTER.registerSimpleBlockItem(ModBlocks.CHOPPING_BLOCK);
    public static final DeferredItem<BlockItem> DRYING_RACK = REGISTER.registerSimpleBlockItem(ModBlocks.DRYING_RACK);

    /** Firewood: the stove's proper fuel, damp until it has dried on a rack. */
    public static final DeferredItem<FirewoodItem> FIREWOOD_BIRCH = REGISTER.registerItem(
            "firewood_birch", properties -> new FirewoodItem(WoodSpecies.BIRCH, properties), new Item.Properties());

    public static final DeferredItem<FirewoodItem> FIREWOOD_OAK = REGISTER.registerItem(
            "firewood_oak", properties -> new FirewoodItem(WoodSpecies.OAK, properties), new Item.Properties());

    public static final DeferredItem<FirewoodItem> FIREWOOD_SPRUCE = REGISTER.registerItem(
            "firewood_spruce", properties -> new FirewoodItem(WoodSpecies.SPRUCE, properties), new Item.Properties());

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
