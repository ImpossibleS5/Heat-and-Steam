package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.climate.StoveLocator;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ThermometerBlockEntity extends BlockEntity {
    private float shownTemperature = -1.0F;
    private float shownHumidity = -1.0F;
    private long shownAtMillis;

    public float[] easedFill(float easePerSecond) {
        long now = net.minecraft.Util.getMillis();
        float target0 = temperatureFill();
        float target1 = humidityFill();
        if (this.shownTemperature < 0.0F) {
            this.shownTemperature = target0;
            this.shownHumidity = target1;
        } else {
            float elapsed = Math.min(1.0F, (now - this.shownAtMillis) / 1000.0F);
            float step = 1.0F - (float) Math.exp(-easePerSecond * elapsed);
            this.shownTemperature += (target0 - this.shownTemperature) * step;
            this.shownHumidity += (target1 - this.shownHumidity) * step;
        }
        this.shownAtMillis = now;
        return new float[] {this.shownTemperature, this.shownHumidity};
    }

    private static final int INTERVAL_TICKS = 20;

    public static final float TEMPERATURE_SCALE = 120.0F;

    private float temperature;
    private float humidity;

    private boolean attached;

    private boolean sealed;

    private int ticks;

    public ThermometerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THERMOMETER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  ThermometerBlockEntity gauge) {
        if (++gauge.ticks < INTERVAL_TICKS) {
            return;
        }
        gauge.ticks = 0;

        StoveBlockEntity stove = StoveLocator.findNearest(level, pos);

        boolean attached = stove != null && ThermometerBlock.reads(stove, state, pos);
        float temperature = attached
                ? (float) stove.getTemperature()
                : Config.AMBIENT_TEMPERATURE.get().floatValue();
        float humidity = attached ? (float) stove.getHumidity() : 0.0F;
        boolean sealed = attached && stove.getRoom() != null;

        if (attached == gauge.attached
                && sealed == gauge.sealed
                && Math.round(temperature) == Math.round(gauge.temperature)
                && Math.round(humidity) == Math.round(gauge.humidity)) {
            return;
        }
        gauge.attached = attached;
        gauge.sealed = sealed;
        gauge.temperature = temperature;
        gauge.humidity = humidity;
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    public float temperatureFill() {
        float ambient = Config.AMBIENT_TEMPERATURE.get().floatValue();
        return Math.clamp((this.temperature - ambient) / (TEMPERATURE_SCALE - ambient), 0.0F, 1.0F);
    }

    public float humidityFill() {
        return Math.clamp(this.humidity / 100.0F, 0.0F, 1.0F);
    }

    public boolean isAttached() {
        return this.attached;
    }

    public boolean isSealed() {
        return this.sealed;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("Temperature", this.temperature);
        tag.putFloat("Humidity", this.humidity);
        tag.putBoolean("Attached", this.attached);
        tag.putBoolean("Sealed", this.sealed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.temperature = tag.getFloat("Temperature");
        this.humidity = tag.getFloat("Humidity");
        this.attached = tag.getBoolean("Attached");
        this.sealed = tag.getBoolean("Sealed");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
