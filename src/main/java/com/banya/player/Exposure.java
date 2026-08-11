package com.banya.player;

/**
 * What a player is currently exposed to, written by the owning stove each simulation step and
 * consumed by {@link PlayerWarmth}.
 *
 * @param inRoom    whether the player stands inside a stove's sealed room
 * @param heatIndex perceived heat of that room — temperature amplified by humidity, not raw deg C
 */
public record Exposure(boolean inRoom, double heatIndex) {
    /** Not inside any parnaya. */
    public static final Exposure NONE = new Exposure(false, 0.0);

    /** Combines two readings when rooms or stoves overlap; the hottest one wins. */
    public Exposure merge(double otherHeatIndex) {
        return new Exposure(true, Math.max(this.heatIndex, otherHeatIndex));
    }
}
