package com.impossibles5.heatandsteam.item;

import com.impossibles5.heatandsteam.Config;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public enum WhiskSpecies {
    BIRCH {
        @Override
        public void applyTo(LivingEntity target, double multiplier) {
            extend(target, MobEffects.REGENERATION, scale(300, multiplier), 1);
            extend(target, MobEffects.MOVEMENT_SPEED, scale(300, multiplier), 0);
        }
    },

    OAK {
        @Override
        public void applyTo(LivingEntity target, double multiplier) {
            extend(target, MobEffects.ABSORPTION, scale(600, multiplier), 1);
            extend(target, MobEffects.DAMAGE_RESISTANCE, scale(200, multiplier), 0);
        }
    };

    public abstract void applyTo(LivingEntity target, double multiplier);

    static int scale(int ticks, double multiplier) {
        return (int) Math.round(ticks * multiplier);
    }

    private static void extend(LivingEntity target, Holder<MobEffect> effect, int ticks, int amplifier) {
        MobEffectInstance active = target.getEffect(effect);
        int duration = ticks;

        if (active != null) {
            if (active.getAmplifier() > amplifier) {
                return;
            }
            if (active.getAmplifier() == amplifier) {
                duration += active.getDuration();
            }
        }

        int cap = Config.WHISK_MAX_EFFECT_SECONDS.get() * 20;
        target.addEffect(new MobEffectInstance(effect, Math.min(duration, cap), amplifier, false, true));
    }
}
