package com.banya.player;

/**
 * What a player is currently exposed to, written by the owning stove each simulation step and
 * consumed by {@link PlayerWarmth}.
 *
 * @param inRoom         whether the player stands inside a stove's sealed room
 * @param heatIndex      perceived heat of that room — temperature amplified by humidity, not raw deg C
 * @param relativeHeight where the player stands in the room's vertical span, 0 at the floor and 1 at
 *                       the ceiling. Heat rises, so this is what makes building a polok worthwhile.
 */
public record Exposure(boolean inRoom, double heatIndex, double relativeHeight) {
    /** Not inside any parnaya. */
    public static final Exposure NONE = new Exposure(false, 0.0, 0.0);

    /** Combines two readings when rooms or stoves overlap; the hottest one wins. */
    public Exposure merge(double otherHeatIndex, double otherRelativeHeight) {
        if (otherHeatIndex <= this.heatIndex) {
            return new Exposure(true, this.heatIndex, this.relativeHeight);
        }
        return new Exposure(true, otherHeatIndex, otherRelativeHeight);
    }
}
