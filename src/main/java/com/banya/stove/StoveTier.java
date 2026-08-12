package com.banya.stove;

/**
 * How much stove has been built around the firebox.
 *
 * <p>The trade-off runs the way the design describes: a bigger stove does not burn hotter so much as
 * it holds far more heat and asks for far less wood. "Slow to heat" falls out of that on its own —
 * a bigger stone mass simply takes longer to charge.
 *
 * @param stoneSlots     how many stones the basket accepts
 * @param capacityFactor multiplier on what each stone can bank
 * @param fuelFactor     multiplier on how long a piece of firewood lasts
 * @param heatFactor     multiplier on the heat reaching the room
 */
public enum StoveTier {
    /** "Буржуйка": bare firebox. Quick, thirsty, and it cools as fast as it heats. */
    T1(4, 1.0, 1.0, 1.0),
    /** "Кирпичная каменка": a course of masonry around the firebox. */
    T2(6, 2.0, 1.5, 1.1),
    /** "Массивная печь": full brick body. Holds a banya warm well past a game day. */
    T3(8, 4.0, 2.2, 1.2);

    /** Biggest basket any tier has; the handler is sized for this and gated per tier. */
    public static final int MAX_STONE_SLOTS = 8;

    private final int stoneSlots;
    private final double capacityFactor;
    private final double fuelFactor;
    private final double heatFactor;

    StoveTier(int stoneSlots, double capacityFactor, double fuelFactor, double heatFactor) {
        this.stoneSlots = stoneSlots;
        this.capacityFactor = capacityFactor;
        this.fuelFactor = fuelFactor;
        this.heatFactor = heatFactor;
    }

    public int stoneSlots() {
        return this.stoneSlots;
    }

    public double capacityFactor() {
        return this.capacityFactor;
    }

    public double fuelFactor() {
        return this.fuelFactor;
    }

    public double heatFactor() {
        return this.heatFactor;
    }
}
