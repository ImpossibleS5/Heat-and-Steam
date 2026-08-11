package com.banya.registry;

import com.banya.Banya;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Creative tab registry. The single "banya" tab collects all mod content.
 * New items are appended to {@code displayItems} as they are registered in later sub-slices.
 */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Banya.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BANYA_TAB = REGISTER.register(
            "banya",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.banya"))
                    .icon(() -> ModItems.STOVE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STOVE.get());
                        output.accept(ModItems.THERMOMETER.get());
                        output.accept(ModItems.TUB.get());
                        output.accept(ModItems.POLOK.get());
                        output.accept(ModItems.BANYA_DOOR.get());
                        output.accept(ModItems.CHOPPING_BLOCK.get());
                        output.accept(ModItems.DRYING_RACK.get());
                        output.accept(ModItems.FIREWOOD_BIRCH.get());
                        output.accept(ModItems.FIREWOOD_OAK.get());
                        output.accept(ModItems.FIREWOOD_SPRUCE.get());
                        output.accept(ModItems.FELT_HAT.get());
                        output.accept(ModItems.LADLE.get());
                        output.accept(ModItems.VENIK_BIRCH.get());
                        output.accept(ModItems.VENIK_OAK.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}
