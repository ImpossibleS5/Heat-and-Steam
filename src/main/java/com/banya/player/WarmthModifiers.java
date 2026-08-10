package com.banya.player;

import net.minecraft.world.entity.player.Player;

/**
 * Multipliers applied to the raw Warmth gain. Split out so gear and posture effects stay in one
 * place as they are added (felt hat, sitting on the polok, steam quality).
 */
public final class WarmthModifiers {
    /** Above this Warmth the felt hat starts protecting the wearer. */
    static final double HAT_PROTECTION_FROM = 70.0;
    /** How much of the gain the hat removes once past {@link #HAT_PROTECTION_FROM}. */
    static final double HAT_GAIN_REDUCTION = 0.6;

    private WarmthModifiers() {}

    /**
     * @param warmth the player's Warmth before this step
     * @return factor applied to the Warmth gained this step
     */
    public static double gainMultiplier(Player player, double warmth) {
        double multiplier = 1.0;
        if (warmth >= HAT_PROTECTION_FROM && FeltHat.isWornBy(player)) {
            multiplier *= (1.0 - HAT_GAIN_REDUCTION);
        }
        return multiplier;
    }
}
