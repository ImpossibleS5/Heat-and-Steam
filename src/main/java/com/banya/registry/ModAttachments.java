package com.banya.registry;

import com.banya.Banya;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/** Per-player state attached to the entity, persisted across sessions where it makes sense. */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTER =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Banya.MODID);

    /** Warmth (прогрев), 0-100. Persisted; deliberately not copied on death — dying cools you off. */
    public static final Supplier<AttachmentType<Double>> WARMTH = REGISTER.register(
            "warmth", () -> AttachmentType.builder(() -> 0.0D).serialize(Codec.DOUBLE).build());

    /**
     * Temperature the player is currently standing in, written by the owning stove each simulation
     * step and consumed by the player tick. Transient: it is re-derived every step, so persisting it
     * would only let a stale value leak across a reload.
     */
    public static final Supplier<AttachmentType<Double>> EXPOSURE = REGISTER.register(
            "exposure", () -> AttachmentType.builder(() -> 0.0D).build());

    private ModAttachments() {}
}
