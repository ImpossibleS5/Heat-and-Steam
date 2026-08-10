package com.banya.stove;

import com.banya.Banya;
import com.banya.Config;
import com.banya.climate.RoomScanner;
import com.banya.climate.RoomShape;
import com.banya.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Owns the room microclimate for its enclosing parnaya. This sub-slice adds room detection;
 * temperature simulation is layered on next, consuming {@link #getRoom()}.
 */
public class StoveBlockEntity extends BlockEntity {
    /** Simulation advances once per second; the stove ticks every game tick and gates internally. */
    private static final int SIMULATION_INTERVAL_TICKS = 20;
    /**
     * MVP invalidation stand-in: re-scan the room every N simulation steps. Event-driven
     * invalidation (flag on block changes inside {@link RoomShape#bounds()}) is a later optimization;
     * {@link #markForRescan()} is already exposed for it. This is throttled, never per-tick.
     */
    private static final int RESCAN_EVERY_STEPS = 5;

    private int tickCounter;
    private int stepsSinceScan;
    private boolean needsRescan = true;

    @Nullable
    private RoomShape room;

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STOVE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StoveBlockEntity stove) {
        if (++stove.tickCounter < SIMULATION_INTERVAL_TICKS) {
            return;
        }
        stove.tickCounter = 0;

        if (stove.needsRescan) {
            stove.rescan(level);
        } else if (++stove.stepsSinceScan >= RESCAN_EVERY_STEPS) {
            stove.needsRescan = true;
        }

        // Temperature simulation step (consuming stove.room) is added in the next sub-slice.
    }

    private void rescan(Level level) {
        RoomShape previous = this.room;
        this.room = RoomScanner.scan(level, this.worldPosition, Config.MAX_ROOM_VOLUME.get());
        this.needsRescan = false;
        this.stepsSinceScan = 0;
        setChanged();

        boolean wasEnclosed = previous != null;
        boolean isEnclosed = this.room != null;
        if (wasEnclosed != isEnclosed || (isEnclosed && previous.volume() != this.room.volume())) {
            Banya.LOGGER.debug("Banya stove at {} room: {}", this.worldPosition,
                    isEnclosed ? this.room.volume() + " blocks" : "open/leaking");
        }
    }

    /** Force a room re-scan on the next simulation step (for future block-change hooks). */
    public void markForRescan() {
        this.needsRescan = true;
    }

    /** The current enclosed room, or {@code null} if the stove is open/leaking. */
    @Nullable
    public RoomShape getRoom() {
        return this.room;
    }
}
