package com.banya.climate;

import com.banya.Config;
import com.banya.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Temperature model for a parnaya, advanced one simulation step (1 second) at a time.
 *
 * <p>Kept as static functions over an explicit state so the maths stays readable and independent of
 * the BlockEntity. All tuning values come from {@link Config}.
 */
public final class RoomClimate {
    /** Insulation weights per tier; 1.0 is a perfect wall. */
    private static final double INSULATION_HIGH = 1.0;
    private static final double INSULATION_MID = 0.8;
    private static final double INSULATION_LOW = 0.5;
    /** Anything unclassified is assumed to insulate poorly. */
    private static final double INSULATION_DEFAULT = 0.5;
    /** Heat bleeds out this much faster when the stove is not inside a sealed room. */
    private static final double OPEN_SPACE_LEAK_MULTIPLIER = 3.0;

    private RoomClimate() {}

    /**
     * @param current  current room temperature in deg C
     * @param heating  whether the stove is burning this step
     * @param room     the enclosing room, or {@code null} when open/leaking
     * @return the temperature after one simulation step
     */
    public static double nextTemperature(double current, boolean heating,
                                         @Nullable RoomShape room, LevelReader level) {
        double ambient = Config.AMBIENT_TEMPERATURE.get();
        if (room == null) {
            // Nothing to hold the heat: fall back toward ambient regardless of the fire.
            double leak = Config.BASE_LEAK_PER_STEP.get() * OPEN_SPACE_LEAK_MULTIPLIER;
            return approach(current, ambient, leak);
        }

        double leak = Config.BASE_LEAK_PER_STEP.get() * (2.0 - averageInsulation(level, room));
        double heatIn = heating ? Config.HEAT_PER_STEP.get() * volumeFactor(room) : 0.0;
        return clamp(current + heatIn - leak, ambient, Config.MAX_TEMPERATURE.get());
    }

    /**
     * Humidity after one simulation step. Steam condenses steadily, and an unsealed room loses it
     * as fast as it loses heat.
     */
    public static double nextHumidity(double current, @Nullable RoomShape room) {
        double decay = Config.HUMIDITY_DECAY_PER_STEP.get();
        if (room == null) {
            decay *= OPEN_SPACE_LEAK_MULTIPLIER;
        }
        return Math.max(0.0, current - decay);
    }

    /**
     * Perceived heat ("индекс жара"): what the player's body actually reacts to. Humid air carries
     * heat far better, so a 60 C parnaya at 90% humidity feels hotter than a 100 C dry sauna —
     * the two play styles the design is built around.
     *
     * @param temperature room temperature in deg C
     * @param humidity    room humidity, 0-100
     */
    public static double heatIndex(double temperature, double humidity) {
        double weight = Config.HUMIDITY_HEAT_WEIGHT.get();
        return temperature * (1.0 + weight * (humidity / 100.0));
    }

    /** Mean insulation quality of the enclosing walls, in {@code [0, 1]}. */
    private static double averageInsulation(LevelReader level, RoomShape room) {
        if (room.walls().isEmpty()) {
            return INSULATION_DEFAULT;
        }
        double total = 0.0;
        for (BlockPos wall : room.walls()) {
            total += insulationOf(level.getBlockState(wall));
        }
        return total / room.walls().size();
    }

    private static double insulationOf(BlockState state) {
        if (state.is(ModTags.Blocks.INSULATION_HIGH)) {
            return INSULATION_HIGH;
        }
        if (state.is(ModTags.Blocks.INSULATION_MID)) {
            return INSULATION_MID;
        }
        if (state.is(ModTags.Blocks.INSULATION_LOW)) {
            return INSULATION_LOW;
        }
        return INSULATION_DEFAULT;
    }

    /** Rooms larger than the reference volume warm up proportionally slower. */
    private static double volumeFactor(RoomShape room) {
        double reference = Config.REFERENCE_VOLUME.get();
        return Math.min(1.0, reference / Math.max(1.0, room.volume()));
    }

    private static double approach(double current, double target, double rate) {
        if (current > target) {
            return Math.max(target, current - rate);
        }
        return Math.min(target, current + rate);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
