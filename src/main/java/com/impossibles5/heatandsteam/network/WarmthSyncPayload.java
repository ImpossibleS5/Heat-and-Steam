package com.impossibles5.heatandsteam.network;

import com.impossibles5.heatandsteam.HeatAndSteam;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WarmthSyncPayload(float warmth, float strain, boolean strainRising, boolean inSauna)
        implements CustomPacketPayload {
    public static final Type<WarmthSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "warmth_sync"));

    public static final StreamCodec<ByteBuf, WarmthSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WarmthSyncPayload::warmth,
            ByteBufCodecs.FLOAT, WarmthSyncPayload::strain,
            ByteBufCodecs.BOOL, WarmthSyncPayload::strainRising,
            ByteBufCodecs.BOOL, WarmthSyncPayload::inSauna,
            WarmthSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
