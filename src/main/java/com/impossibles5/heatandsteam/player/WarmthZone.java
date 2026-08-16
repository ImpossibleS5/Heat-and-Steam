package com.impossibles5.heatandsteam.player;

public enum WarmthZone {
    NEUTRAL,

    LIGHT_STEAM,

    DEEP_WARMTH,

    OVERHEAT;

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
