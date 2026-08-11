package com.banya.stove;

import com.banya.Config;
import com.banya.registry.ModTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * The stone basket of the каменка: a heat battery that keeps the parnaya warm after the fire dies,
 * and the surface the ladle is thrown onto.
 *
 * <p>Quality comes from item tags, so any mod's rock can qualify. Mass is simply the stack count —
 * more stones, longer heat.
 */
public final class StoveStones {
    private static final int QUALITY_LOW = 1;
    private static final int QUALITY_MID = 2;
    private static final int QUALITY_HIGH = 3;
    private static final int QUALITY_BEST = 4;

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
     * How much heat the loaded stones can bank. Scales with both quality and mass, so a full basket
     * of basalt holds a parnaya far longer than a couple of cobbles.
     */
    /**
     * Cracks one stone from the basket, the way real ones split after enough heat-and-water cycles.
     * Better rock survives proportionally longer, so the trip up the mountain for soapstone pays off
     * twice: hotter banya and fewer replacements.
     *
     * @return true if a stone was lost
     */
    public static boolean wearOne(IItemHandler stones, RandomSource random, double poursPerCrack) {
        int slot = randomLoadedSlot(stones, random);
        if (slot < 0) {
            return false;
        }
        ItemStack stack = stones.getStackInSlot(slot);
        double lifetime = Math.max(1.0, poursPerCrack * qualityOf(stack));
        if (random.nextDouble() >= 1.0 / lifetime) {
            return false;
        }
        stack.shrink(1);
        return true;
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

    public static double capacityOf(IItemHandler stones) {
        int qualityPoints = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            qualityPoints += qualityOf(stack) * stack.getCount();
        }
        return qualityPoints * Config.STONE_CAPACITY_PER_QUALITY.get();
    }
}
