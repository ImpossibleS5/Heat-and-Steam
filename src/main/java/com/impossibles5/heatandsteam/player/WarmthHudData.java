package com.impossibles5.heatandsteam.player;

public final class WarmthHudData {
    private static float warmth;
    private static float strain;
    private static boolean strainRising;
    private static boolean inSauna;

    private WarmthHudData() {}

    public static void set(float warmth, float strain, boolean strainRising, boolean inSauna) {
        WarmthHudData.warmth = warmth;
        WarmthHudData.strain = strain;
        WarmthHudData.strainRising = strainRising;
        WarmthHudData.inSauna = inSauna;
    }

    public static boolean strainRising() {
        return strainRising;
    }

    public static float warmth() {
        return warmth;
    }

    public static float strain() {
        return strain;
    }

    public static boolean inSauna() {
        return inSauna;
    }

    public static void reset() {
        warmth = 0.0F;
        strain = 0.0F;
        strainRising = false;
        inSauna = false;
    }
}
