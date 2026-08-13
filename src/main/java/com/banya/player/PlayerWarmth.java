package com.banya.player;

import com.banya.Config;
import com.banya.network.WarmthSyncPayload;
import com.banya.registry.ModAttachments;
import com.banya.registry.ModEffects;
import com.banya.registry.ModTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Advances a player's Warmth (прогрев) one simulation step and applies the effects of the band they
 * land in. Driven by {@link WarmthEvents}; the exposure temperature is written by the owning stove.
 *
 * <p>Fainting is deliberately the soft variant agreed in the design: a blackout plus heavy slowness,
 * no damage — see concept section 9.
 */
public final class PlayerWarmth {
    public static final double MAX_WARMTH = 100.0;

    /** Effects are refreshed every step, so they only need to outlive one simulation interval. */
    private static final int EFFECT_DURATION_TICKS = 60;
    /**
     * The blackout: long enough to interrupt, short enough that the bather regains control and can
     * still stagger out. A longer one plus the damage that follows was simply a death sentence.
     */
    private static final int FAINT_DURATION_TICKS = 60;
    /** The wrung-out feeling afterwards, which is the real cost of ignoring the heat. */
    private static final int FAINT_AFTERMATH_TICKS = 500;

    private PlayerWarmth() {}

    public static double get(Player player) {
        return player.getData(ModAttachments.WARMTH);
    }

    public static void set(Player player, double warmth) {
        player.setData(ModAttachments.WARMTH, clamp(warmth));
    }

    /** Runs once per simulation step (1 second) for each server player. */
    public static void tick(ServerPlayer player) {
        Exposure exposure = player.getData(ModAttachments.EXPOSURE);
        // Consume the reading; the stove writes it again next step if the player is still inside.
        player.setData(ModAttachments.EXPOSURE, Exposure.NONE);

        double warmth = get(player);
        double threshold = Config.WARMTH_THRESHOLD_TEMPERATURE.get();

        WarmthZone zoneBefore = WarmthZone.of(warmth);

        if (exposure.heatIndex() >= threshold) {
            double gain = gainFor(exposure.heatIndex(), threshold)
                    * WarmthModifiers.gainMultiplier(player, exposure, warmth);
            if (zoneBefore == WarmthZone.OVERHEAT) {
                // The last stretch to a blackout is the slowest, so the warning has time to land.
                gain *= Config.OVERHEAT_GAIN_DAMPING.get();
            }
            warmth += gain;
        } else {
            warmth -= Config.WARMTH_DECAY_PER_STEP.get();
        }

        warmth = clamp(warmth);
        WarmthZone zoneAfter = WarmthZone.of(warmth);
        warnOnEnteringOverheat(player, zoneBefore, zoneAfter);
        set(player, warmth);

        // Every step spent warmed through, not only the step that crossed the line. As an edge it
        // was missed by anyone who was already warm when the check first ran — and the root
        // advancement going unearned hides the whole tree below it, since vanilla only draws
        // unfinished advancements within two steps of a finished one. The criterion is one-shot, so
        // repeating the call costs nothing.
        if (zoneAfter != WarmthZone.NEUTRAL) {
            ModTriggers.FIRST_STEAM.get().trigger(player);
        }

        boolean inHeat = exposure.heatIndex() >= threshold;
        boolean strainRising = inHeat && warmth >= WarmthZone.OVERHEAT_START;
        double strain = updateStrain(player, warmth, inHeat);
        applyZoneEffects(player, WarmthZone.of(warmth), strain);
        applyOverheatDamage(player, strain, inHeat);

        ContrastTracker.tick(player, exposure, warmth);
        warmth = get(player); // the plunge cools the bather, which is the way out of the danger

        syncToClient(player, (float) warmth, strainFraction(strain), strainRising, exposure.inRoom());
    }

    /**
     * Advances heat strain, the meter that actually decides how much trouble the bather is in.
     *
     * <p>It builds only near the very top of the Warmth range and drains only once they have cooled
     * below the overheat band. Crucially the blackout does not touch it: an earlier version reset
     * Warmth on fainting, which turned passing out into a way to clear the danger and produced a
     * sawtooth of faint, recover, faint. Now the only way out is to leave the heat — or take a
     * plunge, which halves Warmth and starts the strain draining.
     *
     * @return the strain after this step
     */
    private static double updateStrain(ServerPlayer player, double warmth, boolean inHeat) {
        double strain = player.getData(ModAttachments.HEAT_STRAIN);

        // Gated on still being in the heat, not merely on a high Warmth reading: Warmth takes a
        // while to fall, and stepping outside has to start helping immediately.
        if (inHeat && warmth >= WarmthZone.OVERHEAT_START) {
            double intensity = (warmth - WarmthZone.OVERHEAT_START)
                    / (MAX_WARMTH - WarmthZone.OVERHEAT_START);
            strain = Math.min(Config.STRAIN_MAX.get(), strain + Config.STRAIN_GAIN.get() * intensity);
        } else {
            double recovery = Config.STRAIN_RECOVERY.get();
            if (ContrastTracker.isInCold(player)) {
                // Cold water does what standing about cannot: it flushes the strain out fast.
                recovery *= Config.COLD_STRAIN_RECOVERY.get();
            }
            strain = Math.max(0.0, strain - recovery);
        }
        player.setData(ModAttachments.HEAT_STRAIN, strain);

        if (strain <= 0.0) {
            player.setData(ModAttachments.HEAT_EXHAUSTED, false);
        } else if (strain >= Config.STRAIN_FAINT.get()
                && !player.getData(ModAttachments.HEAT_EXHAUSTED)) {
            faint(player);
        }
        return strain;
    }

    /** Strain as a 0..1 fraction, so the HUD needs no knowledge of the configured ceiling. */
    private static float strainFraction(double strain) {
        return (float) Math.min(1.0, strain / Math.max(1.0, Config.STRAIN_MAX.get()));
    }

    /** One clear shout on crossing into the danger band, rather than a message every second. */
    private static void warnOnEnteringOverheat(ServerPlayer player, WarmthZone before, WarmthZone after) {
        if (after == WarmthZone.OVERHEAT && before != WarmthZone.OVERHEAT) {
            player.displayClientMessage(
                    Component.translatable("message.banya.overheat").withStyle(ChatFormatting.GOLD), true);
        }
    }

    /**
     * Sends the HUD values only when they actually changed. Crucially that includes the change back
     * to "not in the banya": skipping that packet used to leave the bar stuck on screen at 0 after
     * stepping out of a cold room.
     */
    private static void syncToClient(ServerPlayer player, float warmth, float strain,
                                     boolean rising, boolean inBanya) {
        WarmthSync last = player.getData(ModAttachments.LAST_SYNC);
        if (!last.differsFrom(warmth, strain, rising, inBanya)) {
            return;
        }
        player.setData(ModAttachments.LAST_SYNC, new WarmthSync(warmth, strain, rising, inBanya));
        PacketDistributor.sendToPlayer(player, new WarmthSyncPayload(warmth, strain, rising, inBanya));
    }

    /** Hotter rooms heat proportionally faster, scaled around the reference heat index. */
    private static double gainFor(double heatIndex, double threshold) {
        double reference = Config.WARMTH_REFERENCE_TEMPERATURE.get();
        double span = Math.max(1.0, reference - threshold);
        double intensity = (heatIndex - threshold) / span;
        return Config.WARMTH_GAIN_PER_STEP.get() * Math.max(0.0, intensity);
    }

    /**
     * Harm from staying in the heat once strain has passed the blackout point. It ramps with
     * strain, so remaining only ever gets worse, and it is conditioned on still being in the heat
     * rather than on Warmth: Warmth takes many seconds to fall, and walking out has to help at once.
     */
    private static void applyOverheatDamage(ServerPlayer player, double strain, boolean inHeat) {
        double faintPoint = Config.STRAIN_FAINT.get();
        if (!inHeat || strain < faintPoint) {
            return;
        }
        double ramp = 1.0 + (strain - faintPoint) / faintPoint;
        player.hurt(player.damageSources().onFire(), (float) (Config.OVERHEAT_DAMAGE.get() * ramp));
    }

    private static void applyZoneEffects(ServerPlayer player, WarmthZone zone, double strain) {
        switch (zone) {
            case LIGHT_STEAM -> player.addEffect(
                    new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION_TICKS, 0, true, false));
            case DEEP_WARMTH -> player.addEffect(
                    new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION_TICKS, 1, true, false));
            case OVERHEAT -> {
                // Sitting it out has to be genuinely unpleasant, or the danger band is decoration.
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, EFFECT_DURATION_TICKS, 0, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION_TICKS, 1, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION_TICKS, 1, true, true));
                player.causeFoodExhaustion(Config.OVERHEAT_EXHAUSTION.get().floatValue());
            }
            case NEUTRAL -> {
            }
        }
    }

    /**
     * Soft faint, as agreed: no damage. It still has to cost something, so the blackout is long
     * enough to interrupt, it knocks the bather off the polok, and it leaves them wrung out for a
     * while afterwards.
     */
    private static void faint(ServerPlayer player) {
        player.stopRiding();
        // From here on the heat bites until they have properly cooled down.
        player.setData(ModAttachments.HEAT_EXHAUSTED, true);

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, FAINT_DURATION_TICKS, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FAINT_DURATION_TICKS, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, FAINT_DURATION_TICKS, 0, false, true));

        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, FAINT_AFTERMATH_TICKS, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, FAINT_AFTERMATH_TICKS, 1, false, true));
        player.causeFoodExhaustion(Config.FAINT_EXHAUSTION.get().floatValue());

        player.displayClientMessage(
                Component.translatable("message.banya.faint").withStyle(ChatFormatting.RED), true);

        // Blacking out is one thing; blacking out in a room full of fumes is the story worth telling.
        if (player.hasEffect(ModEffects.SMOKE_POISONING.getDelegate())) {
            ModTriggers.CHOKED.get().trigger(player);
        }
    }

    private static double clamp(double warmth) {
        return Math.max(0.0, Math.min(MAX_WARMTH, warmth));
    }
}
