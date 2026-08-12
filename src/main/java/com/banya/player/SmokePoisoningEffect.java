package com.banya.player;

import com.banya.Config;
import com.banya.registry.ModDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * "Угар" — breathing the smoke of a banya fired without a chimney, or one whose damper was shut too
 * early. It hurts steadily rather than all at once, which is what makes airing the room the
 * obvious answer.
 *
 * <p>The severity is in the size of the hit, not its frequency. Speeding the ticks up read as a
 * stutter of chip damage and hid how much worse a thick room actually was; one heavier hit on a
 * steady beat is legible. The damage bypasses armour, Resistance and Protection — see
 * {@link ModDamageTypes}.
 */
public class SmokePoisoningEffect extends MobEffect {
    private static final int COLOR = 0x5E5A55;

    public SmokePoisoningEffect() {
        super(MobEffectCategory.HARMFUL, COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        float damage = (float) (Config.SMOKE_DAMAGE.get() * (amplifier + 1));
        entity.hurt(ModDamageTypes.smokePoisoning(entity.level()), damage);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Same beat at every level, so the player can time getting out.
        return duration % Config.SMOKE_DAMAGE_INTERVAL_TICKS.get() == 0;
    }
}
