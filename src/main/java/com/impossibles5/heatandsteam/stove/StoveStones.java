package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModDataComponents;
import com.impossibles5.heatandsteam.registry.ModTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class StoveStones {
    public static final int MAX_CRACKS = 3;

    private static final double SECOND_IN_TICKS = 20.0;

    private StoveStones() {}

    public static int qualityOf(ItemStack stack) {
        for (int tier = ModTags.Items.STONES.size(); tier >= 1; tier--) {
            if (stack.is(ModTags.Items.STONES.get(tier - 1))) {
                return tier;
            }
        }
        return 0;
    }

    public static boolean isStone(ItemStack stack) {
        return qualityOf(stack) > 0;
    }

    public static float thermalMass(ItemStack stack) {
        return (float) (qualityOf(stack) * Config.STONE_THERMAL_MASS_PER_QUALITY.get());
    }

    public static float temperature(ItemStack stack) {
        return Math.max(0.0F, stack.getOrDefault(ModDataComponents.TEMPERATURE.get(), 0.0F));
    }

    private static final float DISPLAY_STEP = 1.0F;

    public static void setTemperature(ItemStack stack, float temperature) {
        stack.set(ModDataComponents.TEMPERATURE.get(), Math.max(0.0F, temperature));
    }

    public static float temperatureAt(ItemStack stack, long gameTime) {
        return temperatureAt(stack, gameTime, 1.0);
    }

    private static float temperatureAt(ItemStack stack, long gameTime, double multiplier) {
        float stored = temperature(stack);
        Long written = stack.get(ModDataComponents.TEMPERATURE_TIME.get());
        float mass = thermalMass(stack);
        if (stored <= 0.0F || written == null || mass <= 0.0F) {
            return stored;
        }
        long elapsed = Math.max(0L, gameTime - written);
        double cooling = Config.STONE_COOLING_MODIFIER.get() * multiplier * elapsed
                / (SECOND_IN_TICKS * mass);
        return (float) Math.max(0.0, stored - cooling);
    }

    public static float settle(ItemStack stack, long gameTime, double multiplier) {
        if (!stack.has(ModDataComponents.TEMPERATURE.get())
                && !stack.has(ModDataComponents.TEMPERATURE_TIME.get())) {
            return 0.0F;
        }
        float before = temperature(stack);
        if (!stack.has(ModDataComponents.TEMPERATURE_TIME.get())) {
            stamp(stack, gameTime);
            return before;
        }
        float now = temperatureAt(stack, gameTime, multiplier);
        if (now <= 0.0F) {
            stack.remove(ModDataComponents.TEMPERATURE.get());
            stack.remove(ModDataComponents.TEMPERATURE_TIME.get());
            return 0.0F;
        }
        if (Math.abs(now - before) < DISPLAY_STEP) {
            return before;
        }
        setTemperature(stack, now);
        stamp(stack, gameTime);
        return now;
    }

    public static void stamp(ItemStack stack, long gameTime) {
        stack.set(ModDataComponents.TEMPERATURE_TIME.get(), gameTime);
    }

    public static void stampAll(IItemHandler stones, long gameTime) {
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                stamp(stack, gameTime);
            }
        }
    }

    public static boolean isScalding(ItemStack stack, long gameTime) {
        return isStone(stack)
                && temperatureAt(stack, gameTime) >= Config.STONE_SCALD_TEMPERATURE.get();
    }

    public static int cracksOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CRACKS.get(), 0);
    }

    public static float totalThermalMass(IItemHandler stones) {
        float total = 0.0F;
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            total += thermalMass(stones.getStackInSlot(slot));
        }
        return total;
    }

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

    public static void quench(IItemHandler stones, double degrees, double floor) {
        for (int slot = 0; slot < stones.getSlots(); slot++) {
            ItemStack stack = stones.getStackInSlot(slot);
            float mass = thermalMass(stack);
            if (mass <= 0.0F) {
                continue;
            }
            float current = temperature(stack);
            setTemperature(stack, (float) Math.max(floor, current - degrees / mass));
        }
    }

    public static double giveToRoom(IItemHandler stones, double roomTemperature) {
        float mass = totalThermalMass(stones);
        if (mass <= 0.0F) {
            return 0.0;
        }
        double gradient = averageTemperature(stones) - roomTemperature;
        if (gradient <= 0.0) {
            return 0.0;
        }

        double gain = Config.STONE_ROOM_COEFFICIENT.get()
                * (mass / Config.STONE_REFERENCE_MASS.get())
                * gradient;

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

    public static boolean wearOne(IItemHandler stones, RandomSource random, double poursPerCrack) {
        int slot = randomLoadedSlot(stones, random);
        if (slot < 0) {
            return false;
        }
        ItemStack stack = stones.getStackInSlot(slot);

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
