package com.impossibles5.heatandsteam.datagen;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModEffects;
import com.impossibles5.heatandsteam.registry.ModItems;
import com.impossibles5.heatandsteam.registry.ModTriggers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.function.Consumer;

public class ModAdvancementProvider implements AdvancementGenerator {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/block/spruce_planks.png");

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver,
                         ExistingFileHelper existingFileHelper) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(ModBlocks.STOVE.get(),
                        title("root"), description("root"),
                        BACKGROUND, AdvancementType.TASK, true, true, false)
                .addCriterion("warmed", criterion(ModTriggers.FIRST_STEAM.get()))
                .save(saver, id("root"));

        AdvancementHolder lightSteam = child(saver, root, ModItems.WHISK_BIRCH.get(), "light_steam",
                AdvancementType.GOAL, criterion(ModTriggers.LIGHT_STEAM.get()));

        child(saver, lightSteam, Items.CHARCOAL, "smoke_sauna",
                AdvancementType.CHALLENGE, criterion(ModTriggers.SMOKE_SAUNA.get()));

        child(saver, root, ModItems.FELT_HAT.get(), "walrus", AdvancementType.CHALLENGE,
                EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects()
                        .and(ModEffects.HARDENING.getDelegate(),
                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                        MinMaxBounds.Ints.atLeast(2), MinMaxBounds.Ints.ANY,
                                        Optional.empty(), Optional.empty()))));

        child(saver, root, ModBlocks.DAMPER.get(), "choked",
                AdvancementType.TASK, criterion(ModTriggers.CHOKED.get()));
    }

    private AdvancementHolder child(Consumer<AdvancementHolder> saver, AdvancementHolder parent,
                                    ItemLike icon, String name, AdvancementType type,
                                    net.minecraft.advancements.Criterion<?> criterion) {
        return Advancement.Builder.advancement()
                .parent(parent)
                .display(icon, title(name), description(name), null, type, true, true, false)
                .addCriterion(name, criterion)
                .save(saver, id(name));
    }

    private static net.minecraft.advancements.Criterion<PlayerTrigger.TriggerInstance> criterion(
            PlayerTrigger trigger) {
        return trigger.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }

    private static Component title(String name) {
        return Component.translatable("advancements." + HeatAndSteam.MODID + "." + name + ".title");
    }

    private static Component description(String name) {
        return Component.translatable("advancements." + HeatAndSteam.MODID + "." + name + ".description");
    }

    private static String id(String name) {
        return HeatAndSteam.MODID + ":" + name;
    }
}
