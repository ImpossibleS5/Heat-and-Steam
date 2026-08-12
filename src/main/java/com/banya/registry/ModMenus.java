package com.banya.registry;

import com.banya.Banya;
import com.banya.stove.StoveMenu;
import com.banya.stove.ThermometerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/** Menu type registry. Screens are bound client-side in {@code client/}. */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, Banya.MODID);

    /**
     * Built with extra data so the screen is opened knowing the stove's tier, and can lay out
     * exactly the basket slots that stove has — rather than always eight with some struck out.
     */
    public static final Supplier<MenuType<StoveMenu>> STOVE = REGISTER.register(
            "stove", () -> IMenuTypeExtension.create(StoveMenu::new));

    /** The thermometer's readout screen; it has no slots, only synced climate values. */
    public static final Supplier<MenuType<ThermometerMenu>> THERMOMETER = REGISTER.register(
            "thermometer", () -> IMenuTypeExtension.create(ThermometerMenu::new));

    private ModMenus() {}
}
