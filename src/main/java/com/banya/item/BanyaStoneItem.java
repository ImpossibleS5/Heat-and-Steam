package com.banya.item;

import com.banya.Config;
import com.banya.stove.StoveStones;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A banya stone. Its heat and its cracks live on the stack, so a stone carries its warmth out of
 * the basket and wears its damage where you can see it.
 *
 * <p>Heat storage itself is component-based and works on any tagged stack, so third-party stones
 * still bank heat in a stove. What this class adds is the parts that need an item: cooling down in
 * your pocket, and the gauge that shows it.
 */
public class BanyaStoneItem extends Item {
    /** Width of a full vanilla item bar, in pixels. */
    private static final int BAR_SEGMENTS = 13;
    /** Ember orange, so the heat gauge never reads as durability. */
    private static final int HEAT_BAR_COLOR = 0xFF8A3B;
    private static final int COOL_INTERVAL_TICKS = 20;

    public BanyaStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide() || level.getGameTime() % COOL_INTERVAL_TICKS != 0) {
            return;
        }
        int heat = StoveStones.heatOf(stack);
        if (heat > 0) {
            // Out of the stove it gives its warmth up to the room, slowly enough to be carried.
            StoveStones.setHeat(stack, heat - Config.STONE_COOLING_PER_STEP.get());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return StoveStones.heatOf(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int capacity = Math.max(1, StoveStones.capacityOf(stack));
        // A big stove banks more into a stone than the stone holds on its own, so a freshly pulled
        // one can read over full. Clamp, or the bar runs off the end of the icon.
        float filled = Math.min(1.0F, StoveStones.heatOf(stack) / (float) capacity);
        return Math.clamp(Math.round(BAR_SEGMENTS * filled), 1, BAR_SEGMENTS);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return HEAT_BAR_COLOR;
    }
}
