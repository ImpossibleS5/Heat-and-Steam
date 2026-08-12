package com.banya.registry;

import com.banya.Banya;
import com.banya.item.FirewoodItem;
import com.banya.stove.StoveStones;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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
                        output.accept(ModItems.STOVE_CASING.get());
                        output.accept(ModItems.CHIMNEY.get());
                        output.accept(ModItems.DAMPER.get());
                        output.accept(ModItems.THERMOMETER.get());
                        output.accept(ModItems.TUB.get());
                        output.accept(ModItems.POLOK.get());
                        output.accept(ModItems.BANYA_DOOR.get());
                        output.accept(ModItems.SOAPSTONE_ORE.get());
                        // Every cracking stage, so the wear can be looked at without pouring a
                        // hundred ladles to reach it.
                        for (var stone : List.of(ModItems.RIVER_STONE, ModItems.ANDESITE_STONE,
                                ModItems.BASALT_STONE, ModItems.SOAPSTONE)) {
                            for (int cracks = 0; cracks < StoveStones.MAX_CRACKS; cracks++) {
                                output.accept(cracked(stone.get(), cracks));
                            }
                        }
                        output.accept(ModItems.CHOPPING_BLOCK.get());
                        output.accept(ModItems.DRYING_RACK.get());
                        // Both states of a stateful item, the way vanilla lists potion variants:
                        // damp is what the chopping block yields, dry is what you actually burn.
                        for (var firewood : List.of(ModItems.FIREWOOD_BIRCH, ModItems.FIREWOOD_OAK,
                                ModItems.FIREWOOD_SPRUCE)) {
                            output.accept(firewood.get());
                            output.accept(dried(firewood.get()));
                        }
                        output.accept(ModItems.FELT_HAT.get());
                        output.accept(ModItems.LADLE.get());
                        // Only the dry venik is listed. A steeped one is something you earn at the
                        // tub, not something you pick off a shelf — the steeping is the whole
                        // ritual. The item and its charges are untouched either way.
                        output.accept(ModItems.VENIK_BIRCH.get());
                        output.accept(ModItems.VENIK_OAK.get());
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
