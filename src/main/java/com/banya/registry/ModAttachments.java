package com.banya.registry;

import com.banya.Banya;
import com.banya.player.Exposure;
import com.banya.player.WarmthSync;
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
     * The room reading for this step. Transient on purpose: it is re-derived every step, so
     * persisting it would only let a stale value leak across a reload.
     */
    public static final Supplier<AttachmentType<Exposure>> EXPOSURE = REGISTER.register(
            "exposure", () -> AttachmentType.builder(() -> Exposure.NONE).build());

    /**
     * Heat strain: the danger meter, deliberately separate from Warmth. It builds only near the top
     * of the Warmth range and drains only once the bather has cooled below the overheat band, so
     * blacking out cannot be used to shed the danger.
     */
    public static final Supplier<AttachmentType<Double>> HEAT_STRAIN = REGISTER.register(
            "heat_strain", () -> AttachmentType.builder(() -> 0.0D).serialize(Codec.DOUBLE).build());

    /** Whether the blackout for this strain episode has already happened, so it fires just once. */
    public static final Supplier<AttachmentType<Boolean>> HEAT_EXHAUSTED = REGISTER.register(
            "heat_exhausted", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    /** Last HUD values sent to this player's client, so packets only go out on a real change. */
    public static final Supplier<AttachmentType<WarmthSync>> LAST_SYNC = REGISTER.register(
            "last_sync", () -> AttachmentType.builder(() -> WarmthSync.NONE).build());

    /** Simulation steps left to reach cold water and earn Hardening. Transient by design. */
    public static final Supplier<AttachmentType<Integer>> CONTRAST_WINDOW = REGISTER.register(
            "contrast_window", () -> AttachmentType.builder(() -> 0).build());

    /** How many contrast laps the player has strung together, 1-3. */
    public static final Supplier<AttachmentType<Integer>> HARDENING_CYCLES = REGISTER.register(
            "hardening_cycles", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    /** Game time of the last plunge, used to tell a new session from the next lap. */
    public static final Supplier<AttachmentType<Long>> LAST_PLUNGE = REGISTER.register(
            "last_plunge", () -> AttachmentType.builder(() -> 0L).serialize(Codec.LONG).build());

    private ModAttachments() {}
}
