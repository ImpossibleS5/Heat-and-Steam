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

    /** Cooling rates are quoted per second, which is also one simulation step. */
    private static final double SECOND_IN_TICKS = 20.0;

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
     * Heat as last written down, with no cooling applied for the time since. This is the stove's own
     * working value and what the item's gauge draws; anything outside a stove wants
     * {@link #heatAt} instead.
     *
     * <p>Heat is a float on purpose. With whole numbers a three-a-second fire could not be shared
     * between eight stones, so it went to one stone at a time and the basket appeared to heat in
     * turn rather than together.
     */
    public static float storedHeat(ItemStack stack) {
        return Math.max(0.0F, stack.getOrDefault(ModDataComponents.HEAT.get(), 0.0F));
    }

    public static void setHeat(ItemStack stack, float heat) {
        stack.set(ModDataComponents.HEAT.get(), Math.max(0.0F, heat));
    }

    /**
     * Heat right now: what was stored, less the cooling owed for the time since it was written.
     *
     * <p>Cooling is worked out on read rather than ticked down because items in a chest never tick,
     * which made a chest a perfect thermos. A stone now goes cold wherever it is left — in a
     * backpack, in a barrel, lying in the grass, even in an unloaded chunk — and it costs nothing
     * per tick to do it.
     */
    public static float heatAt(ItemStack stack, long gameTime) {
        return heatAt(stack, gameTime, 1.0);
    }

    private static float heatAt(ItemStack stack, long gameTime, double multiplier) {
        float stored = storedHeat(stack);
        Long written = stack.get(ModDataComponents.HEAT_TIME.get());
        if (stored <= 0.0F || written == null) {
            // Never stamped: a stone straight from the creative menu or a recipe. Put it on the
            // clock at its first settle rather than charging it for all the time since the world
            // began, which would read as "hot stones arrive cold".
            return stored;
        }
        long elapsed = Math.max(0L, gameTime - written);
        double cooling = Config.STONE_COOLING_PER_STEP.get() * multiplier
                * elapsed / SECOND_IN_TICKS;
        return (float) Math.max(0.0, stored - cooling);
    }

    /**
     * Applies the cooling owed since the last write and puts the stone back on the clock.
     *
     * @param multiplier how much faster than usual it is losing heat — see
     *                   {@link com.banya.registry.ModTags.Fluids#COOLS_STONES}
     * @return the heat it is left with
     */
    public static float settle(ItemStack stack, long gameTime, double multiplier) {
        float heat = heatAt(stack, gameTime, multiplier);
        setHeat(stack, heat);
        stamp(stack, gameTime);
        return heat;
    }

    /** Puts a stone on the clock without cooling it, for heat that is being modelled elsewhere. */
    public static void stamp(ItemStack stack, long gameTime) {
        stack.set(ModDataComponents.HEAT_TIME.get(), gameTime);
    }

    /**
     * Keeps every stone in a basket on the clock. The stove models its own stones, so they must not
     * also be charged for the time they spend sitting in it.
     */
    public static void stampAll(IItemHandler stones, long gameTime) {
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                stamp(stack, gameTime);
            }
        }
    }

    /** Heat past which a stone is too hot to keep hold of, as a share of what it can hold. */
    public static boolean isScalding(ItemStack stack, long gameTime) {
        float capacity = capacityOf(stack);
        return capacity > 0.0F
                && heatAt(stack, gameTime) >= capacity * Config.STONE_BURN_FRACTION.get();
    }

    public static int cracksOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CRACKS.get(), 0);
    }

    /** Heat banked across the whole basket. */
    public static float totalHeat(IItemHandler stones) {
        float total = 0.0F;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            total += storedHeat(stones.getStackInSlot(slot));
        }
        return total;
    }

    /** The fire heats the whole basket at once, so every stone takes an equal share. */
    public static void charge(IItemHandler stones, double amount, double tierFactor) {
        int sharing = 0;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            if (!stack.isEmpty() && storedHeat(stack) < capacityOf(stack, tierFactor)) {
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
            if (storedHeat(stack) < capacity) {
                setHeat(stack, Math.min(capacity, storedHeat(stack) + share));
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
            if (storedHeat(stones.getStackInSlot(slot)) > 0.0F) {
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
            float heat = storedHeat(stack);
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
