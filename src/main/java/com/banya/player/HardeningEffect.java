package com.banya.player;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * "Закалка" — the payoff for the hot-to-cold contrast. A hardened bather shrugs off the cold: the
 * effect keeps freezing at bay for its duration.
 *
 * <p>The extra hearts come from a vanilla Absorption applied alongside this, so the two decay
 * independently and Absorption's own HUD row stays meaningful.
 */
public class HardeningEffect extends MobEffect {
    private static final int COLOR = 0x8CC8EC;

    public HardeningEffect() {
        super(MobEffectCategory.BENEFICIAL, COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Shed any freezing accumulated from powder snow or an icy plunge.
        if (entity.isFullyFrozen() || entity.getTicksFrozen() > 0) {
            entity.setTicksFrozen(0);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
