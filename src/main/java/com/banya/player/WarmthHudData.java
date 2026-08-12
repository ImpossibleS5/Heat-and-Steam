package com.banya.player;

/**
 * Last values received from the server for the local player's HUD.
 *
 * <p>Deliberately free of client-only types so the payload handler in common code can write to it
 * without dragging {@code Minecraft} onto a dedicated server's classpath. Only the client ever
 * writes here; on a server it simply stays at its defaults.
 */
public final class WarmthHudData {
    private static float warmth;
    private static float strain;
    private static boolean strainRising;
    private static boolean inBanya;

    private WarmthHudData() {}

    public static void set(float warmth, float strain, boolean strainRising, boolean inBanya) {
        WarmthHudData.warmth = warmth;
        WarmthHudData.strain = strain;
        WarmthHudData.strainRising = strainRising;
        WarmthHudData.inBanya = inBanya;
    }

    /** Whether the strain is still building, as opposed to wearing off. */
    public static boolean strainRising() {
        return strainRising;
    }

    public static float warmth() {
        return warmth;
    }

    /** Heat strain as a 0..1 fraction of its ceiling. */
    public static float strain() {
        return strain;
    }

    public static boolean inBanya() {
        return inBanya;
    }

    /** Called when leaving a world so a stale bar cannot survive into the next session. */
    public static void reset() {
        warmth = 0.0F;
        strain = 0.0F;
        strainRising = false;
        inBanya = false;
    }
}
