package com.banya.player;

import com.banya.Config;
import com.banya.network.WarmthSyncPayload;
import com.banya.registry.ModAttachments;
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
    private static final int FAINT_DURATION_TICKS = 60;
    /** Warmth a player is left with after fainting, so they wake below the danger band. */
    private static final double WARMTH_AFTER_FAINT = 60.0;

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

        if (exposure.heatIndex() >= threshold) {
            warmth += gainFor(exposure.heatIndex(), threshold)
                    * WarmthModifiers.gainMultiplier(player, exposure, warmth);
        } else {
            warmth -= Config.WARMTH_DECAY_PER_STEP.get();
        }

        warmth = clamp(warmth);

        if (warmth >= MAX_WARMTH) {
            faint(player);
            warmth = WARMTH_AFTER_FAINT;
        } else {
            applyZoneEffects(player, WarmthZone.of(warmth));
        }

        set(player, warmth);
        ContrastTracker.tick(player, exposure, warmth);
        warmth = get(player); // the plunge may have cooled the player as a side effect

        // Nothing to draw when the player is cold and outside — skip the packet entirely.
        if (exposure.inRoom() || warmth > 0.0) {
            PacketDistributor.sendToPlayer(player, new WarmthSyncPayload((float) warmth, exposure.inRoom()));
        }
    }

    /** Hotter rooms heat proportionally faster, scaled around the reference heat index. */
    private static double gainFor(double heatIndex, double threshold) {
        double reference = Config.WARMTH_REFERENCE_TEMPERATURE.get();
        double span = Math.max(1.0, reference - threshold);
        double intensity = (heatIndex - threshold) / span;
        return Config.WARMTH_GAIN_PER_STEP.get() * Math.max(0.0, intensity);
    }

    private static void applyZoneEffects(ServerPlayer player, WarmthZone zone) {
        switch (zone) {
            case LIGHT_STEAM -> player.addEffect(
                    new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION_TICKS, 0, true, false));
            case DEEP_WARMTH -> player.addEffect(
                    new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION_TICKS, 1, true, false));
            case OVERHEAT -> player.addEffect(
                    new MobEffectInstance(MobEffects.CONFUSION, EFFECT_DURATION_TICKS, 0, true, true));
            case NEUTRAL -> {
            }
        }
    }

    /** Soft faint: the screen goes dark and the player can barely move, but takes no damage. */
    private static void faint(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, FAINT_DURATION_TICKS, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FAINT_DURATION_TICKS, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, FAINT_DURATION_TICKS, 0, false, true));
    }

    private static double clamp(double warmth) {
        return Math.max(0.0, Math.min(MAX_WARMTH, warmth));
    }
}
