package com.impossibles5.heatandsteam.player;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.network.WarmthSyncPayload;
import com.impossibles5.heatandsteam.registry.ModAttachments;
import com.impossibles5.heatandsteam.registry.ModEffects;
import com.impossibles5.heatandsteam.registry.ModTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PlayerWarmth {
    public static final double MAX_WARMTH = 100.0;

    private static final int EFFECT_DURATION_TICKS = 60;

    private static final int FAINT_DURATION_TICKS = 60;

    private static final int FAINT_AFTERMATH_TICKS = 500;

    private PlayerWarmth() {}

    public static double get(Player player) {
        return player.getData(ModAttachments.WARMTH);
    }

    public static void set(Player player, double warmth) {
        player.setData(ModAttachments.WARMTH, clamp(warmth));
    }

    public static void tick(ServerPlayer player) {
        Exposure exposure = player.getData(ModAttachments.EXPOSURE);

        player.setData(ModAttachments.EXPOSURE, Exposure.NONE);

        double warmth = get(player);
        double threshold = Config.WARMTH_THRESHOLD_TEMPERATURE.get();

        WarmthZone zoneBefore = WarmthZone.of(warmth);

        if (exposure.heatIndex() >= threshold) {
            double gain = gainFor(exposure.heatIndex(), threshold)
                    * WarmthModifiers.gainMultiplier(player, exposure, warmth);
            if (zoneBefore == WarmthZone.OVERHEAT) {
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

        if (zoneAfter != WarmthZone.NEUTRAL) {
            ModTriggers.FIRST_STEAM.get().trigger(player);
        }

        boolean inHeat = exposure.heatIndex() >= threshold;
        boolean strainRising = inHeat && warmth >= WarmthZone.OVERHEAT_START;
        double strain = updateStrain(player, warmth, inHeat);
        applyZoneEffects(player, WarmthZone.of(warmth), strain);
        applyOverheatDamage(player, strain, inHeat);

        ContrastTracker.tick(player, exposure, warmth);
        warmth = get(player);

        syncToClient(player, (float) warmth, strainFraction(strain), strainRising, exposure.inRoom());
    }

    private static double updateStrain(ServerPlayer player, double warmth, boolean inHeat) {
        double strain = player.getData(ModAttachments.HEAT_STRAIN);

        if (inHeat && warmth >= WarmthZone.OVERHEAT_START) {
            double intensity = (warmth - WarmthZone.OVERHEAT_START)
                    / (MAX_WARMTH - WarmthZone.OVERHEAT_START);
            strain = Math.min(Config.STRAIN_MAX.get(), strain + Config.STRAIN_GAIN.get() * intensity);
        } else {
            double recovery = Config.STRAIN_RECOVERY.get();
            if (ContrastTracker.isInCold(player)) {
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

    private static float strainFraction(double strain) {
        return (float) Math.min(1.0, strain / Math.max(1.0, Config.STRAIN_MAX.get()));
    }

    private static void warnOnEnteringOverheat(ServerPlayer player, WarmthZone before, WarmthZone after) {
        if (after == WarmthZone.OVERHEAT && before != WarmthZone.OVERHEAT) {
            player.displayClientMessage(
                    Component.translatable("message.heat_and_steam.overheat").withStyle(ChatFormatting.GOLD), true);
        }
    }

    private static void syncToClient(ServerPlayer player, float warmth, float strain,
                                     boolean rising, boolean inSauna) {
        WarmthSync last = player.getData(ModAttachments.LAST_SYNC);
        if (!last.differsFrom(warmth, strain, rising, inSauna)) {
            return;
        }
        player.setData(ModAttachments.LAST_SYNC, new WarmthSync(warmth, strain, rising, inSauna));
        PacketDistributor.sendToPlayer(player, new WarmthSyncPayload(warmth, strain, rising, inSauna));
    }

    private static double gainFor(double heatIndex, double threshold) {
        double reference = Config.WARMTH_REFERENCE_TEMPERATURE.get();
        double span = Math.max(1.0, reference - threshold);
        double intensity = (heatIndex - threshold) / span;
        return Config.WARMTH_GAIN_PER_STEP.get() * Math.max(0.0, intensity);
    }

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
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, EFFECT_DURATION_TICKS, 0, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION_TICKS, 1, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION_TICKS, 1, true, true));
                player.causeFoodExhaustion(Config.OVERHEAT_EXHAUSTION.get().floatValue());
            }
            case NEUTRAL -> {
            }
        }
    }

    private static void faint(ServerPlayer player) {
        player.stopRiding();

        player.setData(ModAttachments.HEAT_EXHAUSTED, true);

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, FAINT_DURATION_TICKS, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FAINT_DURATION_TICKS, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, FAINT_DURATION_TICKS, 0, false, true));

        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, FAINT_AFTERMATH_TICKS, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, FAINT_AFTERMATH_TICKS, 1, false, true));
        player.causeFoodExhaustion(Config.FAINT_EXHAUSTION.get().floatValue());

        player.displayClientMessage(
                Component.translatable("message.heat_and_steam.faint").withStyle(ChatFormatting.RED), true);

        if (player.hasEffect(ModEffects.SMOKE_POISONING.getDelegate())) {
            ModTriggers.CHOKED.get().trigger(player);
        }
    }

    private static double clamp(double warmth) {
        return Math.max(0.0, Math.min(MAX_WARMTH, warmth));
    }
}
