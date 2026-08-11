package com.banya.datagen;

import com.banya.Banya;
import com.banya.registry.ModBlocks;
import com.banya.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/** Generates en_us and ru_ru lang files (one instance per locale). */
public class ModLanguageProvider extends LanguageProvider {
    private final String locale;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, Banya.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if (locale.equals("ru_ru")) {
            add("itemGroup.banya", "Баня");
            add(ModBlocks.STOVE.get(), "Печь-каменка (T1)");
            add(ModBlocks.THERMOMETER.get(), "Термометр");
            add(ModItems.FELT_HAT.get(), "Банная шапка");
            add(ModItems.LADLE.get(), "Ковш");
            add("container.banya.stove", "Печь-каменка");
            add("container.banya.stove.climate", "%s °C · %s%%");
            add("message.banya.thermometer.reading", "Температура: %s °C, влажность: %s%%");
            add("message.banya.thermometer.leaking", "(парная не замкнута)");
            add("message.banya.thermometer.no_stove", "Печь не найдена поблизости");
            add("message.banya.steam.heavy", "Тяжёлый пар — камни ещё не раскалились");
            add("hud.banya.warmth", "Прогрев: %s");
        } else {
            add("itemGroup.banya", "Banya");
            add(ModBlocks.STOVE.get(), "Banya Stove");
            add(ModBlocks.THERMOMETER.get(), "Thermometer");
            add(ModItems.FELT_HAT.get(), "Felt Banya Hat");
            add(ModItems.LADLE.get(), "Ladle");
            add("container.banya.stove", "Banya Stove");
            add("container.banya.stove.climate", "%s °C · %s%%");
            add("message.banya.thermometer.reading", "Temperature: %s °C, humidity: %s%%");
            add("message.banya.thermometer.leaking", "(room not sealed)");
            add("message.banya.thermometer.no_stove", "No stove nearby");
            add("message.banya.steam.heavy", "Heavy steam — the stones are not hot enough yet");
            add("hud.banya.warmth", "Warmth: %s");
        }
    }
}
