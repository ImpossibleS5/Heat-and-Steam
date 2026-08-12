package com.banya.stove;

import com.banya.registry.ModBlocks;
import com.banya.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
    public static final int FUEL_SLOT_Y = 54;
    /** Stone basket: up to two rows of four, each row centred on the panel. */
    public static final int STONE_SLOT_Y = 16;
    public static final int STONE_COLUMNS = 4;
    /** Vanilla slot spacing, and the panel width the rows are centred in. */
    public static final int SLOT_PITCH = 18;
    public static final int PANEL_WIDTH = 176;

    private static final int FUEL_SLOTS = 1;

    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final int stoneSlotCount;
    // Slot index boundaries depend on how many basket slots this stove has, so they are per-menu.
    private final int playerInventoryStart;
    private final int playerInventoryEnd;
    private final int hotbarEnd;

    /** Client-side constructor; the stove's tier arrives in the extra data written on opening. */
    public StoveMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, new ItemStackHandler(FUEL_SLOTS),
                new ItemStackHandler(StoveTier.MAX_STONE_SLOTS),
                new SimpleContainerData(StoveBlockEntity.DATA_SIZE), ContainerLevelAccess.NULL,
                StoveTier.values()[Math.clamp(data.readByte(), 0, StoveTier.values().length - 1)]);
    }

    public StoveMenu(int containerId, Inventory playerInventory, IItemHandler fuel, IItemHandler stones,
                     ContainerData data, ContainerLevelAccess access, StoveTier tier) {
        super(ModMenus.STOVE.get(), containerId);
        this.data = data;
        this.access = access;
        this.stoneSlotCount = tier.stoneSlots();
        this.playerInventoryStart = FUEL_SLOTS + this.stoneSlotCount;
        this.playerInventoryEnd = this.playerInventoryStart + 27;
        this.hotbarEnd = this.playerInventoryEnd + 9;

        this.addSlot(new SlotItemHandler(fuel, 0, FUEL_SLOT_X, FUEL_SLOT_Y));
        // Only the slots this stove has. An unbuilt stove shows four, not eight with half crossed out.
        for (int slot = 0; slot < this.stoneSlotCount; slot++) {
            this.addSlot(new SlotItemHandler(stones, slot,
                    stoneSlotX(slot, this.stoneSlotCount), stoneSlotY(slot)));
        }
        addPlayerInventory(playerInventory);
        this.addDataSlots(data);
    }

    /** Rows are centred on the panel, so four, six and eight slots all sit square. */
    public static int stoneSlotX(int slot, int total) {
        int row = slot / STONE_COLUMNS;
        int inRow = slot % STONE_COLUMNS;
        int rowCount = Math.min(STONE_COLUMNS, total - row * STONE_COLUMNS);
        return (PANEL_WIDTH - rowCount * SLOT_PITCH) / 2 + inRow * SLOT_PITCH + 1;
    }

    public static int stoneSlotY(int slot) {
        return STONE_SLOT_Y + (slot / STONE_COLUMNS) * SLOT_PITCH;
    }

    public int getStoneSlotCount() {
        return this.stoneSlotCount;
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

    // The room's climate is not this screen's business — the thermometer reads that.

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < this.playerInventoryStart) {
            // Stove -> player inventory
            if (!this.moveItemStackTo(stack, this.playerInventoryStart, this.hotbarEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player inventory -> whichever part of the stove accepts this item
            boolean stowed = StoveStones.isStone(stack)
                    ? this.moveItemStackTo(stack, FUEL_SLOTS, this.playerInventoryStart, false)
                    : this.moveItemStackTo(stack, 0, FUEL_SLOTS, false);
            if (!stowed) {
                // Nothing took it, so fall back to the usual inventory <-> hotbar swap
                if (index < this.playerInventoryEnd) {
                    if (!this.moveItemStackTo(stack, this.playerInventoryEnd, this.hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, this.playerInventoryStart, this.playerInventoryEnd, false)) {
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
