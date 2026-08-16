package com.impossibles5.heatandsteam.player;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.bath.SaunaBenchBlock;
import net.minecraft.world.entity.player.Player;

public final class WarmthModifiers {
    static final double HAT_PROTECTION_FROM = 70.0;

    static final double HAT_GAIN_REDUCTION = 0.6;

    private WarmthModifiers() {}

    public static double gainMultiplier(Player player, Exposure exposure, double warmth) {
        double multiplier = 1.0 + exposure.relativeHeight() * Config.HEIGHT_BONUS.get();

        if (SaunaBenchBlock.isSittingOnBench(player)) {
            multiplier *= Config.BENCH_BONUS.get();
        }
        if (warmth >= HAT_PROTECTION_FROM && FeltHat.isWornBy(player)) {
            multiplier *= (1.0 - HAT_GAIN_REDUCTION);
        }
        return multiplier;
    }
}
