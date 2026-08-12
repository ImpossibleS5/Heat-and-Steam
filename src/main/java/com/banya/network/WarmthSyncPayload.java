package com.banya.network;

import com.banya.Banya;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server -> owning client sync of the HUD values. Sent once per simulation step while there is
 * something to show, so the bar can be driven entirely client-side between updates.
 *
 * @param warmth        the player's Warmth, 0-100
 * @param strain        heat strain as a 0..1 fraction, so the client needs no server config
 * @param strainRising  whether the strain is still building, as opposed to wearing off
 * @param inBanya       whether the player is inside a parnaya
 */
public record WarmthSyncPayload(float warmth, float strain, boolean strainRising, boolean inBanya)
        implements CustomPacketPayload {
    public static final Type<WarmthSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Banya.MODID, "warmth_sync"));

    public static final StreamCodec<ByteBuf, WarmthSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WarmthSyncPayload::warmth,
            ByteBufCodecs.FLOAT, WarmthSyncPayload::strain,
            ByteBufCodecs.BOOL, WarmthSyncPayload::strainRising,
            ByteBufCodecs.BOOL, WarmthSyncPayload::inBanya,
            WarmthSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
