package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModDamageTypes;
import com.banya.worldgen.ModWorldgen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Generates every datapack registry entry the mod owns — worldgen and damage types alike.
 *
 * <p>One provider for the lot, not one per topic: the data generator keys providers by name and all
 * {@code DatapackBuiltinEntriesProvider}s answer to "Registries", so a second one is rejected
 * outright. A single {@link RegistrySetBuilder} is also what the API is shaped for, and it keeps
 * cross-registry references resolvable in one pass.
 */
public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModWorldgen::bootstrapConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, ModWorldgen::bootstrapPlacedFeatures)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModWorldgen::bootstrapBiomeModifiers)
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap);

    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Banya.MODID));
    }
}
