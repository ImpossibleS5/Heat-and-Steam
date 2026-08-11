package com.banya.player;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * "Угар" — breathing the smoke of a banya fired without a chimney, or one whose damper was shut too
 * early. It hurts steadily rather than all at once, which is what makes airing the room the
 * obvious answer.
 */
public class SmokePoisoningEffect extends MobEffect {
    private static final int COLOR = 0x5E5A55;
    /** Ticks between hits at level I; higher levels bite proportionally faster. */
    private static final int BASE_INTERVAL = 40;
    private static final float DAMAGE = 1.0F;

    public SmokePoisoningEffect() {
        super(MobEffectCategory.HARMFUL, COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().magic(), DAMAGE);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = Math.max(5, BASE_INTERVAL / (amplifier + 1));
        return duration % interval == 0;
    }
}
