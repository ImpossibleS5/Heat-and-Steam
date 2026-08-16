package com.impossibles5.heatandsteam.stove;

public final class DisplayClock {
    private static volatile long gameTime;

    private DisplayClock() {}

    public static void set(long time) {
        gameTime = time;
    }

    public static long gameTime() {
        return gameTime;
    }
}
