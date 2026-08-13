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
     * How much this stone's temperature resists being moved, in either direction.
     *
     * <p>Quality is the whole story: soapstone is four times the thermal mass of a river cobble, so
     * it takes four times as long to heat and four times as long to give that heat back. One number
     * for both halves is what makes better rock a trade rather than a strict upgrade — and it is why
     * there is no separate dial for charge time any more.
     */
    public static float thermalMass(ItemStack stack) {
        return (float) (qualityOf(stack) * Config.STONE_THERMAL_MASS_PER_QUALITY.get());
    }

    /**
     * Temperature in °C as last written down, with no cooling applied for the time since. This is
     * the stove's own working value; anything outside a stove wants {@link #temperatureAt} instead.
     */
    public static float temperature(ItemStack stack) {
        return Math.max(0.0F, stack.getOrDefault(ModDataComponents.TEMPERATURE.get(), 0.0F));
    }

    public static void setTemperature(ItemStack stack, float temperature) {
        stack.set(ModDataComponents.TEMPERATURE.get(), Math.max(0.0F, temperature));
    }

    /**
     * Temperature right now: what was written down, less the cooling owed for the time since.
     *
     * <p>Cooling is worked out on read rather than ticked down because items in a chest never tick,
     * which made a chest a perfect thermos. A stone now goes cold wherever it is left — in a
     * backpack, in a barrel, lying in the grass, even in an unloaded chunk — and it costs nothing
     * per tick to do it. The rate divides by thermal mass, so the better the rock the longer it
     * stays dangerous.
     */
    public static float temperatureAt(ItemStack stack, long gameTime) {
        return temperatureAt(stack, gameTime, 1.0);
    }

    private static float temperatureAt(ItemStack stack, long gameTime, double multiplier) {
        float stored = temperature(stack);
        Long written = stack.get(ModDataComponents.TEMPERATURE_TIME.get());
        float mass = thermalMass(stack);
        if (stored <= 0.0F || written == null || mass <= 0.0F) {
            // Never stamped: a stone straight from the creative menu or a recipe. Put it on the
            // clock at its first settle rather than charging it for all the time since the world
            // began, which would read as "hot stones arrive cold".
            return stored;
        }
        long elapsed = Math.max(0L, gameTime - written);
        double cooling = Config.STONE_COOLING_MODIFIER.get() * multiplier * elapsed
                / (SECOND_IN_TICKS * mass);
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
        float temperature = temperatureAt(stack, gameTime, multiplier);
        setTemperature(stack, temperature);
        stamp(stack, gameTime);
        return temperature;
    }

    /** Puts a stone on the clock without cooling it, for heat that is being modelled elsewhere. */
    public static void stamp(ItemStack stack, long gameTime) {
        stack.set(ModDataComponents.TEMPERATURE_TIME.get(), gameTime);
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

    /**
     * Whether this stone is too hot to keep hold of. A flat temperature, not a share of anything:
     * a humble river cobble at 300 °C burns exactly as badly as soapstone at 300 °C.
     */
    public static boolean isScalding(ItemStack stack, long gameTime) {
        return isStone(stack)
                && temperatureAt(stack, gameTime) >= Config.STONE_SCALD_TEMPERATURE.get();
    }

    public static int cracksOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CRACKS.get(), 0);
    }

    /** Thermal mass of the whole basket — how much banya there is to warm the room with. */
    public static float totalThermalMass(IItemHandler stones) {
        float total = 0.0F;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            total += thermalMass(stones.getStackInSlot(slot));
        }
        return total;
    }

    /**
     * Temperature of the basket as one body, weighted by mass — one cold pebble among soapstone
     * should not drag the reading down as far as it drags the heat down.
     */
    public static float averageTemperature(IItemHandler stones) {
        float mass = 0.0F;
        float weighted = 0.0F;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            float stoneMass = thermalMass(stack);
            if (stoneMass > 0.0F) {
                mass += stoneMass;
                weighted += temperature(stack) * stoneMass;
            }
        }
        return mass <= 0.0F ? 0.0F : weighted / mass;
    }

    /**
     * Pushes every stone towards the temperature of the fire under it.
     *
     * <p>Each stone climbs at {@code modifier / its own mass} on its own account, and never past the
     * fire: nothing in a stove gets hotter than what is burning in it. Deliberately not shared out
     * between the stones — a stone in a fire heats at the rate its own rock allows, so a full basket
     * is not a slower basket, it is simply a bigger one.
     */
    public static void heatTowards(IItemHandler stones, float target, double modifier) {
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            float mass = thermalMass(stack);
            float current = temperature(stack);
            if (mass <= 0.0F || current >= target) {
                continue;
            }
            setTemperature(stack, (float) Math.min(target, current + modifier / mass));
        }
    }

    /**
     * The basket's exchange with the parnaya, both halves of it: what the room gains, and what the
     * stones spend to give it.
     *
     * <p>Runs whether or not the fire is lit, because heat does not wait for the flames to go out.
     * While the stove burns the stones add to it; once it dies they are the only thing still holding
     * the room warm, and they cool as they do it. That is the whole of what a каменка is for, and it
     * replaces the old pair of dials — a flat "radiation" while burning and a flat "release" after —
     * with the one gradient that drives both.
     *
     * @param roomTemperature the parnaya's current temperature
     * @return degrees the room gains this step
     */
    public static double giveToRoom(IItemHandler stones, double roomTemperature) {
        float mass = totalThermalMass(stones);
        if (mass <= 0.0F) {
            return 0.0;
        }
        double gradient = averageTemperature(stones) - roomTemperature;
        if (gradient <= 0.0) {
            return 0.0; // a cold basket is not a heat sink: the room keeps what it has
        }

        double gain = Config.STONE_ROOM_COEFFICIENT.get()
                * (mass / Config.STONE_REFERENCE_MASS.get())
                * gradient;
        // Paid for out of the stones themselves, and never past the room they are warming.
        float spent = (float) (gain * Config.STONE_ENERGY_TO_DEGREES.get() / mass);
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            float current = temperature(stack);
            if (thermalMass(stack) > 0.0F && current > roomTemperature) {
                setTemperature(stack, (float) Math.max(roomTemperature, current - spent));
            }
        }
        return gain;
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
