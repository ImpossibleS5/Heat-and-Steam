package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.stove.StoveMenu;
import com.impossibles5.heatandsteam.stove.ThermometerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, HeatAndSteam.MODID);

    public static final Supplier<MenuType<StoveMenu>> STOVE = REGISTER.register(
            "stove", () -> IMenuTypeExtension.create(StoveMenu::new));

    public static final Supplier<MenuType<ThermometerMenu>> THERMOMETER = REGISTER.register(
            "thermometer", () -> IMenuTypeExtension.create(ThermometerMenu::new));

    private ModMenus() {}
}
