package com.banya.player;

/**
 * The last HUD values actually sent to a player's client, so the server only speaks up when
 * something changed — including the change to "nothing to show", which the HUD needs in order to
 * disappear.
 */
public record WarmthSync(float warmth, boolean inBanya) {
    /** What a fresh client already assumes, so this state needs no packet. */
    public static final WarmthSync NONE = new WarmthSync(0.0F, false);

    /** Warmth moves continuously; resend only once it has visibly shifted. */
    private static final float WARMTH_EPSILON = 0.5F;

    public boolean differsFrom(float otherWarmth, boolean otherInBanya) {
        return this.inBanya != otherInBanya || Math.abs(this.warmth - otherWarmth) >= WARMTH_EPSILON;
    }
}
