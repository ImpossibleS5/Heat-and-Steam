package com.impossibles5.heatandsteam.climate;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModTags;
import com.impossibles5.heatandsteam.stove.ChimneyState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class RoomClimate {
    private static final double[] INSULATION_RATINGS = {0.5, 0.8, 1.0};

    private static final double INSULATION_DEFAULT = INSULATION_RATINGS[0];

    private RoomClimate() {}

    public static double nextTemperature(double current, double heatInput,
                                         @Nullable RoomShape room, double thermalMass,
                                         LevelReader level, double leakMultiplier) {
        double ambient = Config.AMBIENT_TEMPERATURE.get();
        double coefficient = Config.LEAK_COEFFICIENT.get() * leakMultiplier;
        double excess = current - ambient;

        if (room == null) {
            double loss = coefficient * Config.OPEN_ROOM_LEAK_MULTIPLIER.get() * excess / thermalMass;
            return Math.max(ambient, current - loss);
        }

        double loss = coefficient * wallConductance(level, room) * areaFactor(room) * excess;
        double change = (heatInput - loss) / thermalMass;
        return clamp(current + change, ambient, Config.MAX_TEMPERATURE.get());
    }

    public static double nextHumidity(double current, @Nullable RoomShape room, double thermalMass) {
        double decay = Config.HUMIDITY_DECAY_PER_STEP.get();
        if (room == null) {
            decay *= Config.OPEN_ROOM_LEAK_MULTIPLIER.get();
        }
        return Math.max(0.0, current - decay / thermalMass);
    }

    public static double nextSmoke(double current, double output, @Nullable RoomShape room,
                                   double thermalMass, ChimneyState chimney) {
        double settle = Config.SMOKE_SETTLE_PER_STEP.get();
        if (room == null) {
            settle = settle * Config.SMOKE_VENT_MULTIPLIER.get()
                    + current * Config.CHIMNEY_VENT_FRACTION.get();
        } else if (chimney == ChimneyState.OPEN) {
            settle = settle * Config.CHIMNEY_VENT_MULTIPLIER.get()
                    + current * Config.CHIMNEY_VENT_FRACTION.get();
        }
        return Math.clamp(current + (output - settle) / thermalMass, 0.0, 100.0);
    }

    public static double leakMultiplier(ChimneyState chimney) {
        return chimney == ChimneyState.OPEN ? Config.CHIMNEY_HEAT_LOSS.get() : 1.0;
    }

    public static boolean isHumid(double humidity) {
        return humidity >= 1.0;
    }

    public static double heatIndex(double temperature, double humidity) {
        double weight = Config.HUMIDITY_HEAT_WEIGHT.get();
        return temperature * (1.0 + weight * (humidity / 100.0));
    }

    private static double wallConductance(LevelReader level, RoomShape room) {
        if (room.walls().isEmpty()) {
            return 1.0 / INSULATION_DEFAULT;
        }
        double total = 0.0;
        for (BlockPos wall : room.walls()) {
            total += 1.0 / insulationOf(level.getBlockState(wall));
        }
        return total / room.walls().size();
    }

    private static double insulationOf(BlockState state) {
        for (int i = ModTags.Blocks.INSULATION.size() - 1; i >= 0; i--) {
            if (state.is(ModTags.Blocks.INSULATION.get(i))) {
                return INSULATION_RATINGS[i];
            }
        }
        return INSULATION_DEFAULT;
    }

    private static double areaFactor(RoomShape room) {
        double volume = Config.REFERENCE_VOLUME.get();
        double referenceArea = 6.0 * Math.cbrt(volume * volume);
        return Math.max(1.0, room.shell()) / referenceArea;
    }

    public static double thermalMass(RoomShape room) {
        return Math.max(1.0, room.volume() / (double) Config.REFERENCE_VOLUME.get());
    }

    public static double approach(double current, double target, double rate) {
        if (current > target) {
            return Math.max(target, current - rate);
        }
        return Math.min(target, current + rate);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
