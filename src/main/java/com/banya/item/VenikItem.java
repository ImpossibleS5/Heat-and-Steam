package com.banya.item;

import com.banya.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * A venik — the bundle of leafy twigs used to whisk a bather. It must be steeped in a hot tub first;
 * a dry one crumbles and scratches, so steaming with it is refused (see Phase 2D).
 *
 * <p>Steeped state is a data component so one item covers both looks, and durability is vanilla —
 * leaves fall off with use.
 */
public class VenikItem extends Item {
    public VenikItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(isSteeped(stack)
                        ? "tooltip.banya.venik.steeped"
                        : "tooltip.banya.venik.dry")
                .withStyle(isSteeped(stack) ? ChatFormatting.AQUA : ChatFormatting.GRAY));
    }

    public static boolean isSteeped(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STEEPED.get(), false);
    }

    public static void setSteeped(ItemStack stack, boolean steeped) {
        stack.set(ModDataComponents.STEEPED.get(), steeped);
    }
}
