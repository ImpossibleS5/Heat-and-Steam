package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModBlocks;
import com.banya.registry.ModEffects;
import com.banya.registry.ModItems;
import com.banya.registry.ModTriggers;
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

/**
 * The banya's five advancements, from the concept.
 *
 * <p>Four of them hang off {@link ModTriggers}; «Морж» needs no trigger of ours at all, since three
 * contrast cycles are exactly Закалка III and vanilla can already ask about an effect's amplifier.
 */
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

        AdvancementHolder lightSteam = child(saver, root, ModItems.VENIK_BIRCH.get(), "light_steam",
                AdvancementType.GOAL, criterion(ModTriggers.LIGHT_STEAM.get()));

        // Charcoal rather than the sooty planks themselves: soot is world-made, never an item, so
        // those blocks have no icon to draw.
        child(saver, lightSteam, Items.CHARCOAL, "black_banya",
                AdvancementType.CHALLENGE, criterion(ModTriggers.BLACK_BANYA.get()));

        // Закалка III is the third cycle of the same session, which is what "морж" means here.
        child(saver, root, ModItems.FELT_HAT.get(), "walrus", AdvancementType.CHALLENGE,
                EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects()
                        .and(ModEffects.HARDENING.getDelegate(),
                                new MobEffectsPredicate.MobEffectInstancePredicate(
                                        MinMaxBounds.Ints.atLeast(2), MinMaxBounds.Ints.ANY,
                                        Optional.empty(), Optional.empty()))));

        child(saver, root, ModBlocks.CHIMNEY.get(), "choked",
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
        return Component.translatable("advancements." + Banya.MODID + "." + name + ".title");
    }

    private static Component description(String name) {
        return Component.translatable("advancements." + Banya.MODID + "." + name + ".description");
    }

    private static String id(String name) {
        return Banya.MODID + ":" + name;
    }
}
