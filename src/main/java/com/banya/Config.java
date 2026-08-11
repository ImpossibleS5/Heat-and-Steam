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

    public static final ModConfigSpec.DoubleValue HUMIDITY_DECAY_PER_STEP = BUILDER
            .comment("Humidity (%) lost per simulation step as steam condenses.")
            .defineInRange("humidityDecayPerStep", 2.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue HUMIDITY_PER_LADLE = BUILDER
            .comment("Humidity (%) added by one ladle of water thrown onto hot stones.")
            .defineInRange("humidityPerLadle", 25.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue STEAM_TEMPERATURE = BUILDER
            .comment(
                    "Room temperature (deg C) the stove must reach for a proper light steam.",
                    "Below this a ladle produces heavy steam: much less humidity and no benefit.")
            .defineInRange("steamTemperature", 70.0, 0.0, 300.0);

    public static final ModConfigSpec.DoubleValue HEAVY_STEAM_MULTIPLIER = BUILDER
            .comment("Fraction of the normal humidity gained when the stones are too cold.")
            .defineInRange("heavySteamMultiplier", 0.4, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue HUMIDITY_HEAT_WEIGHT = BUILDER
            .comment(
                    "How strongly humidity amplifies perceived heat.",
                    "At 1.0, 100% humidity makes a room feel twice as hot as its dry temperature,",
                    "so a moderate wet parnaya out-heats a very hot dry sauna — as intended.")
            .defineInRange("humidityHeatWeight", 1.0, 0.0, 4.0);

    public static final ModConfigSpec.DoubleValue STONE_CAPACITY_PER_QUALITY = BUILDER
            .comment(
                    "Heat a single stone stores per point of quality (low=1, mid=2, high=3).",
                    "Stored heat is spent at stoneReleasePerStep once the fire dies down.")
            .defineInRange("stoneCapacityPerQuality", 60.0, 1.0, 10000.0);

    public static final ModConfigSpec.DoubleValue STONE_CHARGE_PER_STEP = BUILDER
            .comment("Heat the stones absorb per simulation step while the stove burns.")
            .defineInRange("stoneChargePerStep", 3.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue STONE_RELEASE_PER_STEP = BUILDER
            .comment("Degrees C the stones give back per simulation step after the fire goes out.")
            .defineInRange("stoneReleasePerStep", 1.5, 0.1, 50.0);

    public static final ModConfigSpec.DoubleValue TUB_STEEP_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) the tub's water must reach before a venik can be steeped.")
            .defineInRange("tubSteepTemperature", 60.0, 0.0, 300.0);

    public static final ModConfigSpec.IntValue VENIK_STEEP_USES = BUILDER
            .comment(
                    "How many whisks one steeping is good for.",
                    "After that the venik dries out and has to go back in the tub.")
            .defineInRange("venikSteepUses", 4, 1, 64);

    public static final ModConfigSpec.DoubleValue VENIK_HEAT_INDEX = BUILDER
            .comment("Perceived heat the room must reach before a venik can be used at all.")
            .defineInRange("venikHeatIndex", 60.0, 0.0, 400.0);

    public static final ModConfigSpec.IntValue VENIK_CHANNEL_TICKS = BUILDER
            .comment("How long whisking takes, in ticks (20 = 1 second).")
            .defineInRange("venikChannelTicks", 60, 5, 200);

    public static final ModConfigSpec.DoubleValue VENIK_OTHER_PLAYER_MULTIPLIER = BUILDER
            .comment(
                    "Effect multiplier when whisking someone else rather than yourself.",
                    "Above 1.0 this is the mod's nudge towards bathing together.")
            .defineInRange("venikOtherPlayerMultiplier", 1.5, 1.0, 5.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
