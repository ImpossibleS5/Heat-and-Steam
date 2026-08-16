package com.impossibles5.heatandsteam;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static { BUILDER.push("climate"); }

    public static final ModConfigSpec.IntValue MAX_ROOM_VOLUME = BUILDER
            .comment(
                    "Maximum enclosed volume (in blocks) the stove treats as a single steam room.",
                    "A space that floods past this cap is considered open/leaking and holds no microclimate.")
            .defineInRange("maxRoomVolume", 512, 8, 32768);

    public static final ModConfigSpec.DoubleValue AMBIENT_TEMPERATURE = BUILDER
            .comment("Baseline temperature (deg C) a room decays back to once the stove goes out.")
            .defineInRange("ambientTemperature", 20.0, -50.0, 60.0);

    public static final ModConfigSpec.DoubleValue MAX_TEMPERATURE = BUILDER
            .comment(
                    "Hard ceiling (deg C) on a steam room, whatever is burning in it.",
                    "A safety rail rather than a target: the room's real ceiling is set by the",
                    "firebox temperature and how much of it the walls hold on to.")
            .defineInRange("maxTemperature", 120.0, 30.0, 300.0);

    public static final ModConfigSpec.DoubleValue STOVE_ROOM_COEFFICIENT = BUILDER
            .comment(
                    "How readily the firebox itself drives the room, per second, per degree between",
                    "them. The room gains stoveRoomCoefficient x tierFactor x (T fire - T room)",
                    "degrees a second, so a stove roaring at 600 C heats far harder than one barely",
                    "alight -- and it stops of its own accord as the room catches up. Scaled by the",
                    "tier because a massive brick body radiates from a far bigger surface than a bare",
                    "firebox does. Replaces the old flat heatPerStep, which handed a room the same",
                    "three degrees a second whether the fire was at 100 C or 600.",
                    "Deliberately smaller than stoneRoomCoefficient: flames alone should get a",
                    "steam room warm, and only a loaded basket should get it steaming.")
            .defineInRange("stoveRoomCoefficient", 0.006, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue FIRE_TEMPERATURE = BUILDER
            .comment(
                    "Temperature (°C) a firebox burning dry oak settles at.",
                    "Scaled by the wood species and the stove's tier, and it is what the stones in",
                    "the basket climb towards: nothing in the stove ever gets hotter than its fire.",
                    "Coals hold a fraction of it during the ember window.")
            .defineInRange("fireTemperature", 500.0, 0.0, 3000.0);

    public static final ModConfigSpec.DoubleValue FIRE_TEMPERATURE_PER_STEP = BUILDER
            .comment(
                    "Degrees a second the firebox moves towards the temperature it is heading for.",
                    "Lighting a cold stove is not instant, and neither is it going out.")
            .defineInRange("fireTemperaturePerStep", 8.0, 0.1, 1000.0);

    public static final ModConfigSpec.DoubleValue LEAK_COEFFICIENT = BUILDER
            .comment(
                    "Fraction of the room's excess heat lost per second through a reference-sized",
                    "shell of perfect timber. Scaled up by how much shell the room actually has and",
                    "by what it is made of (glass leaks twice what logs do), and by an open damper.",
                    "Loss grows with how far above ambient the room already is, so a sauna settles at",
                    "an equilibrium rather than climbing to the cap. That equilibrium is now a blend:",
                    "T = (sum of conductance x source temperature + leak x ambient) / (sum + leak),",
                    "which is why a hotter fire and better walls both raise it, and neither alone can.")
            .defineInRange("leakCoefficient", 0.05, 0.001, 1.0);

    public static final ModConfigSpec.DoubleValue OPEN_ROOM_LEAK_MULTIPLIER = BUILDER
            .comment(
                    "How much faster an unsealed room loses heat and steam -- the door standing",
                    "open, or any other hole the scan can escape through. A multiple of",
                    "leakCoefficient, and applied over the room's thermal mass just as the sealed",
                    "case is: opening the door does not make a hall into a cupboard, it makes its",
                    "shell leak. Before that division a 218-block steam room lost ten times its sealed",
                    "rate rather than this multiple, and a look through the door emptied it.")
            .defineInRange("openRoomLeakMultiplier", 2.0, 1.0, 20.0);

    public static final ModConfigSpec.IntValue REFERENCE_VOLUME = BUILDER
            .comment(
                    "The sauna every other one is measured against, as a volume in blocks. 64 is a",
                    "4x4x4 steam room. It does two distinct jobs, one for each half of what 'size' means:",
                    "its volume is the air a stove can warm at full speed (twice the volume takes",
                    "twice as long to come up, and no lower), and the six faces of a cube that big --",
                    "96 blocks -- are the shell that leaks at exactly leakCoefficient. Raise it to",
                    "make halls both quicker to heat and cheaper to hold.")
            .defineInRange("referenceVolume", 64, 1, 32768);

    static { BUILDER.pop(); }

    static { BUILDER.push("warmth"); }

    public static final ModConfigSpec.DoubleValue WARMTH_THRESHOLD_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) at or above which a player starts gaining Warmth.")
            .defineInRange("warmthThresholdTemperature", 50.0, 0.0, 300.0);

    public static final ModConfigSpec.DoubleValue WARMTH_GAIN_PER_STEP = BUILDER
            .comment(
                    "Warmth gained per simulation step at the reference heat index.",
                    "Scales with how far the room's perceived heat is above the threshold, so a humid",
                    "steam room warms you markedly faster than a dry one at the same temperature.")
            .defineInRange("warmthGainPerStep", 0.8, 0.05, 50.0);

    public static final ModConfigSpec.DoubleValue WARMTH_DECAY_PER_STEP = BUILDER
            .comment("Warmth lost per simulation step while out of the heat.")
            .defineInRange("warmthDecayPerStep", 1.0, 0.05, 50.0);

    public static final ModConfigSpec.DoubleValue WARMTH_REFERENCE_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) at which Warmth is gained at exactly the base rate.")
            .defineInRange("warmthReferenceTemperature", 80.0, 1.0, 300.0);

    public static final ModConfigSpec.DoubleValue HEIGHT_BONUS = BUILDER
            .comment(
                    "Extra Warmth gain at the very top of the room, as a fraction.",
                    "0.3 means the ceiling warms you 30% faster than the floor: heat rises,",
                    "which is what makes a tiered bench worth building.")
            .defineInRange("heightBonus", 0.3, 0.0, 3.0);

    public static final ModConfigSpec.DoubleValue BENCH_BONUS = BUILDER
            .comment("Warmth gain multiplier while sitting on the bench.")
            .defineInRange("benchBonus", 1.15, 1.0, 3.0);

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

    static { BUILDER.pop(); }

    static { BUILDER.push("steam"); }

    public static final ModConfigSpec.DoubleValue HUMIDITY_DECAY_PER_STEP = BUILDER
            .comment(
                    "Humidity (%) lost per simulation step as steam condenses.",
                    "Low enough that one ladle is felt for a good while: steam that evaporates in",
                    "seconds reads as doing nothing at all.")
            .defineInRange("humidityDecayPerStep", 0.5, 0.05, 100.0);

    public static final ModConfigSpec.DoubleValue HUMIDITY_PER_LADLE = BUILDER
            .comment("Humidity (%) added by one ladle of water thrown onto hot stones.")
            .defineInRange("humidityPerLadle", 25.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue HUMIDITY_HEAT_WEIGHT = BUILDER
            .comment(
                    "How strongly humidity amplifies perceived heat.",
                    "At 1.0, 100% humidity makes a room feel twice as hot as its dry temperature,",
                    "so a moderate wet steam room out-heats a very hot dry sauna, as intended.")
            .defineInRange("humidityHeatWeight", 1.0, 0.0, 4.0);

    public static final ModConfigSpec.DoubleValue STEAM_TEMPERATURE = BUILDER
            .comment(
                    "Room temperature (deg C) the stove must reach for a proper light steam.",
                    "Below this a ladle produces heavy steam: much less humidity and no benefit.")
            .defineInRange("steamTemperature", 70.0, 0.0, 300.0);

    public static final ModConfigSpec.DoubleValue STEAM_STONE_TEMPERATURE = BUILDER
            .comment(
                    "Temperature (°C) the stones must be at to flash a ladle of water into steam.",
                    "Water only cracks into light steam off rock that is properly hot; below this it",
                    "merely boils off the surface, which is the heavy steam nobody wants.")
            .defineInRange("steamStoneTemperature", 150.0, 0.0, 2000.0);

    public static final ModConfigSpec.DoubleValue HEAVY_STEAM_MULTIPLIER = BUILDER
            .comment("Fraction of the normal humidity gained when the stones are too cold.")
            .defineInRange("heavySteamMultiplier", 0.4, 0.0, 1.0);

    static { BUILDER.pop(); }

    static { BUILDER.push("smoke"); }

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
                    "Opening the door is the whole answer to a sauna fired without a chimney.")
            .defineInRange("smokeVentMultiplier", 12.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue CHIMNEY_VENT_MULTIPLIER = BUILDER
            .comment(
                    "How much faster smoke clears through an open flue, as a multiple of settling.",
                    "This has to beat the worst the fire can make or the damper stops being a",
                    "damper: damp wood produces smokePerStep * wetSmokeMultiplier = 2.4 a step, and",
                    "at 12 the flue takes 3.6, so smoke always falls while the damper is open.",
                    "At 8 the two were exactly equal and an open damper cleared nothing at all.")
            .defineInRange("chimneyVentMultiplier", 12.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue CHIMNEY_VENT_FRACTION = BUILDER
            .comment(
                    "Share of the smoke present that an open flue carries off per step, on top of",
                    "the flat rate above. A flue exchanges air, so a room full of smoke clears far",
                    "faster than one with a wisp in it -- and the two terms can never cancel each",
                    "other exactly, whatever the rest is tuned to.")
            .defineInRange("chimneyVentFraction", 0.08, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue CHIMNEY_HEAT_LOSS = BUILDER
            .comment(
                    "Heat loss multiplier while the damper is open.",
                    "An open flue is a hole in the roof: that is the trade for clean air.")
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
            .comment(
                    "Smoke (%) a room needs before its walls start to blacken.",
                    "Only counts while the smoke has nowhere to go: no flue at all, or the damper",
                    "shut. Seasoning a sauna is therefore something you do on purpose, by firing it",
                    "closed, and no ordinary firing will blacken it by accident.")
            .defineInRange("sootSmokeLevel", 40.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SOOT_CHANCE_PER_STEP = BUILDER
            .comment(
                    "Chance per second of blackening one more wall block in a smoky smoke sauna.",
                    "Low on purpose: the patina should creep on over many firings, not appear at once.")
            .defineInRange("sootChancePerStep", 0.08, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue SOOT_BAND_LIGHT = BUILDER
            .comment(
                    "Share of ALL the room's walls that must be blackened for the first step of the",
                    "soot bonus. Counted against every wall, not only the timber among them: a stone",
                    "floor, the stove and a window never blacken, so 100 % is not a real target.",
                    "Steps rather than a sliding scale, so one more blackened plank cannot nudge the",
                    "temperature the bather is reading.")
            .defineInRange("sootBandLight", 0.15, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue SOOT_BAND_MEDIUM = BUILDER
            .comment("Share of the walls needed for the second step of the soot bonus.")
            .defineInRange("sootBandMedium", 0.30, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue SOOT_BAND_HEAVY = BUILDER
            .comment("Share of the walls needed for the full soot bonus.")
            .defineInRange("sootBandHeavy", 0.50, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue SOOT_STEAM_BONUS = BUILDER
            .comment(
                    "Steam quality bonus at the top soot step, while the smoke is not being vented.",
                    "0.25 means a seasoned smoke sauna warms you a quarter faster than any other.",
                    "This is the payoff for putting up with the smoke: open the damper and it goes.")
            .defineInRange("sootSteamBonus", 0.25, 0.0, 3.0);

    public static final ModConfigSpec.DoubleValue SOOT_INSULATION_BONUS = BUILDER
            .comment(
                    "Heat loss the room saves at the top soot step, as a fraction.",
                    "Soot on the timber is insulation in its own right, so unlike the steam bonus",
                    "this one holds whatever the damper is doing: it is a property of the walls.")
            .defineInRange("sootInsulationBonus", 0.15, 0.0, 0.9);

    public static final ModConfigSpec.DoubleValue SMOKE_STING_LEVEL = BUILDER
            .comment("Smoke (%) at which the bather's eyes start to sting.")
            .defineInRange("smokeStingLevel", 30.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SMOKE_CHOKE_LEVEL = BUILDER
            .comment("Smoke (%) at which breathing it starts to do harm.")
            .defineInRange("smokeChokeLevel", 60.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SMOKE_DAMAGE = BUILDER
            .comment(
                    "Damage one hit of smoke poisoning deals at level I, in half-hearts.",
                    "Levels multiply this rather than shortening the interval: a thicker room hits",
                    "harder on the same beat, which reads clearly. It bypasses armour, Resistance",
                    "and Protection: nothing you wear helps against carbon monoxide.")
            .defineInRange("smokeDamage", 1.0, 0.0, 100.0);

    public static final ModConfigSpec.IntValue SMOKE_DAMAGE_INTERVAL_TICKS = BUILDER
            .comment("Ticks between hits of smoke poisoning, the same at every level.")
            .defineInRange("smokeDamageIntervalTicks", 40, 1, 1200);

    static { BUILDER.pop(); }

    static { BUILDER.push("stones"); }

    public static final ModConfigSpec.DoubleValue STONE_THERMAL_MASS_PER_QUALITY = BUILDER
            .comment(
                    "Thermal mass a stone has per point of quality (low=1 .. talcochlorite=4).",
                    "Mass is not a tank: it is how slowly the stone's temperature moves. The same",
                    "number makes better rock slower to heat AND slower to give its heat back, which",
                    "is the whole trade talcochlorite is mined for. Both rates divide by it.")
            .defineInRange("stoneThermalMassPerQuality", 4.0, 0.1, 1000.0);

    public static final ModConfigSpec.DoubleValue STONE_HEATING_MODIFIER = BUILDER
            .comment(
                    "How hard the fire pushes a stone's temperature up, per second, before mass.",
                    "A stone gains stoneHeatingModifier / mass degrees a second and never passes the",
                    "firebox temperature: river stone (mass 4) climbs 3 °C/s, talcochlorite (mass 16)",
                    "0.75 °C/s. Each stone heats on its own: a full basket is not a slower basket.")
            .defineInRange("stoneHeatingModifier", 12.0, 0.1, 1000.0);

    public static final ModConfigSpec.DoubleValue STONE_COOLING_MODIFIER = BUILDER
            .comment(
                    "How fast a stone loses temperature outside a stove, per second, before mass.",
                    "Falls by stoneCoolingModifier / mass degrees a second wherever it is: carried,",
                    "dropped, or shut in a chest. Items in chests never tick, so cooling is worked",
                    "out from a timestamp when the stone is read: storage buys no time at all.",
                    "Stones in a basket are exempt; the stove models those itself.")
            .defineInRange("stoneCoolingModifier", 5.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue STONE_WATER_COOLING_MULTIPLIER = BUILDER
            .comment(
                    "How much faster a stone gives up its heat while sitting in a cooling fluid.",
                    "Which fluids count is the heat_and_steam:cools_stones tag, water by default. Lava is",
                    "deliberately not in it: dropping a stone in lava does not quench it.")
            .defineInRange("stoneWaterCoolingMultiplier", 20.0, 1.0, 1000.0);

    public static final ModConfigSpec.DoubleValue STONE_SCALD_TEMPERATURE = BUILDER
            .comment(
                    "Temperature (°C) past which a stone cannot be carried at all.",
                    "It sets light to whoever is holding it and lands on the floor, every second,",
                    "for as long as it is that hot. Picking it back up is allowed and simply repeats",
                    "the burn, so the only ways on are to wait it out or to quench it in water.",
                    "A flat figure, not a share of anything: 100 °C is 100 °C in any hand.")
            .defineInRange("stoneScaldTemperature", 100.0, 0.0, 2000.0);

    public static final ModConfigSpec.DoubleValue STONE_BURN_SECONDS = BUILDER
            .comment("Seconds of fire from holding a scalding stone.")
            .defineInRange("stoneBurnSeconds", 3.0, 0.0, 60.0);

    public static final ModConfigSpec.DoubleValue STONE_POURS_PER_CRACK = BUILDER
            .comment(
                    "Average hot-stone pours a quality-1 stone survives before it cracks and is lost.",
                    "Scaled by quality, so talcochlorite lasts four times as long as river stone.",
                    "With four stones in the basket that is roughly 60 pours for the cheapest rock",
                    "and 240 for the best: a few sessions versus a few dozen.")
            .defineInRange("stonePoursPerCrack", 15.0, 1.0, 100000.0);

    public static final ModConfigSpec.DoubleValue STONE_ROOM_COEFFICIENT = BUILDER
            .comment(
                    "How readily the basket gives its heat to the steam room.",
                    "The room gains stoneRoomCoefficient x (mass / stoneReferenceMass) x (T stones -",
                    "T room) degrees a second, and the stones lose exactly that heat back out of",
                    "their own temperature. One term, running whether or not the fire is lit: while",
                    "it burns the stones add to the fire, and once it dies they are all that is left",
                    "holding the room. That is what the basket is for -- which is why a full basket is",
                    "worth as much to the room as the flames are, and why this is no longer the",
                    "rounding error it was at 0.005 -- at which a basket glowing at 600 C handed a",
                    "steam room less warmth than the fire under it.")
            .defineInRange("stoneRoomCoefficient", 0.02, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue STONE_REFERENCE_MASS = BUILDER
            .comment(
                    "Thermal mass that counts as a full basket for the purpose of heating the room.",
                    "64 is four talcochlorite stones, a loaded T1. Twice the mass warms the room twice as",
                    "hard at the same temperature, which is why a bigger stove is worth building.")
            .defineInRange("stoneReferenceMass", 64.0, 1.0, 100000.0);

    public static final ModConfigSpec.DoubleValue STONE_POUR_COOLING = BUILDER
            .comment(
                    "Stone degrees one ladle of water takes out of the basket, before mass.",
                    "Flashing water into steam is what steam costs, so a pour cools the stones,",
                    "and a heavy hand with the ladle kills a stove, exactly as it does in a real",
                    "sauna. Divided by thermal mass like everything else the rock does: a ladle",
                    "knocks 50 °C off a river cobble and 12.5 off talcochlorite, which is the trade the",
                    "better rock is mined for turning up where the bather actually feels it.",
                    "Set to 0 for the old behaviour, where water was free.")
            .defineInRange("stonePourCooling", 200.0, 0.0, 10000.0);

    public static final ModConfigSpec.DoubleValue POUR_FIREBOX_COOLING = BUILDER
            .comment(
                    "Degrees a ladle takes off the firebox itself.",
                    "Smaller than what it takes from the stones: the water goes onto the stones,",
                    "not into the fire, and what reaches the firebox is spray and steam. The fire",
                    "climbs back at fireTemperaturePerStep, so this reads as a dip rather than a loss.")
            .defineInRange("pourFireboxCooling", 25.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue STONE_ENERGY_TO_DEGREES = BUILDER
            .comment(
                    "Stone degrees given up per degree the basket adds to the room, before mass.",
                    "Ties the two halves of the exchange together: raise it and the stones spend",
                    "themselves faster for the same warmth, lower it and they last longer. This and",
                    "stoneRoomCoefficient together set the afterglow: a full basket of andesite in",
                    "a T3 holds a shut steam room above steaming heat for about five minutes after the",
                    "flames die, and above bearable for a quarter of an hour.")
            .defineInRange("stoneEnergyToDegrees", 4.0, 0.1, 1000.0);

    static { BUILDER.pop(); }

    static { BUILDER.push("bath"); }

    public static final ModConfigSpec.DoubleValue TUB_STEEP_TEMPERATURE = BUILDER
            .comment("Room temperature (deg C) the tub's water must reach before a whisk can be soaked.")
            .defineInRange("tubSteepTemperature", 60.0, 0.0, 300.0);

    public static final ModConfigSpec.IntValue WHISK_SOAK_USES = BUILDER
            .comment(
                    "How many whisks one steeping is good for.",
                    "After that the whisk dries out and has to go back in the tub.")
            .defineInRange("whiskSoakUses", 4, 1, 64);

    public static final ModConfigSpec.DoubleValue WHISK_HEAT_INDEX = BUILDER
            .comment("Perceived heat the room must reach before a whisk can be used at all.")
            .defineInRange("whiskHeatIndex", 60.0, 0.0, 400.0);

    public static final ModConfigSpec.IntValue WHISK_CHANNEL_TICKS = BUILDER
            .comment("How long whisking takes, in ticks (20 = 1 second).")
            .defineInRange("whiskChannelTicks", 60, 5, 200);

    public static final ModConfigSpec.DoubleValue WHISK_OTHER_PLAYER_MULTIPLIER = BUILDER
            .comment(
                    "Effect multiplier when whisking someone else rather than yourself.",
                    "Above 1.0 this is the mod's nudge towards bathing together.")
            .defineInRange("whiskOtherPlayerMultiplier", 1.5, 1.0, 5.0);

    public static final ModConfigSpec.IntValue WHISK_MAX_EFFECT_SECONDS = BUILDER
            .comment(
                    "Ceiling on a whisk effect, in seconds, however many rounds are stacked onto it.",
                    "Repeat whisking now adds to the running timer instead of replacing it, which is",
                    "what a real session feels like; without a ceiling a tub of whisks would bank an",
                    "afternoon of Regeneration in one sitting.")
            .defineInRange("whiskMaxEffectSeconds", 120, 1, 100000);

    static { BUILDER.pop(); }

    static { BUILDER.push("contrast"); }

    public static final ModConfigSpec.DoubleValue CONTRAST_WARMTH = BUILDER
            .comment("Warmth a player must carry out of the steam room for the plunge to count.")
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

    static { BUILDER.pop(); }

    static { BUILDER.push("wood"); }

    public static final ModConfigSpec.IntValue FIREWOOD_PER_LOG = BUILDER
            .comment("How many pieces of firewood one log splits into.")
            .defineInRange("firewoodPerLog", 4, 1, 16);

    public static final ModConfigSpec.IntValue FIREWOOD_DRY_STEPS = BUILDER
            .comment("Seconds a rack takes to dry its load of firewood.")
            .defineInRange("firewoodDrySteps", 300, 5, 100000);

    public static final ModConfigSpec.DoubleValue FIREWOOD_SUN_DRY_MULTIPLIER = BUILDER
            .comment(
                    "How much faster a rack dries standing in full daylight rather than in shade.",
                    "Judged by sky light, so a glass roof still counts as sun: a glazed drying shed",
                    "is the sensible thing to build, and glass is not a blanket. Rain resets the",
                    "timer outright, which is what makes putting a roof on worth the trouble.",
                    "Rounded to a whole step, since drying progress is counted in seconds.")
            .defineInRange("firewoodSunDryMultiplier", 2.0, 1.0, 10.0);

    public static final ModConfigSpec.DoubleValue SPARK_IGNITE_CHANCE = BUILDER
            .comment(
                    "Chance per second that burning spruce sets a fire next to the stove.",
                    "Authentic, and a real hazard in a wooden sauna. Set to 0 to disable ignition;",
                    "the sparks still show as particles either way.")
            .defineInRange("sparkIgniteChance", 0.002, 0.0, 1.0);

    static { BUILDER.pop(); }

    static { BUILDER.push("compat"); }

    public static final ModConfigSpec.DoubleValue HARDENING_FREEZING_POINT_DROP = BUILDER
            .comment(
                    "How far one Hardening level lowers Cold Sweat's freezing point, in its MC units.",
                    "1 unit is about 23 °C, so 0.15 buys roughly three and a half degrees per cycle",
                    "and a full three-cycle session lets you stand about ten degrees more cold.",
                    "Ignored when Cold Sweat is not installed. 0 turns the hook off.")
            .defineInRange("hardeningFreezingPointDrop", 0.15, 0.0, 2.0);

    public static final ModConfigSpec.DoubleValue HARDENING_COLD_RESISTANCE = BUILDER
            .comment(
                    "Share of incoming cold damage one Hardening level blocks in Cold Sweat, 0-1.",
                    "Kept well under 1 on purpose: the sauna is a head start against the cold,",
                    "not a replacement for a coat. Ignored when Cold Sweat is not installed.")
            .defineInRange("hardeningColdResistance", 0.15, 0.0, 1.0);

    static { BUILDER.pop(); }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        if (STRAIN_FAINT.get() > STRAIN_MAX.get()) {
            HeatAndSteam.LOGGER.warn("Heat & Steam config: warmth.strainFaintThreshold ({}) is above warmth.strainMax"
                            + " ({}), so the strain bar will read full before anyone faints",
                    STRAIN_FAINT.get(), STRAIN_MAX.get());
        }
    }

    private Config() {}
}
