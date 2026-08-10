package com.banya.registry;

import com.banya.Banya;
import com.banya.stove.StoveBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/** BlockEntity type registry. */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Banya.MODID);

    public static final Supplier<BlockEntityType<StoveBlockEntity>> STOVE = REGISTER.register(
            "stove",
            () -> BlockEntityType.Builder.of(StoveBlockEntity::new, ModBlocks.STOVE.get()).build(null));

    private ModBlockEntities() {}
}
