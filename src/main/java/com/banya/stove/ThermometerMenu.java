package com.banya.stove;

import com.banya.registry.ModBlocks;
import com.banya.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * The thermometer's own screen. It carries no inventory at all — it is an instrument, and reads the
 * microclimate of whichever stove owns the room it hangs in.
 */
public class ThermometerMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;

    /** Client-side constructor used by the {@code MenuType} factory. */
    public ThermometerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, new SimpleContainerData(StoveBlockEntity.DATA_SIZE), ContainerLevelAccess.NULL);
    }

    public ThermometerMenu(int containerId, ContainerData data, ContainerLevelAccess access) {
        super(ModMenus.THERMOMETER.get(), containerId);
        this.data = data;
        this.access = access;
        this.addDataSlots(data);
    }

    public int getTemperature() {
        return this.data.get(StoveBlockEntity.DATA_TEMPERATURE);
    }

    public int getHumidity() {
        return this.data.get(StoveBlockEntity.DATA_HUMIDITY);
    }

    public int getSmoke() {
        return this.data.get(StoveBlockEntity.DATA_SMOKE);
    }

    /** Perceived heat: what the body actually reacts to. */
    public int getHeatIndex() {
        return this.data.get(StoveBlockEntity.DATA_HEAT_INDEX);
    }

    public boolean isSealed() {
        return this.data.get(StoveBlockEntity.DATA_SEALED) != 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // nothing to move: the instrument has no slots
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.THERMOMETER.get());
    }
}
