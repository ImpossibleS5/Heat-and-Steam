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
            .comment("Heat the stove offers per simulation step (1 second) while burning.")
            .defineInRange("heatPerStep", 3.0, 0.1, 50.0);

    public static final ModConfigSpec.DoubleValue LEAK_COEFFICIENT = BUILDER
            .comment(
                    "Fraction of the room's excess heat lost per simulation step through perfect walls.",
                    "Loss grows with how far above ambient the room already is, so a banya settles at",
                    "an equilibrium instead of climbing to the cap: hotter needs better walls, drier",
                    "wood or a smaller room. Roughly, equilibrium = ambient + heat / (this * wallFactor).")
            .defineInRange("leakCoefficient", 0.05, 0.001, 1.0);

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
                    "Warmth gained per simulation step at the reference heat index.",
                    "Scales with how far the room's perceived heat is above the threshold, so a humid",
                    "parnaya warms you markedly faster than a dry one at the same temperature.")
            .defineInRange("warmthGainPerStep", 0.8, 0.05, 50.0);

    public static final ModConfigSpec.DoubleValue WARMTH_DECAY_PER_STEP = BUILDER
            .comment("Warmth lost per simulation step while out of the heat.")
            .defineInRange("warmthDecayPerStep", 1.0, 0.05, 50.0);

    public static final ModConfigSpec.DoubleValue OVERHEAT_EXHAUSTION = BUILDER
            .comment("Hunger exhaustion added each second spent in the overheat band.")
            .defineInRange("overheatExhaustion", 0.6, 0.0, 20.0);

    public static final ModConfigSpec.DoubleValue FAINT_EXHAUSTION = BUILDER
            .comment("Hunger exhaustion dealt by a faint. The heat takes it out of you.")
            .defineInRange("faintExhaustion", 6.0, 0.0, 40.0);

    public static final ModConfigSpec.DoubleValue WARMTH_REFERENCE_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) at which Warmth is gained at exactly the base rate.")
            .defineInRange("warmthReferenceTemperature", 80.0, 1.0, 300.0);

    public static final ModConfigSpec.DoubleValue HUMIDITY_DECAY_PER_STEP = BUILDER
            .comment(
                    "Humidity (%) lost per simulation step as steam condenses.",
                    "Low enough that one ladle is felt for a good while — steam that evaporates in",
                    "seconds reads as doing nothing at all.")
            .defineInRange("humidityDecayPerStep", 0.5, 0.05, 100.0);

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

    public static final ModConfigSpec.DoubleValue CONTRAST_WARMTH = BUILDER
            .comment("Warmth a player must carry out of the parnaya for the plunge to count.")
            .defineInRange("contrastWarmth", 60.0, 0.0, 100.0);

    public static final ModConfigSpec.IntValue CONTRAST_WINDOW_STEPS = BUILDER
            .comment("Seconds after leaving the heat during which cold water still earns Hardening.")
            .defineInRange("contrastWindowSeconds", 30, 1, 600);

    public static final ModConfigSpec.IntValue CONTRAST_MAX_CYCLES = BUILDER
            .comment("How far the hot-cold loop can be stacked in one session.")
            .defineInRange("contrastMaxCycles", 3, 1, 10);

    public static final ModConfigSpec.IntValue CONTRAST_EFFECT_SECONDS = BUILDER
            .comment("Hardening duration granted per completed cycle, in seconds.")
            .defineInRange("contrastEffectSeconds", 60, 5, 3600);

    public static final ModConfigSpec.IntValue CONTRAST_CYCLE_MEMORY_TICKS = BUILDER
            .comment(
                    "Ticks after a plunge during which another one counts as the next lap.",
                    "A longer gap starts the cycle count over.")
            .defineInRange("contrastCycleMemoryTicks", 12000, 200, 240000);

    public static final ModConfigSpec.DoubleValue HEIGHT_BONUS = BUILDER
            .comment(
                    "Extra Warmth gain at the very top of the room, as a fraction.",
                    "0.3 means the ceiling warms you 30% faster than the floor — heat rises,",
                    "which is what makes a tiered polok worth building.")
            .defineInRange("heightBonus", 0.3, 0.0, 3.0);

    public static final ModConfigSpec.DoubleValue POLOK_BONUS = BUILDER
            .comment("Warmth gain multiplier while sitting on the polok.")
            .defineInRange("polokBonus", 1.15, 1.0, 3.0);

    public static final ModConfigSpec.IntValue FIREWOOD_PER_LOG = BUILDER
            .comment("How many pieces of firewood one log splits into.")
            .defineInRange("firewoodPerLog", 4, 1, 16);

    public static final ModConfigSpec.IntValue FIREWOOD_DRY_STEPS = BUILDER
            .comment("Seconds a rack takes to dry its load of firewood.")
            .defineInRange("firewoodDrySteps", 300, 5, 100000);

    public static final ModConfigSpec.DoubleValue SPARK_IGNITE_CHANCE = BUILDER
            .comment(
                    "Chance per second that burning spruce sets a fire next to the stove.",
                    "Authentic, and a real hazard in a wooden banya. Set to 0 to disable ignition;",
                    "the sparks still show as particles either way.")
            .defineInRange("sparkIgniteChance", 0.002, 0.0, 1.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
