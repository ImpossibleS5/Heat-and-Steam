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

    /**
     * Heat one stone can hold, scaled by how good the rock is.
     *
     * <p>The stove it sits in scales this further: a massive stove banks several times as much in
     * the same stone, which is what lets it hold a banya warm past a game day. Out of a stove the
     * plain capacity applies, so a stone carried away cannot hold more than it earned.
     */
    public static int capacityOf(ItemStack stack) {
        return capacityOf(stack, 1.0);
    }

    public static int capacityOf(ItemStack stack, double tierFactor) {
        return (int) Math.round(qualityOf(stack) * Config.STONE_CAPACITY_PER_QUALITY.get() * tierFactor);
    }

    /**
     * Heat is a float on purpose. With whole numbers a three-a-second fire could not be shared
     * between eight stones, so it went to one stone at a time and the basket appeared to heat in
     * turn rather than together.
     */
    public static float heatOf(ItemStack stack) {
        return Math.max(0.0F, stack.getOrDefault(ModDataComponents.HEAT.get(), 0.0F));
    }

    public static void setHeat(ItemStack stack, float heat) {
        stack.set(ModDataComponents.HEAT.get(), Math.max(0.0F, heat));
    }

    public static int cracksOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CRACKS.get(), 0);
    }

    /** Heat banked across the whole basket. */
    public static float totalHeat(IItemHandler stones) {
        float total = 0.0F;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            total += heatOf(stones.getStackInSlot(slot));
        }
        return total;
    }

    /** The fire heats the whole basket at once, so every stone takes an equal share. */
    public static void charge(IItemHandler stones, double amount, double tierFactor) {
        int sharing = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            if (!stack.isEmpty() && heatOf(stack) < capacityOf(stack, tierFactor)) {
                sharing++;
            }
        }
        if (sharing == 0) {
            return;
        }

        float share = (float) (amount / sharing);
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            float capacity = capacityOf(stack, tierFactor);
            if (heatOf(stack) < capacity) {
                setHeat(stack, Math.min(capacity, heatOf(stack) + share));
            }
        }
    }

    /**
     * Draws heat back out of the basket, evenly, the way it went in.
     *
     * @return how much was actually available
     */
    public static double release(IItemHandler stones, double amount) {
        int sharing = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            if (heatOf(stones.getStackInSlot(slot)) > 0.0F) {
                sharing++;
            }
        }
        if (sharing == 0) {
            return 0.0;
        }

        float share = (float) (amount / sharing);
        float drawn = 0.0F;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            float heat = heatOf(stack);
            if (heat <= 0.0F) {
                continue;
            }
            float taken = Math.min(share, heat);
            setHeat(stack, heat - taken);
            drawn += taken;
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
