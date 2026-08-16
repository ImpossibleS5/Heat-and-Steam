package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.item.FirewoodItem;
import com.impossibles5.heatandsteam.stove.StoveStones;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HeatAndSteam.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = REGISTER.register(
            "heat_and_steam",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.heat_and_steam"))
                    .icon(() -> ModItems.STOVE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STOVE.get());
                        output.accept(ModItems.STOVE_CASING.get());
                        output.accept(ModItems.CHIMNEY.get());
                        output.accept(ModItems.DAMPER.get());
                        output.accept(ModItems.THERMOMETER.get());
                        output.accept(ModItems.TUB.get());
                        output.accept(ModItems.SAUNA_BENCH.get());
                        output.accept(ModItems.SAUNA_DOOR.get());
                        output.accept(ModItems.TALCOCHLORITE_ORE.get());

                        for (var stone : List.of(ModItems.RIVER_STONE, ModItems.ANDESITE_STONE,
                                ModItems.BASALT_STONE, ModItems.TALCOCHLORITE_STONE)) {
                            for (int cracks = 0; cracks < StoveStones.MAX_CRACKS; cracks++) {
                                output.accept(cracked(stone.get(), cracks));
                            }
                        }
                        output.accept(ModItems.CHOPPING_BLOCK.get());
                        output.accept(ModItems.DRYING_RACK.get());

                        for (var firewood : List.of(ModItems.FIREWOOD_BIRCH, ModItems.FIREWOOD_OAK,
                                ModItems.FIREWOOD_SPRUCE)) {
                            output.accept(firewood.get());
                            output.accept(dried(firewood.get()));
                        }
                        output.accept(ModItems.FELT_HAT.get());
                        output.accept(ModItems.LADLE.get());

                        output.accept(ModItems.WHISK_BIRCH.get());
                        output.accept(ModItems.WHISK_OAK.get());
                    })
                    .build());

    private static ItemStack dried(ItemLike item) {
        ItemStack stack = new ItemStack(item);
        FirewoodItem.setDry(stack, true);
        return stack;
    }

    private static ItemStack cracked(ItemLike item, int cracks) {
        ItemStack stack = new ItemStack(item);
        if (cracks > 0) {
            stack.set(ModDataComponents.CRACKS.get(), cracks);
        }
        return stack;
    }

    private ModCreativeTabs() {}
}
