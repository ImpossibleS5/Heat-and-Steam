package com.impossibles5.heatandsteam.bath;

import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SeatEntity extends Entity {
    private static final double SEAT_HEIGHT = 0.4;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static boolean sit(Level level, BlockPos pos, net.minecraft.world.entity.Entity rider) {
        SeatEntity seat = new SeatEntity(ModEntities.SEAT.get(), level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + SEAT_HEIGHT, pos.getZ() + 0.5);
        if (!level.addFreshEntity(seat)) {
            return false;
        }
        return rider.startRiding(seat);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        boolean stillABench = this.level().getBlockState(this.blockPosition()).is(ModBlocks.SAUNA_BENCH.get());
        if (this.getPassengers().isEmpty() || !stillABench) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
