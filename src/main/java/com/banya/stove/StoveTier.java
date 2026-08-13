package com.banya.stove;

/**
 * How much stove has been built around the firebox.
 *
 * <p>The trade-off runs the way the design describes: a bigger stove does not burn hotter so much as
 * it holds far more heat and asks for far less wood. "Slow to heat" falls out of that on its own —
 * a bigger stone mass simply takes longer to charge.
 *
 * @param stoneSlots how many stones the basket accepts
 * @param fuelFactor multiplier on how long a piece of firewood lasts
 * @param heatFactor multiplier on the temperature the firebox reaches, and on the heat reaching
 *                   the room directly from the fire
 */
public enum StoveTier {
    /** "Буржуйка": bare firebox. Quick, thirsty, and it cools as fast as it heats. */
    T1(4, 1.0, 1.0),
    /** "Кирпичная каменка": a course of masonry around the firebox. */
    T2(6, 1.5, 1.1),
    /** "Массивная печь": full brick body. Holds a banya warm well past a game day. */
    T3(8, 2.2, 1.2);

    /** Biggest basket any tier has; the handler is sized for this and gated per tier. */
    public static final int MAX_STONE_SLOTS = 8;

    private final int stoneSlots;
    private final double fuelFactor;
    private final double heatFactor;

    StoveTier(int stoneSlots, double fuelFactor, double heatFactor) {
        this.stoneSlots = stoneSlots;
        this.fuelFactor = fuelFactor;
        this.heatFactor = heatFactor;
    }

    /**
     * How much banya a tier holds is now simply how many stones it takes: heat lives in the rock at
     * a temperature, and thermal mass belongs to the rock rather than to the masonry around it.
     *
     * <p>That is why there is no longer a capacity multiplier, nor the flow multiplier that was
     * added to compensate for one. A T3 holds a parnaya warm for longer than a T1 because eight
     * soapstones are twice the mass of four, and because its hotter fire sends them up higher — not
     * because a table says so.
     */
    public int stoneSlots() {
        return this.stoneSlots;
    }

    public double fuelFactor() {
        return this.fuelFactor;
    }

    public double heatFactor() {
        return this.heatFactor;
    }
}
