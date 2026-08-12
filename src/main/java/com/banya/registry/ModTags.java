package com.banya.registry;

import com.banya.Banya;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Tag keys owned by this mod. Insulation is expressed as tiers so KubeJS and third-party mods can
 * classify their own building blocks without a code change.
 */
public final class ModTags {

    public static final class Blocks {
        /** Best retention — logs and other thick timber. */
        public static final TagKey<Block> INSULATION_HIGH = create("insulation/high");
        /** Middling retention — planks and similar. */
        public static final TagKey<Block> INSULATION_MID = create("insulation/mid");
        /** Poor retention — stone, brick, glass. */
        public static final TagKey<Block> INSULATION_LOW = create("insulation/low");

        private static TagKey<Block> create(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Banya.MODID, path));
        }

        private Blocks() {}
    }

    /**
     * Stones the каменка accepts, by how well they hold heat. Tags rather than dedicated items so
     * KubeJS and other mods can contribute their own rock without a code change.
     */
    public static final class Items {
        /**
         * What the stove accepts as fuel. A banya is fired with split firewood, not with coal or
         * fence posts — but it stays a tag so packs and other mods can qualify their own wood.
         */
        public static final TagKey<Item> FIREWOOD = create("firewood");

        /** Common rock — heats fast, holds little. */
        public static final TagKey<Item> STONES_LOW = create("stones/low");
        /** Better retention. */
        public static final TagKey<Item> STONES_MID = create("stones/mid");
        /** Dense volcanic rock. */
        public static final TagKey<Item> STONES_HIGH = create("stones/high");
        /** Soapstone and its equals — the banya stone proper. */
        public static final TagKey<Item> STONES_BEST = create("stones/best");

        private static TagKey<Item> create(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Banya.MODID, path));
        }

        private Items() {}
    }

    public static final class Fluids {
        /**
         * What a hot stone can be quenched in. Water by default; lava deliberately is not in it, and
         * a tag rather than a hardcoded check so a pack or another mod can qualify its own coolant.
         */
        public static final TagKey<Fluid> COOLS_STONES = create("cools_stones");

        private static TagKey<Fluid> create(String path) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Banya.MODID, path));
        }

        private Fluids() {}
    }

    private ModTags() {}
}
