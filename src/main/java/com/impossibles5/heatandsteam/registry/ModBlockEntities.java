package com.impossibles5.heatandsteam.registry;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.stove.StoveBlockEntity;
import com.impossibles5.heatandsteam.stove.ThermometerBlockEntity;
import com.impossibles5.heatandsteam.wood.ChoppingBlockEntity;
import com.impossibles5.heatandsteam.wood.DryingRackBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HeatAndSteam.MODID);

    public static final Supplier<BlockEntityType<StoveBlockEntity>> STOVE = REGISTER.register(
            "stove",
            () -> BlockEntityType.Builder.of(StoveBlockEntity::new, ModBlocks.STOVE.get()).build(null));

    public static final Supplier<BlockEntityType<ChoppingBlockEntity>> CHOPPING_BLOCK = REGISTER.register(
            "chopping_block",
            () -> BlockEntityType.Builder.of(ChoppingBlockEntity::new, ModBlocks.CHOPPING_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK = REGISTER.register(
            "drying_rack",
            () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));

    public static final Supplier<BlockEntityType<ThermometerBlockEntity>> THERMOMETER = REGISTER.register(
            "thermometer",
            () -> BlockEntityType.Builder.of(ThermometerBlockEntity::new, ModBlocks.THERMOMETER.get()).build(null));

    private ModBlockEntities() {}
}
