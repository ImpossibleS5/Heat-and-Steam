package com.banya.registry;

import com.banya.Banya;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Particles the mod draws itself. */
public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Banya.MODID);

    /**
     * Пар. Vanilla's CLOUD stood in for it, but a cloud falls and thins where steam climbs, gathers
     * and hangs — the difference between a puff and a parnaya.
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STEAM =
            REGISTER.register("steam", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
