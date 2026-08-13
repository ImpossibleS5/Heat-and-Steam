package com.banya.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Steam off the stones.
 *
 * <p>It climbs hard at first, then tires and drifts sideways, growing and thinning as it goes —
 * which is what makes a parnaya feel like one rather than like a machine venting. No ceiling test:
 * the vertical speed is spent by mid-life anyway, so the spreading happens on its own wherever the
 * roof is.
 */
public class SteamParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected SteamParticle(ClientLevel level, double x, double y, double z,
                            double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;
        this.gravity = 0.0F;
        this.friction = 1.0F;
        this.xd = xSpeed * 0.1;
        this.yd = ySpeed * 0.1 + 0.06;
        this.zd = zSpeed * 0.1;
        this.lifetime = 40 + this.random.nextInt(30);
        this.quadSize *= 1.4F + this.random.nextFloat() * 0.6F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        float life = (float) this.age / this.lifetime;

        // Rises while it is hot, then loses the will and wanders.
        this.yd = this.yd * 0.93 + 0.0015;
        this.xd = (this.xd + (this.random.nextDouble() - 0.5) * 0.004) * 0.99;
        this.zd = (this.zd + (this.random.nextDouble() - 0.5) * 0.004) * 0.99;

        this.quadSize += 0.004F;
        this.alpha = 1.0F - life * life;
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed) {
            return new SteamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
