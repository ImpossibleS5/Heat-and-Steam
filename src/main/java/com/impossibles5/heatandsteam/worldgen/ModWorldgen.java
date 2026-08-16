package com.impossibles5.heatandsteam.worldgen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public final class ModWorldgen {
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALCOCHLORITE_ORE = configuredFeature("talcochlorite_ore");
    public static final ResourceKey<PlacedFeature> TALCOCHLORITE_ORE_PLACED = placedFeature("talcochlorite_ore");
    public static final ResourceKey<BiomeModifier> ADD_TALCOCHLORITE_ORE = biomeModifier("add_talcochlorite_ore");

    private static final int VEIN_SIZE = 6;

    private static final int VEINS_PER_CHUNK = 3;
    private static final int MIN_HEIGHT = 40;
    private static final int MAX_HEIGHT = 90;

    private ModWorldgen() {}

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest inStone = new BlockMatchTest(net.minecraft.world.level.block.Blocks.STONE);
        context.register(TALCOCHLORITE_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                List.of(OreConfiguration.target(inStone, ModBlocks.TALCOCHLORITE_ORE.get().defaultBlockState())),
                VEIN_SIZE)));
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        context.register(TALCOCHLORITE_ORE_PLACED, new PlacedFeature(
                context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(TALCOCHLORITE_ORE),
                List.of(
                        CountPlacement.of(VEINS_PER_CHUNK),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(MIN_HEIGHT), VerticalAnchor.absolute(MAX_HEIGHT)))));
    }

    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderSet<Biome> mountains = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_MOUNTAIN);
        context.register(ADD_TALCOCHLORITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                mountains,
                HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(TALCOCHLORITE_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeature(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, id(name));
    }

    private static ResourceKey<PlacedFeature> placedFeature(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, id(name));
    }

    private static ResourceKey<BiomeModifier> biomeModifier(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, id(name));
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, name);
    }
}
