package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public final class ModTags {
    public static final class Blocks {
        public static final TagKey<Block> INSULATION_TIER_1 = create("insulation/tier_1");

        public static final TagKey<Block> INSULATION_TIER_2 = create("insulation/tier_2");

        public static final TagKey<Block> INSULATION_TIER_3 = create("insulation/tier_3");

        public static final List<TagKey<Block>> INSULATION =
                List.of(INSULATION_TIER_1, INSULATION_TIER_2, INSULATION_TIER_3);

        public static final TagKey<Block> ORES_TALCOCHLORITE = common("ores/talcochlorite");

        private static TagKey<Block> create(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, path));
        }

        private static TagKey<Block> common(String path) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }

        private Blocks() {}
    }

    public static final class Items {
        public static final TagKey<Item> FIREWOOD = create("firewood");

        public static final TagKey<Item> STONES_TIER_1 = create("stones/tier_1");

        public static final TagKey<Item> STONES_TIER_2 = create("stones/tier_2");

        public static final TagKey<Item> STONES_TIER_3 = create("stones/tier_3");

        public static final TagKey<Item> STONES_TIER_4 = create("stones/tier_4");

        public static final List<TagKey<Item>> STONES =
                List.of(STONES_TIER_1, STONES_TIER_2, STONES_TIER_3, STONES_TIER_4);

        public static final TagKey<Item> ORES_TALCOCHLORITE = common("ores/talcochlorite");

        private static TagKey<Item> create(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, path));
        }

        private static TagKey<Item> common(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }

        private Items() {}
    }

    public static final class Fluids {
        public static final TagKey<Fluid> COOLS_STONES = create("cools_stones");

        private static TagKey<Fluid> create(String path) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, path));
        }

        private Fluids() {}
    }

    private ModTags() {}
}
