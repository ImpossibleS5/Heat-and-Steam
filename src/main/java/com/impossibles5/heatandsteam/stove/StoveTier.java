package com.impossibles5.heatandsteam.stove;

public enum StoveTier {
    T1(4, 1.0, 1.0, 1.0),

    T2(6, 1.5, 1.1, 1.6),

    T3(8, 2.2, 1.2, 2.4);

    public static final int MAX_STONE_SLOTS = 8;

    private final int stoneSlots;
    private final double fuelFactor;
    private final double heatFactor;
    private final double roomFactor;

    StoveTier(int stoneSlots, double fuelFactor, double heatFactor, double roomFactor) {
        this.stoneSlots = stoneSlots;
        this.fuelFactor = fuelFactor;
        this.heatFactor = heatFactor;
        this.roomFactor = roomFactor;
    }

    public int stoneSlots() {
        return this.stoneSlots;
    }

    public double fuelFactor() {
        return this.fuelFactor;
    }

    public double heatFactor() {
        return this.heatFactor;
    }

    public double roomFactor() {
        return this.roomFactor;
    }
}
