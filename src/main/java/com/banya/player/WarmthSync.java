package com.banya.player;

/**
 * The last HUD values actually sent to a player's client, so the server only speaks up when
 * something changed — including the change to "nothing to show", which the HUD needs in order to
 * disappear.
 */
public record WarmthSync(float warmth, float strain, boolean inBanya) {
    /** What a fresh client already assumes, so this state needs no packet. */
    public static final WarmthSync NONE = new WarmthSync(0.0F, 0.0F, false);

    /** These move continuously; resend only once they have visibly shifted. */
    private static final float WARMTH_EPSILON = 0.5F;
    private static final float STRAIN_EPSILON = 0.01F;

    public boolean differsFrom(float otherWarmth, float otherStrain, boolean otherInBanya) {
        return this.inBanya != otherInBanya
                || Math.abs(this.warmth - otherWarmth) >= WARMTH_EPSILON
                || Math.abs(this.strain - otherStrain) >= STRAIN_EPSILON;
    }
}
