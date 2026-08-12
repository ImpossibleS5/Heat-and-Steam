package com.banya.item;

import com.banya.Config;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
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
            extend(target, MobEffects.REGENERATION, scale(300, multiplier), 1);
            extend(target, MobEffects.MOVEMENT_SPEED, scale(300, multiplier), 0);
        }
    },
    /** Sturdier bundle: leaves you padded rather than glowing. */
    OAK {
        @Override
        public void applyTo(LivingEntity target, double multiplier) {
            extend(target, MobEffects.ABSORPTION, scale(600, multiplier), 1);
            extend(target, MobEffects.DAMAGE_RESISTANCE, scale(200, multiplier), 0);
        }
    };

    /**
     * @param multiplier 1.0 when steaming yourself, more when someone else does it for you
     */
    public abstract void applyTo(LivingEntity target, double multiplier);

    static int scale(int ticks, double multiplier) {
        return (int) Math.round(ticks * multiplier);
    }

    /**
     * Adds to what is already running instead of replacing it.
     *
     * <p>Plain {@code addEffect} keeps whichever instance lasts longer, so a second go with the
     * venik on a fresh effect did nothing at all — the bather saw the timer refuse to move. A proper
     * session is several rounds with breaks, so the rounds should add up.
     *
     * <p>Capped, or a bather with a full tub could bank an afternoon of Regeneration in one sitting.
     * A stronger effect already running is left alone rather than being cut down to ours.
     */
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

        int cap = Config.VENIK_MAX_EFFECT_SECONDS.get() * 20;
        target.addEffect(new MobEffectInstance(effect, Math.min(duration, cap), amplifier, false, true));
    }
}
