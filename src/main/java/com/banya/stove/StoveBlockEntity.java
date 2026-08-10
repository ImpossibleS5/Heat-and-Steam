package com.banya.stove;

import com.banya.Config;
import com.banya.climate.RoomClimate;
import com.banya.climate.RoomScanner;
import com.banya.climate.RoomShape;
import com.banya.registry.ModAttachments;
import com.banya.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Owns the room microclimate for its enclosing parnaya: burns fuel, tracks the room temperature,
 * and exposes both to the {@link StoveMenu}.
 */
public class StoveBlockEntity extends BlockEntity implements MenuProvider {
    /** Climate advances once per second; the stove ticks every game tick and gates internally. */
    private static final int SIMULATION_INTERVAL_TICKS = 20;
    /**
     * MVP invalidation stand-in: re-scan the room every N simulation steps. Event-driven
     * invalidation is a later optimization; {@link #markForRescan()} is already exposed for it.
     */
    private static final int RESCAN_EVERY_STEPS = 5;

    public static final int DATA_BURN_TIME = 0;
    public static final int DATA_BURN_TIME_TOTAL = 1;
    public static final int DATA_TEMPERATURE = 2;
    public static final int DATA_SIZE = 3;

    private final ItemStackHandler fuel = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** Ticks of burn time left on the current piece of fuel. */
    private int burnTime;
    /** Burn time the current piece of fuel started with, for the flame gauge. */
    private int burnTimeTotal;
    private double temperature = Config.AMBIENT_TEMPERATURE.get();

    private int tickCounter;
    private int stepsSinceScan;
    private boolean needsRescan = true;

    @Nullable
    private RoomShape room;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_BURN_TIME -> burnTime;
                case DATA_BURN_TIME_TOTAL -> burnTimeTotal;
                case DATA_TEMPERATURE -> (int) Math.round(temperature);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_BURN_TIME -> burnTime = value;
                case DATA_BURN_TIME_TOTAL -> burnTimeTotal = value;
                case DATA_TEMPERATURE -> temperature = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    };

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STOVE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StoveBlockEntity stove) {
        boolean wasBurning = stove.isBurning();

        if (stove.burnTime > 0) {
            stove.burnTime--;
        } else {
            stove.consumeFuel();
        }

        if (++stove.tickCounter >= SIMULATION_INTERVAL_TICKS) {
            stove.tickCounter = 0;
            stove.simulationStep(level);
        }

        if (wasBurning != stove.isBurning()) {
            level.setBlock(pos, state.setValue(StoveBlock.LIT, stove.isBurning()), Block.UPDATE_ALL);
            stove.setChanged();
        }
    }

    private void consumeFuel() {
        ItemStack stack = this.fuel.getStackInSlot(0);
        if (stack.isEmpty()) {
            this.burnTimeTotal = 0;
            return;
        }
        int burn = stack.getBurnTime(null);
        if (burn <= 0) {
            return; // not a fuel item; leave it for the player to take back out
        }
        this.burnTime = burn;
        this.burnTimeTotal = burn;
        stack.shrink(1);
        setChanged();
    }

    private void simulationStep(Level level) {
        if (this.needsRescan) {
            this.room = RoomScanner.scan(level, this.worldPosition, Config.MAX_ROOM_VOLUME.get());
            this.needsRescan = false;
            this.stepsSinceScan = 0;
        } else if (++this.stepsSinceScan >= RESCAN_EVERY_STEPS) {
            this.needsRescan = true;
        }

        this.temperature = RoomClimate.nextTemperature(this.temperature, isBurning(), this.room, level);
        exposeOccupants(level);
        setChanged();
    }

    /**
     * Publishes this room's temperature to the players standing in it. The stove is the one that
     * already knows the room, so pushing from here avoids every player searching for a stove.
     */
    private void exposeOccupants(Level level) {
        if (this.room == null) {
            return;
        }
        for (Player player : level.getEntitiesOfClass(Player.class, this.room.bounds())) {
            if (this.room.interior().contains(player.blockPosition())) {
                // Highest reading wins when rooms or stoves overlap.
                double current = player.getData(ModAttachments.EXPOSURE);
                player.setData(ModAttachments.EXPOSURE, Math.max(current, this.temperature));
            }
        }
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public double getTemperature() {
        return this.temperature;
    }

    /** The current enclosed room, or {@code null} if the stove is open/leaking. */
    @Nullable
    public RoomShape getRoom() {
        return this.room;
    }

    /** Force a room re-scan on the next simulation step (for future block-change hooks). */
    public void markForRescan() {
        this.needsRescan = true;
    }

    public ItemStackHandler getFuelHandler() {
        return this.fuel;
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.fuel.getStackInSlot(0));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.banya.stove");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new StoveMenu(containerId, playerInventory, this.fuel, this.data,
                ContainerLevelAccess.create(this.level, this.worldPosition));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Fuel", this.fuel.serializeNBT(registries));
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("BurnTimeTotal", this.burnTimeTotal);
        tag.putDouble("Temperature", this.temperature);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Fuel")) {
            this.fuel.deserializeNBT(registries, tag.getCompound("Fuel"));
        }
        this.burnTime = tag.getInt("BurnTime");
        this.burnTimeTotal = tag.getInt("BurnTimeTotal");
        this.temperature = tag.contains("Temperature")
                ? tag.getDouble("Temperature")
                : Config.AMBIENT_TEMPERATURE.get();
        this.needsRescan = true;
    }
}
