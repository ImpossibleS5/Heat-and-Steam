package com.impossibles5.heatandsteam.player;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModAttachments;
import com.impossibles5.heatandsteam.registry.ModEffects;
import com.impossibles5.heatandsteam.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

public final class ContrastTracker {
    private ContrastTracker() {}

    public static void tick(ServerPlayer player, Exposure exposure, double warmth) {
        if (exposure.inRoom() && warmth >= Config.CONTRAST_WARMTH.get()) {
            player.setData(ModAttachments.CONTRAST_WINDOW, Config.CONTRAST_WINDOW_STEPS.get());
            return;
        }

        int window = player.getData(ModAttachments.CONTRAST_WINDOW);
        if (window <= 0) {
            return;
        }
        if (isInCold(player)) {
            plunge(player);
            player.setData(ModAttachments.CONTRAST_WINDOW, 0);
            return;
        }
        player.setData(ModAttachments.CONTRAST_WINDOW, window - 1);
    }

    public static boolean isInCold(ServerPlayer player) {
        if (player.isInWater() || player.isInPowderSnow) {
            return true;
        }
        BlockPos pos = player.blockPosition();
        return player.level().getBlockState(pos).is(Blocks.SNOW)
                || player.level().getBlockState(pos).is(Blocks.POWDER_SNOW);
    }

    private static void plunge(ServerPlayer player) {
        long now = player.level().getGameTime();
        long since = now - player.getData(ModAttachments.LAST_PLUNGE);
        int previous = player.getData(ModAttachments.HARDENING_CYCLES);

        int cycles = since > Config.CONTRAST_CYCLE_MEMORY_TICKS.get()
                ? 1
                : Math.min(Config.CONTRAST_MAX_CYCLES.get(), previous + 1);

        player.setData(ModAttachments.HARDENING_CYCLES, cycles);
        player.setData(ModAttachments.LAST_PLUNGE, now);

        PlayerWarmth.set(player, PlayerWarmth.get(player) * 0.5);

        int duration = Config.CONTRAST_EFFECT_SECONDS.get() * 20 * cycles;
        Holder<MobEffect> hardening = ModEffects.HARDENING.getDelegate();
        player.addEffect(new MobEffectInstance(hardening, duration, cycles - 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, cycles - 1, false, true));

        player.playNotifySound(ModSounds.HARDENING.get(), SoundSource.PLAYERS, 0.8F, 1.4F);

        player.displayClientMessage(
                Component.translatable("message.heat_and_steam.hardening").withStyle(ChatFormatting.AQUA), true);
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.SNOWFLAKE,
                    player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.6, 0.4, 0.02);
        }
    }
}
