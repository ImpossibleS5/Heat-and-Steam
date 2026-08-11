package com.banya.bath;

import com.banya.registry.ModBlocks;
import com.banya.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Invisible perch a player rides while sitting on the polok. Carries no state of its own and is
 * discarded the moment it is empty or its block disappears, so it can never litter the world.
 */
public class SeatEntity extends Entity {
    /** How high above the block the rider sits, matching the polok's slab height. */
    private static final double SEAT_HEIGHT = 0.4;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /** Places a seat at the given block and puts the rider on it. */
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
        boolean stillAPolok = this.level().getBlockState(this.blockPosition()).is(ModBlocks.POLOK.get());
        if (this.getPassengers().isEmpty() || !stillAPolok) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No synced state: the seat is a position and nothing else.
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
