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
            add(ModBlocks.POLOK.get(), "Полок");
            add(ModBlocks.BANYA_DOOR.get(), "Банная дверь");
            add(ModBlocks.SOAPSTONE_ORE.get(), "Талькохлоритовая руда");
            add(ModBlocks.SOOTY_PLANKS.get(), "Закопчённые доски");
            add(ModBlocks.SOOTY_LOG.get(), "Закопчённое бревно");
            add(ModBlocks.CHIMNEY.get(), "Дымоход");
            add(ModBlocks.DAMPER.get(), "Заслонка");
            add(ModItems.RIVER_STONE.get(), "Речные голыши");
            add(ModItems.ANDESITE_STONE.get(), "Андезитовые камни");
            add(ModItems.BASALT_STONE.get(), "Базальтовые камни");
            add(ModItems.SOAPSTONE.get(), "Талькохлорит");
            add("message.banya.stone.cracked", "Камень треснул");
            add(ModBlocks.CHOPPING_BLOCK.get(), "Колода для колки");
            add(ModBlocks.DRYING_RACK.get(), "Дровница");
            add(ModItems.FIREWOOD_BIRCH.get(), "Берёзовые поленья");
            add(ModItems.FIREWOOD_OAK.get(), "Дубовые поленья");
            add(ModItems.FIREWOOD_SPRUCE.get(), "Еловые поленья");
            add("tooltip.banya.firewood.wet", "Сырые");
            add("tooltip.banya.firewood.dry", "Сухие");
            add("message.banya.faint", "В глазах потемнело…");
            add(ModItems.VENIK_BIRCH.get(), "Берёзовый веник");
            add(ModItems.VENIK_OAK.get(), "Дубовый веник");
            add("tooltip.banya.venik.dry", "Сухой");
            add("tooltip.banya.venik.steeped", "Запаренный");
            add("message.banya.tub.cold", "Вода в ушате ещё холодная — протопи баню");
            add("message.banya.venik.dry", "Сухой веник крошится — запарь его в ушате");
            add("message.banya.venik.too_cold", "Париться можно только в жаркой парной");
            add("message.banya.venik.received", "%s парит вас веником");
            add("message.banya.venik.dried_out", "Веник высох — запарь его снова");
            add("effect.banya.hardening", "Закалка");
            add("message.banya.hardening", "Закалка!");
            add("container.banya.stove", "Печь-каменка");
            add("container.banya.stove.temperature", "%s °C");
            add("container.banya.stove.humidity", "влажность %s%%");
            add("container.banya.stove.smoke", "дым %s%%");
            add("message.banya.thermometer.smoke", "· дым %s%%");
            add("effect.banya.smoke_poisoning", "Угар");
            add("message.banya.thermometer.dry", "%s °C · влажность %s%%");
            add("message.banya.thermometer.humid", "%s °C · влажность %s%% · как %s °C");
            add("message.banya.thermometer.leaking", "не замкнуто");
            add("message.banya.thermometer.no_stove", "Печь не найдена");
            add("message.banya.overheat", "Голова кружится — пора выйти");
            add("message.banya.steam.heavy", "Тяжёлый пар — камни ещё не раскалились");
            add("hud.banya.warmth", "Прогрев %s");
        } else {
            add("itemGroup.banya", "Banya");
            add(ModBlocks.STOVE.get(), "Banya Stove");
            add(ModBlocks.THERMOMETER.get(), "Thermometer");
            add(ModItems.FELT_HAT.get(), "Felt Banya Hat");
            add(ModItems.LADLE.get(), "Ladle");
            add(ModBlocks.TUB.get(), "Banya Tub");
            add(ModBlocks.POLOK.get(), "Polok Bench");
            add(ModBlocks.BANYA_DOOR.get(), "Banya Door");
            add(ModBlocks.SOAPSTONE_ORE.get(), "Soapstone Ore");
            add(ModBlocks.SOOTY_PLANKS.get(), "Sooty Planks");
            add(ModBlocks.SOOTY_LOG.get(), "Sooty Log");
            add(ModBlocks.CHIMNEY.get(), "Chimney");
            add(ModBlocks.DAMPER.get(), "Damper");
            add(ModItems.RIVER_STONE.get(), "River Stones");
            add(ModItems.ANDESITE_STONE.get(), "Andesite Stones");
            add(ModItems.BASALT_STONE.get(), "Basalt Stones");
            add(ModItems.SOAPSTONE.get(), "Soapstone");
            add("message.banya.stone.cracked", "A stone cracked");
            add(ModBlocks.CHOPPING_BLOCK.get(), "Chopping Block");
            add(ModBlocks.DRYING_RACK.get(), "Drying Rack");
            add(ModItems.FIREWOOD_BIRCH.get(), "Birch Firewood");
            add(ModItems.FIREWOOD_OAK.get(), "Oak Firewood");
            add(ModItems.FIREWOOD_SPRUCE.get(), "Spruce Firewood");
            add("tooltip.banya.firewood.wet", "Damp");
            add("tooltip.banya.firewood.dry", "Dry");
            add("message.banya.faint", "Everything goes dark…");
            add(ModItems.VENIK_BIRCH.get(), "Birch Venik");
            add(ModItems.VENIK_OAK.get(), "Oak Venik");
            add("tooltip.banya.venik.dry", "Dry");
            add("tooltip.banya.venik.steeped", "Steeped");
            add("message.banya.tub.cold", "The tub water is still cold — heat the banya first");
            add("message.banya.venik.dry", "A dry venik just crumbles — steep it in a tub");
            add("message.banya.venik.too_cold", "You can only whisk in a hot parnaya");
            add("message.banya.venik.received", "%s is whisking you with a venik");
            add("message.banya.venik.dried_out", "The venik has dried out — steep it again");
            add("effect.banya.hardening", "Hardening");
            add("message.banya.hardening", "Hardening!");
            add("container.banya.stove", "Banya Stove");
            add("container.banya.stove.temperature", "%s °C");
            add("container.banya.stove.humidity", "humidity %s%%");
            add("container.banya.stove.smoke", "smoke %s%%");
            add("message.banya.thermometer.smoke", "· smoke %s%%");
            add("effect.banya.smoke_poisoning", "Smoke Poisoning");
            add("message.banya.thermometer.dry", "%s °C · humidity %s%%");
            add("message.banya.thermometer.humid", "%s °C · humidity %s%% · like %s °C");
            add("message.banya.thermometer.leaking", "not sealed");
            add("message.banya.thermometer.no_stove", "No stove nearby");
            add("message.banya.overheat", "Your head is swimming — time to step out");
            add("message.banya.steam.heavy", "Heavy steam — the stones are not hot enough yet");
            add("hud.banya.warmth", "Warmth: %s");
        }
    }
}
