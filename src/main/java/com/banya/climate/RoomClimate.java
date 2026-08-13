package com.banya.climate;

import com.banya.Config;
import com.banya.registry.ModTags;
import com.banya.stove.ChimneyState;
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
     * @param current   current room temperature in deg C
     * @param heatInput degrees C of heat offered this step, from the fire and/or the hot stones
     * @param room      the enclosing room, or {@code null} when open/leaking
     * @return the temperature after one simulation step
     */
    public static double nextTemperature(double current, double heatInput,
                                         @Nullable RoomShape room, LevelReader level,
                                         double leakMultiplier) {
        double ambient = Config.AMBIENT_TEMPERATURE.get();
        double coefficient = Config.LEAK_COEFFICIENT.get() * leakMultiplier;
        double excess = current - ambient;

        if (room == null) {
            // Nothing to hold the heat: it bleeds away regardless of the fire.
            double loss = coefficient * OPEN_SPACE_LEAK_MULTIPLIER * excess;
            return Math.max(ambient, current - loss);
        }

        // Newton's law of cooling: the hotter the room already is, the harder it fights back.
        // That is what gives each banya a ceiling set by its walls, wood and size, instead of
        // every room climbing to the same cap at the same rate.
        double loss = coefficient * (2.0 - averageInsulation(level, room)) * excess;
        double gain = heatInput * sizeFactor(room);
        return clamp(current + gain - loss, ambient, Config.MAX_TEMPERATURE.get());
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
     * Smoke after one simulation step. A sealed room holds it in — it only really clears when the
     * door is open, which is exactly how a banya fired without a chimney is aired out.
     *
     * @param producing whether the fire is currently making smoke
     * @param wetFuel   damp wood smokes far worse, as the design intends
     */
    public static double nextSmoke(double current, double output, @Nullable RoomShape room,
                                   ChimneyState chimney) {
        double settle = Config.SMOKE_SETTLE_PER_STEP.get();
        if (room == null) {
            // Opened up: the smoke rolls out far faster than it ever settles.
            settle *= Config.SMOKE_VENT_MULTIPLIER.get();
        } else if (chimney == ChimneyState.OPEN) {
            settle *= Config.CHIMNEY_VENT_MULTIPLIER.get();
        }
        return Math.clamp(current + output - settle, 0.0, 100.0);
    }

    /** How much faster the room bleeds heat, given what is above the stove. */
    public static double leakMultiplier(ChimneyState chimney) {
        // An open flue is a hole in the roof: it takes the smoke out and the warmth with it.
        return chimney == ChimneyState.OPEN ? Config.CHIMNEY_HEAT_LOSS.get() : 1.0;
    }

    /** Whether steam is doing anything worth noticing, for readouts. */
    public static boolean isHumid(double humidity) {
        return humidity >= 1.0;
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

    /**
     * How much of the fire's heat a room of this size takes, 1.0 at the reference banya and less
     * above it.
     *
     * <p>Measured against the room's <em>wall area</em>, not its volume. Heat leaves through the
     * shell, so a room's ceiling is set by how much shell it has; dividing the gain by volume while
     * the loss ignored size altogether made the equilibrium fall as 1/V, and a parnaya three times
     * the reference could not be brought past 30 °C by any stove, wood or stonework. Area grows as
     * the square of a room's side and volume as the cube, so a hall is still much harder to heat
     * than a parnaya — it is simply no longer hopeless.
     *
     * <p>The reference area is derived from {@code referenceVolume} rather than being a dial of its
     * own: six faces of a cube of that volume, which is 96 for the default 64 and leaves the
     * reference-sized banya reading exactly as it did before.
     */
    private static double sizeFactor(RoomShape room) {
        double volume = Config.REFERENCE_VOLUME.get();
        double referenceArea = 6.0 * Math.cbrt(volume * volume);
        return Math.min(1.0, referenceArea / Math.max(1.0, room.walls().size()));
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
