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

    /**
     * Whisks left on a steeped venik. A count rather than a flag so one steeping lasts a bathing
     * session and the tub stays part of the loop instead of being used once.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STEEP_CHARGES =
            REGISTER.registerComponentType("steep_charges", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    /** Whether firewood has dried out on a rack and burns properly. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DRY =
            REGISTER.registerComponentType("dry", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    /**
     * Heat banked in a banya stone. Lives on the stack rather than the stove, so a stone pulled out
     * of a hot basket is still hot in your hand — and still hot in the next stove you put it in.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> HEAT =
            REGISTER.registerComponentType("heat", builder -> builder
                    .persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT));

    /**
     * Game time at which {@link #HEAT} was last accounted for.
     *
     * <p>Cooling is worked out from this on read rather than ticked down, because items in chests do
     * not tick at all — a chest was a perfect thermos. With a timestamp a stone cools correctly
     * wherever it sits, including in an unloaded chunk, at the cost of nothing per tick.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> HEAT_TIME =
            REGISTER.registerComponentType("heat_time", builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG));

    /** How far a stone has cracked. Visible on the texture; at the limit the stone is lost. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CRACKS =
            REGISTER.registerComponentType("cracks", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    private ModDataComponents() {}
}
