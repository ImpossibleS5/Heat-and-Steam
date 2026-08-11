package com.banya.player;

import com.banya.Config;
import com.banya.bath.PolokBlock;
import net.minecraft.world.entity.player.Player;

/**
 * Multipliers applied to the raw Warmth gain. Split out so gear and posture effects stay in one
 * place: the felt hat, how high you sit, and whether you are on the polok.
 */
public final class WarmthModifiers {
    /** Above this Warmth the felt hat starts protecting the wearer. */
    static final double HAT_PROTECTION_FROM = 70.0;
    /** How much of the gain the hat removes once past {@link #HAT_PROTECTION_FROM}. */
    static final double HAT_GAIN_REDUCTION = 0.6;

    private WarmthModifiers() {}

    /**
     * @param exposure this step's room reading, carrying how high the player stands
     * @param warmth   the player's Warmth before this step
     * @return factor applied to the Warmth gained this step
     */
    public static double gainMultiplier(Player player, Exposure exposure, double warmth) {
        // Heat rises: the air near the ceiling is the hottest seat in the house.
        double multiplier = 1.0 + exposure.relativeHeight() * Config.HEIGHT_BONUS.get();

        if (PolokBlock.isSittingOnPolok(player)) {
            multiplier *= Config.POLOK_BONUS.get();
        }
        if (warmth >= HAT_PROTECTION_FROM && FeltHat.isWornBy(player)) {
            multiplier *= (1.0 - HAT_GAIN_REDUCTION);
        }
        return multiplier;
    }
}
