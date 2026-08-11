package com.banya.player;

/**
 * Warmth bands from the design doc. Boundaries are inclusive at the lower end.
 * See the design notes, section 4.
 */
public enum WarmthZone {
    // Band boundaries are shared with the overheat strain model, hence the constants below.

    /** Below the useful range — nothing happens yet. */
    NEUTRAL,
    /** "Лёгкий пар" — gentle regeneration. */
    LIGHT_STEAM,
    /** "Глубокий прогрев" — stronger benefit, with a visual warning. */
    DEEP_WARMTH,
    /** Overheating — nausea, then fainting. */
    OVERHEAT;

    /** Where the danger band starts; strain only builds above this. */
    public static final double OVERHEAT_START = 90.0;
    public static final double DEEP_WARMTH_START = 70.0;
    public static final double LIGHT_STEAM_START = 30.0;

    public static WarmthZone of(double warmth) {
        if (warmth >= OVERHEAT_START) {
            return OVERHEAT;
        }
        if (warmth >= DEEP_WARMTH_START) {
            return DEEP_WARMTH;
        }
        if (warmth >= LIGHT_STEAM_START) {
            return LIGHT_STEAM;
        }
        return NEUTRAL;
    }
}
