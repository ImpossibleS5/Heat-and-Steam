package com.banya.player;

/**
 * What a player is currently exposed to, written by the owning stove each simulation step and
 * consumed by {@link PlayerWarmth}.
 *
 * @param inRoom      whether the player stands inside a stove's sealed room
 * @param temperature that room's temperature in deg C
 */
public record Exposure(boolean inRoom, double temperature) {
    /** Not inside any parnaya. */
    public static final Exposure NONE = new Exposure(false, 0.0);

    /** Combines two readings when rooms or stoves overlap; the hottest one wins. */
    public Exposure merge(double otherTemperature) {
        return new Exposure(true, Math.max(this.temperature, otherTemperature));
    }
}
