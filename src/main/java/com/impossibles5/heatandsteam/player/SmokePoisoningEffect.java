package com.impossibles5.heatandsteam.player;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SmokePoisoningEffect extends MobEffect {
    private static final int COLOR = 0x5E5A55;

    public SmokePoisoningEffect() {
        super(MobEffectCategory.HARMFUL, COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.tickCount % Config.SMOKE_DAMAGE_INTERVAL_TICKS.get() == 0) {
            float damage = (float) (Config.SMOKE_DAMAGE.get() * (amplifier + 1));
            entity.hurt(ModDamageTypes.smokePoisoning(entity.level()), damage);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
