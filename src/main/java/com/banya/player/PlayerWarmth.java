package com.banya.player;

import com.banya.Config;
import com.banya.network.WarmthSyncPayload;
import com.banya.registry.ModAttachments;
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
    /** The blackout itself: long enough that it interrupts whatever you were doing. */
    private static final int FAINT_DURATION_TICKS = 120;
    /** The wrung-out feeling afterwards, which is the real cost of ignoring the heat. */
    private static final int FAINT_AFTERMATH_TICKS = 500;
    /** Warmth a player is left with after fainting: below Deep Warmth, so the climb starts over. */
    private static final double WARMTH_AFTER_FAINT = 45.0;

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

        syncToClient(player, (float) warmth, exposure.inRoom());
    }

    /**
     * Sends the HUD values only when they actually changed. Crucially that includes the change back
     * to "not in the banya": skipping that packet used to leave the bar stuck on screen at 0 after
     * stepping out of a cold room.
     */
    private static void syncToClient(ServerPlayer player, float warmth, boolean inBanya) {
        WarmthSync last = player.getData(ModAttachments.LAST_SYNC);
        if (!last.differsFrom(warmth, inBanya)) {
            return;
        }
        player.setData(ModAttachments.LAST_SYNC, new WarmthSync(warmth, inBanya));
        PacketDistributor.sendToPlayer(player, new WarmthSyncPayload(warmth, inBanya));
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
            case OVERHEAT -> {
                // Sitting it out has to be genuinely unpleasant, or the danger band is decoration.
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, EFFECT_DURATION_TICKS, 0, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION_TICKS, 1, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION_TICKS, 1, true, true));
                // The heat wrings you out: hunger drains rather than health.
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

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, FAINT_DURATION_TICKS, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FAINT_DURATION_TICKS, 4, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, FAINT_DURATION_TICKS, 0, false, true));

        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, FAINT_AFTERMATH_TICKS, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, FAINT_AFTERMATH_TICKS, 1, false, true));
        player.causeFoodExhaustion(Config.FAINT_EXHAUSTION.get().floatValue());

        player.displayClientMessage(
                Component.translatable("message.banya.faint").withStyle(ChatFormatting.RED), true);
    }

    private static double clamp(double warmth) {
        return Math.max(0.0, Math.min(MAX_WARMTH, warmth));
    }
}
