package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(HeatAndSteam.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FILLED =
            REGISTER.registerComponentType("filled", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STEEP_CHARGES =
            REGISTER.registerComponentType("soak_charges", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DRY =
            REGISTER.registerComponentType("dry", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> TEMPERATURE =
            REGISTER.registerComponentType("temperature", builder -> builder
                    .persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> TEMPERATURE_TIME =
            REGISTER.registerComponentType("temperature_time", builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CRACKS =
            REGISTER.registerComponentType("cracks", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    private ModDataComponents() {}
}
