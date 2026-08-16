package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.item.FeltHatItem;
import com.impossibles5.heatandsteam.item.SaunaStoneItem;
import com.impossibles5.heatandsteam.item.FirewoodItem;
import com.impossibles5.heatandsteam.item.LadleItem;
import com.impossibles5.heatandsteam.item.WhiskItem;
import com.impossibles5.heatandsteam.item.WhiskSpecies;
import com.impossibles5.heatandsteam.item.WoodSpecies;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(HeatAndSteam.MODID);

    public static final DeferredItem<BlockItem> STOVE = REGISTER.registerSimpleBlockItem(ModBlocks.STOVE);
    public static final DeferredItem<BlockItem> THERMOMETER = REGISTER.registerSimpleBlockItem(ModBlocks.THERMOMETER);
    public static final DeferredItem<BlockItem> TUB = REGISTER.registerSimpleBlockItem(ModBlocks.TUB);
    public static final DeferredItem<BlockItem> SAUNA_BENCH = REGISTER.registerSimpleBlockItem(ModBlocks.SAUNA_BENCH);
    public static final DeferredItem<BlockItem> SAUNA_DOOR = REGISTER.registerSimpleBlockItem(ModBlocks.SAUNA_DOOR);

    public static final DeferredItem<FeltHatItem> FELT_HAT = REGISTER.registerItem(
            "felt_hat", FeltHatItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<SaunaStoneItem> RIVER_STONE = stone("river_stone");

    public static final DeferredItem<SaunaStoneItem> ANDESITE_STONE = stone("andesite_stone");

    public static final DeferredItem<SaunaStoneItem> BASALT_STONE = stone("basalt_stone");

    public static final DeferredItem<SaunaStoneItem> TALCOCHLORITE_STONE = stone("talcochlorite_stone");

    private static DeferredItem<SaunaStoneItem> stone(String name) {
        return REGISTER.registerItem(name, SaunaStoneItem::new, new Item.Properties().stacksTo(16));
    }

    public static final DeferredItem<LadleItem> LADLE = REGISTER.registerItem(
            "ladle", LadleItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<BlockItem> TALCOCHLORITE_ORE = REGISTER.registerSimpleBlockItem(ModBlocks.TALCOCHLORITE_ORE);
    public static final DeferredItem<BlockItem> STOVE_CASING = REGISTER.registerSimpleBlockItem(ModBlocks.STOVE_CASING);
    public static final DeferredItem<BlockItem> CHIMNEY = REGISTER.registerSimpleBlockItem(ModBlocks.CHIMNEY);
    public static final DeferredItem<BlockItem> DAMPER = REGISTER.registerSimpleBlockItem(ModBlocks.DAMPER);
    public static final DeferredItem<BlockItem> CHOPPING_BLOCK = REGISTER.registerSimpleBlockItem(ModBlocks.CHOPPING_BLOCK);
    public static final DeferredItem<BlockItem> DRYING_RACK = REGISTER.registerSimpleBlockItem(ModBlocks.DRYING_RACK);

    public static final DeferredItem<FirewoodItem> FIREWOOD_BIRCH = REGISTER.registerItem(
            "firewood_birch", properties -> new FirewoodItem(WoodSpecies.BIRCH, properties), new Item.Properties());

    public static final DeferredItem<FirewoodItem> FIREWOOD_OAK = REGISTER.registerItem(
            "firewood_oak", properties -> new FirewoodItem(WoodSpecies.OAK, properties), new Item.Properties());

    public static final DeferredItem<FirewoodItem> FIREWOOD_SPRUCE = REGISTER.registerItem(
            "firewood_spruce", properties -> new FirewoodItem(WoodSpecies.SPRUCE, properties), new Item.Properties());

    public static final DeferredItem<WhiskItem> WHISK_BIRCH = REGISTER.registerItem(
            "whisk_birch",
            properties -> new WhiskItem(WhiskSpecies.BIRCH, properties),
            new Item.Properties().stacksTo(1).durability(48));

    public static final DeferredItem<WhiskItem> WHISK_OAK = REGISTER.registerItem(
            "whisk_oak",
            properties -> new WhiskItem(WhiskSpecies.OAK, properties),
            new Item.Properties().stacksTo(1).durability(64));

    private ModItems() {}
}
