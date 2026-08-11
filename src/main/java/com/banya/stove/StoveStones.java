package com.banya.stove;

import com.banya.Config;
import com.banya.registry.ModDataComponents;
import com.banya.registry.ModTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * The stone basket of the каменка: a heat battery that keeps the parnaya warm after the fire dies,
 * and the surface the ladle is thrown onto.
 *
 * <p>Heat and wear live on the stones themselves rather than on the stove, so a stone carries its
 * warmth out of the basket and its cracks are visible in the hand. Quality comes from item tags, so
 * any mod's rock can qualify; the components work on any stack regardless of its item class.
 */
public final class StoveStones {
    private static final int QUALITY_LOW = 1;
    private static final int QUALITY_MID = 2;
    private static final int QUALITY_HIGH = 3;
    private static final int QUALITY_BEST = 4;

    /** Cracking stages a stone passes through before it falls apart. */
    public static final int MAX_CRACKS = 3;

    private StoveStones() {}

    /** Quality points for one stone, or 0 if this item is not a banya stone. */
    public static int qualityOf(ItemStack stack) {
        if (stack.is(ModTags.Items.STONES_BEST)) {
            return QUALITY_BEST;
        }
        if (stack.is(ModTags.Items.STONES_HIGH)) {
            return QUALITY_HIGH;
        }
        if (stack.is(ModTags.Items.STONES_MID)) {
            return QUALITY_MID;
        }
        if (stack.is(ModTags.Items.STONES_LOW)) {
            return QUALITY_LOW;
        }
        return 0;
    }

    public static boolean isStone(ItemStack stack) {
        return qualityOf(stack) > 0;
    }

    /** Heat one stone can hold, scaled by how good the rock is. */
    public static int capacityOf(ItemStack stack) {
        return (int) Math.round(qualityOf(stack) * Config.STONE_CAPACITY_PER_QUALITY.get());
    }

    public static int heatOf(ItemStack stack) {
        return Math.min(stack.getOrDefault(ModDataComponents.HEAT.get(), 0), capacityOf(stack));
    }

    public static void setHeat(ItemStack stack, int heat) {
        stack.set(ModDataComponents.HEAT.get(), Math.clamp(heat, 0, capacityOf(stack)));
    }

    public static int cracksOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CRACKS.get(), 0);
    }

    /** Heat banked across the whole basket. */
    public static int totalHeat(IItemHandler stones) {
        int total = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            total += heatOf(stones.getStackInSlot(slot));
        }
        return total;
    }

    /** Spreads heat over the basket, filling the coolest stones first. */
    public static void charge(IItemHandler stones, double amount) {
        int remaining = (int) Math.round(amount);
        while (remaining > 0) {
            int target = coolestChargeableSlot(stones);
            if (target < 0) {
                return;
            }
            ItemStack stack = stones.getStackInSlot(target);
            int room = capacityOf(stack) - heatOf(stack);
            int given = Math.min(remaining, Math.min(room, 1 + remaining / 4));
            setHeat(stack, heatOf(stack) + given);
            remaining -= given;
        }
    }

    /**
     * Draws heat back out of the basket, hottest stone first.
     *
     * @return how much was actually available
     */
    public static double release(IItemHandler stones, double amount) {
        int remaining = (int) Math.round(amount);
        int drawn = 0;
        while (remaining > 0) {
            int target = hottestSlot(stones);
            if (target < 0) {
                break;
            }
            ItemStack stack = stones.getStackInSlot(target);
            int taken = Math.min(remaining, heatOf(stack));
            setHeat(stack, heatOf(stack) - taken);
            drawn += taken;
            remaining -= taken;
        }
        return drawn;
    }

    /**
     * Cracks one stone a little further, the way real ones split after enough heat-and-water
     * cycles. Better rock survives proportionally longer.
     *
     * @return true if a stone crumbled away entirely
     */
    public static boolean wearOne(IItemHandler stones, RandomSource random, double poursPerCrack) {
        int slot = randomLoadedSlot(stones, random);
        if (slot < 0) {
            return false;
        }
        ItemStack stack = stones.getStackInSlot(slot);
        // Spread the stone's whole life across its cracking stages.
        double perStage = Math.max(1.0, poursPerCrack * qualityOf(stack) / MAX_CRACKS);
        if (random.nextDouble() >= 1.0 / perStage) {
            return false;
        }

        int cracks = cracksOf(stack) + 1;
        if (cracks >= MAX_CRACKS) {
            stack.shrink(1);
            return true;
        }
        stack.set(ModDataComponents.CRACKS.get(), cracks);
        return false;
    }

    private static int coolestChargeableSlot(IItemHandler stones) {
        int best = -1;
        int lowest = Integer.MAX_VALUE;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            int heat = heatOf(stack);
            if (heat < capacityOf(stack) && heat < lowest) {
                lowest = heat;
                best = slot;
            }
        }
        return best;
    }

    private static int hottestSlot(IItemHandler stones) {
        int best = -1;
        int highest = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            int heat = heatOf(stones.getStackInSlot(slot));
            if (heat > highest) {
                highest = heat;
                best = slot;
            }
        }
        return best;
    }

    private static int randomLoadedSlot(IItemHandler stones, RandomSource random) {
        int loaded = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            if (!stones.getStackInSlot(slot).isEmpty()) {
                loaded++;
            }
        }
        if (loaded == 0) {
            return -1;
        }
        int pick = random.nextInt(loaded);
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            if (!stones.getStackInSlot(slot).isEmpty() && pick-- == 0) {
                return slot;
            }
        }
        return -1;
    }
}
