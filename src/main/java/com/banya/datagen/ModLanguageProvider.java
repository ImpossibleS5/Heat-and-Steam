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
            add(ModBlocks.STOVE.get(), "Печь-каменка");
            add(ModBlocks.THERMOMETER.get(), "Термометр");
            add(ModItems.FELT_HAT.get(), "Банная шапка");
            add(ModItems.LADLE.get(), "Ковш");
            add(ModBlocks.TUB.get(), "Ушат");
            add(ModBlocks.POLOK.get(), "Полок");
            add(ModBlocks.BANYA_DOOR.get(), "Банная дверь");
            add(ModBlocks.SOAPSTONE_ORE.get(), "Талькохлоритовая руда");
            add(ModBlocks.SOOTY_PLANKS.get(), "Закопчённые доски");
            add(ModBlocks.SOOTY_LOG.get(), "Закопчённое бревно");
            add(ModBlocks.STOVE_CASING.get(), "Печная кладка");
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
            add("item.banya.firewood_birch.wet", "Сырые берёзовые поленья");
            add("item.banya.firewood_birch.dry", "Сухие берёзовые поленья");
            add("item.banya.firewood_oak.wet", "Сырые дубовые поленья");
            add("item.banya.firewood_oak.dry", "Сухие дубовые поленья");
            add("item.banya.firewood_spruce.wet", "Сырые еловые поленья");
            add("item.banya.firewood_spruce.dry", "Сухие еловые поленья");
            add("message.banya.faint", "В глазах потемнело…");
            add(ModItems.VENIK_BIRCH.get(), "Берёзовый веник");
            add(ModItems.VENIK_OAK.get(), "Дубовый веник");
            add("item.banya.venik_birch.steeped", "Запаренный берёзовый веник");
            add("item.banya.venik_oak.steeped", "Запаренный дубовый веник");
            add("message.banya.tub.cold", "Вода в ушате ещё холодная — протопи баню");
            add("message.banya.venik.dry", "Сухой веник крошится — запарь его в ушате");
            add("message.banya.venik.too_cold", "Париться можно только в жаркой парной");
            add("message.banya.venik.received", "%s парит вас веником");
            add("message.banya.venik.dried_out", "Веник высох — запарь его снова");
            add("effect.banya.hardening", "Закалка");
            add("message.banya.hardening", "Закалка!");
            add("container.banya.stove.t1", "Печь-каменка");
            add("container.banya.stove.t2", "Кирпичная каменка");
            add("container.banya.stove.t3", "Массивная печь");
            add("effect.banya.smoke_poisoning", "Угар");
            add("death.attack.banya.smoke_poisoning", "%1$s угорел в бане");
            add("container.banya.thermometer", "Термометр");
            add("gui.banya.thermometer.temperature", "Температура");
            add("gui.banya.thermometer.humidity", "Влажность");
            add("gui.banya.thermometer.heat_index", "Ощущается как");
            add("gui.banya.thermometer.smoke", "Задымлённость");
            add("gui.banya.thermometer.degrees", "%s °C");
            add("gui.banya.thermometer.percent", "%s%%");
            add("gui.banya.thermometer.sealed", "Парная замкнута");
            add("gui.banya.thermometer.leaking", "Парная не замкнута");
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
            add(ModBlocks.STOVE_CASING.get(), "Stove Casing");
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
            add("item.banya.firewood_birch.wet", "Damp Birch Firewood");
            add("item.banya.firewood_birch.dry", "Dry Birch Firewood");
            add("item.banya.firewood_oak.wet", "Damp Oak Firewood");
            add("item.banya.firewood_oak.dry", "Dry Oak Firewood");
            add("item.banya.firewood_spruce.wet", "Damp Spruce Firewood");
            add("item.banya.firewood_spruce.dry", "Dry Spruce Firewood");
            add("message.banya.faint", "Everything goes dark…");
            add(ModItems.VENIK_BIRCH.get(), "Birch Venik");
            add(ModItems.VENIK_OAK.get(), "Oak Venik");
            add("item.banya.venik_birch.steeped", "Steeped Birch Venik");
            add("item.banya.venik_oak.steeped", "Steeped Oak Venik");
            add("message.banya.tub.cold", "The tub water is still cold — heat the banya first");
            add("message.banya.venik.dry", "A dry venik just crumbles — steep it in a tub");
            add("message.banya.venik.too_cold", "You can only whisk in a hot parnaya");
            add("message.banya.venik.received", "%s is whisking you with a venik");
            add("message.banya.venik.dried_out", "The venik has dried out — steep it again");
            add("effect.banya.hardening", "Hardening");
            add("message.banya.hardening", "Hardening!");
            add("container.banya.stove.t1", "Banya Stove");
            add("container.banya.stove.t2", "Brick Kamenka");
            add("container.banya.stove.t3", "Massive Stove");
            add("effect.banya.smoke_poisoning", "Smoke Poisoning");
            add("death.attack.banya.smoke_poisoning", "%1$s suffocated in the banya's smoke");
            add("container.banya.thermometer", "Thermometer");
            add("gui.banya.thermometer.temperature", "Temperature");
            add("gui.banya.thermometer.humidity", "Humidity");
            add("gui.banya.thermometer.heat_index", "Feels like");
            add("gui.banya.thermometer.smoke", "Smoke");
            add("gui.banya.thermometer.degrees", "%s °C");
            add("gui.banya.thermometer.percent", "%s%%");
            add("gui.banya.thermometer.sealed", "Room is sealed");
            add("gui.banya.thermometer.leaking", "Room is not sealed");
            add("message.banya.thermometer.no_stove", "No stove nearby");
            add("message.banya.overheat", "Your head is swimming — time to step out");
            add("message.banya.steam.heavy", "Heavy steam — the stones are not hot enough yet");
            add("hud.banya.warmth", "Warmth: %s");
        }
    }
}
