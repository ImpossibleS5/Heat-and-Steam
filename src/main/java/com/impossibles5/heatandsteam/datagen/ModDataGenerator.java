package com.impossibles5.heatandsteam.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ModDataGenerator {
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModLanguageProvider(output, "en_us"));
        generator.addProvider(event.includeClient(), new ModLanguageProvider(output, "ru_ru"));
        generator.addProvider(event.includeClient(), new ModSoundProvider(output, existingFileHelper));

        ModBlockTagProvider blockTags = new ModBlockTagProvider(output, lookup, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(),
                new ModItemTagProvider(output, lookup, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(),
                new ModFluidTagProvider(output, lookup, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookup));

        ModDatapackProvider datapack = new ModDatapackProvider(output, lookup);
        generator.addProvider(event.includeServer(), datapack);
        generator.addProvider(event.includeServer(),
                new ModDamageTypeTagProvider(output, datapack.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(output, lookup));
        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookup,
                existingFileHelper, List.of(new ModAdvancementProvider())));
    }

    private ModDataGenerator() {}
}
