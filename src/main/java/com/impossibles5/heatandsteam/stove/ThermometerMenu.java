package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.registry.ModBlocks;
import com.impossibles5.heatandsteam.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class ThermometerMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;

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

    public int getHeatIndex() {
        return this.data.get(StoveBlockEntity.DATA_HEAT_INDEX);
    }

    public boolean isSealed() {
        return this.data.get(StoveBlockEntity.DATA_SEALED) != 0;
    }

    public int getRoomVolume() {
        return this.data.get(StoveBlockEntity.DATA_ROOM_VOLUME);
    }

    public int getRoomWalls() {
        return this.data.get(StoveBlockEntity.DATA_ROOM_WALLS);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.THERMOMETER.get());
    }
}
