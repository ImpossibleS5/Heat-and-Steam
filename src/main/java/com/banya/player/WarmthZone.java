package com.banya.player;

/**
 * Warmth bands from the design doc. Boundaries are inclusive at the lower end.
 * See the design notes, section 4.
 */
public enum WarmthZone {
    /** Below the useful range — nothing happens yet. */
    NEUTRAL,
    /** "Лёгкий пар" — gentle regeneration. */
    LIGHT_STEAM,
    /** "Глубокий прогрев" — stronger benefit, with a visual warning. */
    DEEP_WARMTH,
    /** Overheating — nausea, then fainting. */
    OVERHEAT;

    public static WarmthZone of(double warmth) {
        if (warmth >= 90.0) {
            return OVERHEAT;
        }
        if (warmth >= 70.0) {
            return DEEP_WARMTH;
        }
        if (warmth >= 30.0) {
            return LIGHT_STEAM;
        }
        return NEUTRAL;
    }
}
