package com.banya.registry;

import com.banya.Banya;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

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

    private ModTags() {}
}
