package com.impossibles5.heatandsteam.item;

public enum WoodSpecies {
    BIRCH(1200, 1.2, false),
    OAK(1800, 1.0, false),
    SPRUCE(800, 1.3, true);

    public static final double WET_BURN_FACTOR = 0.4;
    public static final double WET_HEAT_FACTOR = 0.6;

    private final int burnTicks;
    private final double heatFactor;
    private final boolean sparks;

    WoodSpecies(int burnTicks, double heatFactor, boolean sparks) {
        this.burnTicks = burnTicks;
        this.heatFactor = heatFactor;
        this.sparks = sparks;
    }

    public int burnTicks(boolean dry) {
        return dry ? this.burnTicks : (int) Math.round(this.burnTicks * WET_BURN_FACTOR);
    }

    public double heatFactor(boolean dry) {
        return dry ? this.heatFactor : this.heatFactor * WET_HEAT_FACTOR;
    }

    public boolean sparks() {
        return this.sparks;
    }
}
