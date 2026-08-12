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

    public static final ModConfigSpec.DoubleValue OVERHEAT_GAIN_DAMPING = BUILDER
            .comment(
                    "Warmth gain multiplier once inside the overheat band.",
                    "Well below 1.0 on purpose: the body fights the heat, which buys the bather",
                    "time to notice the warning and get out instead of blacking out unannounced.")
            .defineInRange("overheatGainDamping", 0.25, 0.01, 1.0);

    public static final ModConfigSpec.DoubleValue OVERHEAT_DAMAGE = BUILDER
            .comment(
                    "Damage per second once heat strain has passed the fainting point.",
                    "Scales up as strain keeps climbing, so staying put gets worse, never better.")
            .defineInRange("overheatDamage", 1.0, 0.0, 20.0);

    public static final ModConfigSpec.DoubleValue STRAIN_GAIN = BUILDER
            .comment(
                    "Heat strain gained per second at full Warmth.",
                    "Strain is the danger meter, kept separate from Warmth so that passing out",
                    "cannot double as a way to clear the danger.")
            .defineInRange("strainGainPerStep", 5.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue STRAIN_RECOVERY = BUILDER
            .comment(
                    "Heat strain shed per second once Warmth is back below the overheat band.",
                    "Only cooling down clears it: neither fainting nor waiting it out in the heat will.")
            .defineInRange("strainRecoveryPerStep", 5.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue COLD_STRAIN_RECOVERY = BUILDER
            .comment(
                    "How much faster heat strain sheds while the bather is in cold water or snow.",
                    "The plunge already halves Warmth; this makes it clear the lingering danger too,",
                    "so the contrast ritual pays off twice.")
            .defineInRange("coldStrainRecoveryMultiplier", 3.0, 1.0, 20.0);

    public static final ModConfigSpec.DoubleValue STRAIN_FAINT = BUILDER
            .comment("Heat strain at which the bather blacks out, and past which the heat starts to hurt.")
            .defineInRange("strainFaintThreshold", 100.0, 1.0, 1000.0);

    public static final ModConfigSpec.DoubleValue STRAIN_MAX = BUILDER
            .comment("Ceiling on heat strain, which also caps how fast the damage ramps.")
            .defineInRange("strainMax", 200.0, 1.0, 2000.0);

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

    public static final ModConfigSpec.DoubleValue SMOKE_PER_STEP = BUILDER
            .comment("Smoke (%) the fire adds per simulation step while burning.")
            .defineInRange("smokePerStep", 1.2, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue WET_SMOKE_MULTIPLIER = BUILDER
            .comment("How much worse damp firewood smokes.")
            .defineInRange("wetSmokeMultiplier", 2.0, 1.0, 10.0);

    public static final ModConfigSpec.DoubleValue SMOKE_SETTLE_PER_STEP = BUILDER
            .comment("Smoke (%) that settles out per step in a closed room.")
            .defineInRange("smokeSettlePerStep", 0.3, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SMOKE_VENT_MULTIPLIER = BUILDER
            .comment(
                    "How much faster smoke clears once the room is open.",
                    "Opening the door is the whole answer to a banya fired without a chimney.")
            .defineInRange("smokeVentMultiplier", 12.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue CHIMNEY_VENT_MULTIPLIER = BUILDER
            .comment(
                    "How much faster smoke clears through an open flue.",
                    "Lower than throwing the door open, but it does not cost you the whole room.")
            .defineInRange("chimneyVentMultiplier", 8.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue CHIMNEY_HEAT_LOSS = BUILDER
            .comment(
                    "Heat loss multiplier while the damper is open.",
                    "An open flue is a hole in the roof — that is the trade for clean air.")
            .defineInRange("chimneyHeatLoss", 1.8, 1.0, 10.0);

    public static final ModConfigSpec.IntValue EMBER_TICKS = BUILDER
            .comment(
                    "How long coals keep smouldering after the flames die, in ticks.",
                    "Shut the damper during this window and the fumes back up into the room;",
                    "wait it out and the heat is yours to keep.")
            .defineInRange("emberTicks", 600, 0, 24000);

    public static final ModConfigSpec.DoubleValue EMBER_SMOKE_FRACTION = BUILDER
            .comment("Fraction of the normal smoke output that smouldering coals give off.")
            .defineInRange("emberSmokeFraction", 0.6, 0.0, 2.0);

    public static final ModConfigSpec.DoubleValue SOOT_SMOKE_LEVEL = BUILDER
            .comment("Smoke (%) a chimneyless room needs before its walls start to blacken.")
            .defineInRange("sootSmokeLevel", 40.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SOOT_CHANCE_PER_STEP = BUILDER
            .comment(
                    "Chance per second of blackening one more wall block in a smoky black banya.",
                    "Low on purpose: the patina should creep on over many firings, not appear at once.")
            .defineInRange("sootChancePerStep", 0.08, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue SOOT_STEAM_BONUS = BUILDER
            .comment(
                    "Steam quality bonus at fully blackened walls, in a banya with no chimney.",
                    "0.25 means a seasoned black banya warms you a quarter faster than any other.",
                    "This is the payoff for putting up with the smoke.")
            .defineInRange("sootSteamBonus", 0.25, 0.0, 3.0);

    public static final ModConfigSpec.DoubleValue SMOKE_STING_LEVEL = BUILDER
            .comment("Smoke (%) at which the bather's eyes start to sting.")
            .defineInRange("smokeStingLevel", 30.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SMOKE_CHOKE_LEVEL = BUILDER
            .comment("Smoke (%) at which breathing it starts to do harm.")
            .defineInRange("smokeChokeLevel", 60.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SMOKE_DAMAGE = BUILDER
            .comment(
                    "Damage one hit of Угар deals at level I, in half-hearts.",
                    "Levels multiply this rather than shortening the interval: a thicker room hits",
                    "harder on the same beat, which reads clearly. It bypasses armour, Resistance",
                    "and Protection — nothing you wear helps against carbon monoxide.")
            .defineInRange("smokeDamage", 1.0, 0.0, 100.0);

    public static final ModConfigSpec.IntValue SMOKE_DAMAGE_INTERVAL_TICKS = BUILDER
            .comment("Ticks between hits of Угар, the same at every level.")
            .defineInRange("smokeDamageIntervalTicks", 40, 1, 1200);

    public static final ModConfigSpec.IntValue CHIMNEY_MAX_HEIGHT = BUILDER
            .comment(
                    "How far above the stove the flue is followed looking for open sky.",
                    "Tall enough for any sensible bathhouse, short enough that the check stays trivial.")
            .defineInRange("chimneyMaxHeight", 32, 1, 320);

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

    public static final ModConfigSpec.IntValue STONE_COOLING_PER_STEP = BUILDER
            .comment(
                    "Heat a stone loses per second while out of a stove.",
                    "Slow enough that a hot stone can be carried to another banya, not so slow that",
                    "it stays warm forever in a chest.")
            .defineInRange("stoneCoolingPerStep", 2, 0, 1000);

    public static final ModConfigSpec.DoubleValue STONE_POURS_PER_CRACK = BUILDER
            .comment(
                    "Average hot-stone pours a quality-1 stone survives before it cracks and is lost.",
                    "Scaled by quality, so soapstone lasts four times as long as river stone.",
                    "With four stones in the basket that is roughly 60 pours for the cheapest rock",
                    "and 240 for the best — a few sessions versus a few dozen.")
            .defineInRange("stonePoursPerCrack", 15.0, 1.0, 100000.0);

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
