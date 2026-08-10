package com.banya;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config. Gameplay constants live here (and in the KubeJS layer) rather than as hardcoded
 * Java values — see the design notes.
 */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_ROOM_VOLUME = BUILDER
            .comment(
                    "Maximum enclosed volume (in blocks) the stove treats as a single parnaya (banya room).",
                    "A space that floods past this cap is considered open/leaking and holds no microclimate.")
            .defineInRange("maxRoomVolume", 512, 8, 32768);

    public static final ModConfigSpec.DoubleValue AMBIENT_TEMPERATURE = BUILDER
            .comment("Baseline temperature (deg C) a room decays back to once the stove goes out.")
            .defineInRange("ambientTemperature", 20.0, -50.0, 60.0);

    public static final ModConfigSpec.DoubleValue MAX_TEMPERATURE = BUILDER
            .comment("Temperature ceiling (deg C) reachable by the T1 stove.")
            .defineInRange("maxTemperature", 120.0, 30.0, 300.0);

    public static final ModConfigSpec.DoubleValue HEAT_PER_STEP = BUILDER
            .comment("Degrees C added per simulation step (1 second) while the stove is burning.")
            .defineInRange("heatPerStep", 6.0, 0.1, 50.0);

    public static final ModConfigSpec.DoubleValue BASE_LEAK_PER_STEP = BUILDER
            .comment("Degrees C lost per simulation step through perfectly insulated walls.")
            .defineInRange("baseLeakPerStep", 1.0, 0.01, 50.0);

    public static final ModConfigSpec.IntValue REFERENCE_VOLUME = BUILDER
            .comment(
                    "Room volume (in blocks) that heats at full rate.",
                    "Larger rooms warm up proportionally slower.")
            .defineInRange("referenceVolume", 64, 1, 32768);

    public static final ModConfigSpec.DoubleValue WARMTH_THRESHOLD_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) at or above which a player starts gaining Warmth.")
            .defineInRange("warmthThresholdTemperature", 50.0, 0.0, 300.0);

    public static final ModConfigSpec.DoubleValue WARMTH_GAIN_PER_STEP = BUILDER
            .comment(
                    "Warmth gained per simulation step at the reference temperature.",
                    "Scales with how far the room is above the threshold.")
            .defineInRange("warmthGainPerStep", 2.0, 0.1, 50.0);

    public static final ModConfigSpec.DoubleValue WARMTH_DECAY_PER_STEP = BUILDER
            .comment("Warmth lost per simulation step while out of the heat.")
            .defineInRange("warmthDecayPerStep", 1.5, 0.1, 50.0);

    public static final ModConfigSpec.DoubleValue WARMTH_REFERENCE_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) at which Warmth is gained at exactly the base rate.")
            .defineInRange("warmthReferenceTemperature", 80.0, 1.0, 300.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
