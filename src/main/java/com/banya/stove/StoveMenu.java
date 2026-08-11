package com.banya.stove;

import com.banya.registry.ModBlocks;
import com.banya.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Stove screen handler: one fuel slot plus the player inventory, and synced burn/temperature values.
 * Slot coordinates match the generated {@code textures/gui/stove.png} panel.
 */
public class StoveMenu extends AbstractContainerMenu {
    /** Fuel sits on the left of its row, with the flame gauge beside it and the readout to the right. */
    public static final int FUEL_SLOT_X = 44;
    public static final int FUEL_SLOT_Y = 45;
    /** Left edge of the stone row; four slots sit side by side from here. */
    public static final int STONE_SLOT_X = 52;
    public static final int STONE_SLOT_Y = 17;

    private static final int FUEL_SLOTS = 1;
    private static final int STONE_SLOTS = StoveBlockEntity.STONE_SLOTS;
    private static final int CONTAINER_SLOTS = FUEL_SLOTS + STONE_SLOTS;
    private static final int PLAYER_INVENTORY_START = CONTAINER_SLOTS;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_END = PLAYER_INVENTORY_END + 9;

    private final ContainerData data;
    private final ContainerLevelAccess access;

    /** Client-side constructor used by the {@code MenuType} factory; validity is enforced server-side. */
    public StoveMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(FUEL_SLOTS), new ItemStackHandler(STONE_SLOTS),
                new SimpleContainerData(StoveBlockEntity.DATA_SIZE), ContainerLevelAccess.NULL);
    }

    public StoveMenu(int containerId, Inventory playerInventory, IItemHandler fuel, IItemHandler stones,
                     ContainerData data, ContainerLevelAccess access) {
        super(ModMenus.STOVE.get(), containerId);
        this.data = data;
        this.access = access;

        this.addSlot(new SlotItemHandler(fuel, 0, FUEL_SLOT_X, FUEL_SLOT_Y));
        for (int slot = 0; slot < STONE_SLOTS; slot++) {
            this.addSlot(new SlotItemHandler(stones, slot, STONE_SLOT_X + slot * 18, STONE_SLOT_Y));
        }
        addPlayerInventory(playerInventory);
        this.addDataSlots(data);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    /** Ticks of burn time left on the current fuel item. */
    public int getBurnTime() {
        return this.data.get(StoveBlockEntity.DATA_BURN_TIME);
    }

    /** Burn time the current fuel item started with, or 0 when nothing is burning. */
    public int getBurnTimeTotal() {
        return this.data.get(StoveBlockEntity.DATA_BURN_TIME_TOTAL);
    }

    /** Room temperature in whole degrees C. */
    public int getTemperature() {
        return this.data.get(StoveBlockEntity.DATA_TEMPERATURE);
    }

    /** Room humidity, 0-100. */
    public int getHumidity() {
        return this.data.get(StoveBlockEntity.DATA_HUMIDITY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < PLAYER_INVENTORY_START) {
            // Stove -> player inventory
            if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player inventory -> whichever part of the stove accepts this item
            boolean stowed = StoveStones.isStone(stack)
                    ? this.moveItemStackTo(stack, FUEL_SLOTS, CONTAINER_SLOTS, false)
                    : this.moveItemStackTo(stack, 0, FUEL_SLOTS, false);
            if (!stowed) {
                // Nothing took it, so fall back to the usual inventory <-> hotbar swap
                if (index < PLAYER_INVENTORY_END) {
                    if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_END, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.STOVE.get());
    }
}
