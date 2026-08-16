package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.player.Exposure;
import com.impossibles5.heatandsteam.player.WarmthSync;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTER =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HeatAndSteam.MODID);

    public static final Supplier<AttachmentType<Double>> WARMTH = REGISTER.register(
            "warmth", () -> AttachmentType.builder(() -> 0.0D).serialize(Codec.DOUBLE).build());

    public static final Supplier<AttachmentType<Exposure>> EXPOSURE = REGISTER.register(
            "exposure", () -> AttachmentType.builder(() -> Exposure.NONE).build());

    public static final Supplier<AttachmentType<Double>> HEAT_STRAIN = REGISTER.register(
            "heat_strain", () -> AttachmentType.builder(() -> 0.0D).serialize(Codec.DOUBLE).build());

    public static final Supplier<AttachmentType<Boolean>> HEAT_EXHAUSTED = REGISTER.register(
            "heat_exhausted", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<WarmthSync>> LAST_SYNC = REGISTER.register(
            "last_sync", () -> AttachmentType.builder(() -> WarmthSync.NONE).build());

    public static final Supplier<AttachmentType<Integer>> CONTRAST_WINDOW = REGISTER.register(
            "contrast_window", () -> AttachmentType.builder(() -> 0).build());

    public static final Supplier<AttachmentType<Integer>> HARDENING_CYCLES = REGISTER.register(
            "hardening_cycles", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Long>> LAST_PLUNGE = REGISTER.register(
            "last_plunge", () -> AttachmentType.builder(() -> 0L).serialize(Codec.LONG).build());

    private ModAttachments() {}
}
