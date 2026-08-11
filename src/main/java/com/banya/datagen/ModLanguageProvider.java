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
            add(ModBlocks.TUB.get(), "Ушат");
            add(ModItems.VENIK_BIRCH.get(), "Берёзовый веник");
            add(ModItems.VENIK_OAK.get(), "Дубовый веник");
            add("tooltip.banya.venik.dry", "Сухой — нужно запарить в ушате");
            add("tooltip.banya.venik.steeped", "Запаренный (осталось применений: %s)");
            add("message.banya.tub.cold", "Вода в ушате ещё холодная — протопи баню");
            add("message.banya.venik.dry", "Сухой веник крошится — запарь его в ушате");
            add("message.banya.venik.too_cold", "Париться можно только в жаркой парной");
            add("message.banya.venik.received", "%s парит вас веником");
            add("message.banya.venik.dried_out", "Веник высох — запарь его снова");
            add("effect.banya.hardening", "Закалка");
            add("message.banya.hardening", "Закалка! Цикл %s");
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
            add(ModBlocks.TUB.get(), "Banya Tub");
            add(ModItems.VENIK_BIRCH.get(), "Birch Venik");
            add(ModItems.VENIK_OAK.get(), "Oak Venik");
            add("tooltip.banya.venik.dry", "Dry — steep it in a tub first");
            add("tooltip.banya.venik.steeped", "Steeped (%s whisks left)");
            add("message.banya.tub.cold", "The tub water is still cold — heat the banya first");
            add("message.banya.venik.dry", "A dry venik just crumbles — steep it in a tub");
            add("message.banya.venik.too_cold", "You can only whisk in a hot parnaya");
            add("message.banya.venik.received", "%s is whisking you with a venik");
            add("message.banya.venik.dried_out", "The venik has dried out — steep it again");
            add("effect.banya.hardening", "Hardening");
            add("message.banya.hardening", "Hardening! Cycle %s");
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
