package com.banya.registry;

import com.banya.Banya;
import com.banya.stove.StoveMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/** Menu type registry. Screens are bound client-side in {@code client/}. */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, Banya.MODID);

    public static final Supplier<MenuType<StoveMenu>> STOVE = REGISTER.register(
            "stove", () -> new MenuType<>(StoveMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {}
}
