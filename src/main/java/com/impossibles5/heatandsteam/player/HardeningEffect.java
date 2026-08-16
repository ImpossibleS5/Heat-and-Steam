package com.impossibles5.heatandsteam.player;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HardeningEffect extends MobEffect {
    private static final int COLOR = 0x8CC8EC;

    public HardeningEffect() {
        super(MobEffectCategory.BENEFICIAL, COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
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
