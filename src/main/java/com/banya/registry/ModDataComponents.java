package com.banya.registry;

import com.banya.Banya;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Data components carrying per-stack state. Stateful items use these rather than raw NBT. */
public final class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(Banya.MODID);

    /** Whether a ladle currently holds water. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FILLED =
            REGISTER.registerComponentType("filled", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    private ModDataComponents() {}
}
