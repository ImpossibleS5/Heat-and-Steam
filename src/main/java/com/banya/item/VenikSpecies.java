package com.banya.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * What each kind of venik does to the person being whisked. Species differ in character rather than
 * raw strength, so choosing one is a preference and not a tier.
 */
public enum VenikSpecies {
    /** The all-rounder: keeps the light-steam glow going after you leave the parnaya. */
    BIRCH {
        @Override
        public void applyTo(LivingEntity target, double multiplier) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, scale(300, multiplier), 1, false, true));
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED, scale(300, multiplier), 0, false, true));
        }
    },
    /** Sturdier bundle: leaves you padded rather than glowing. */
    OAK {
        @Override
        public void applyTo(LivingEntity target, double multiplier) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.ABSORPTION, scale(600, multiplier), 1, false, true));
            target.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE, scale(200, multiplier), 0, false, true));
        }
    };

    /**
     * @param multiplier 1.0 when steaming yourself, more when someone else does it for you
     */
    public abstract void applyTo(LivingEntity target, double multiplier);

    static int scale(int ticks, double multiplier) {
        return (int) Math.round(ticks * multiplier);
    }
}
