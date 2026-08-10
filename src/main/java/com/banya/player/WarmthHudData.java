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
    private static boolean inBanya;

    private WarmthHudData() {}

    public static void set(float warmth, boolean inBanya) {
        WarmthHudData.warmth = warmth;
        WarmthHudData.inBanya = inBanya;
    }

    public static float warmth() {
        return warmth;
    }

    public static boolean inBanya() {
        return inBanya;
    }

    /** Called when leaving a world so a stale bar cannot survive into the next session. */
    public static void reset() {
        warmth = 0.0F;
        inBanya = false;
    }
}
