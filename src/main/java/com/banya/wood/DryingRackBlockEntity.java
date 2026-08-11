package com.banya.wood;

import com.banya.Config;
import com.banya.item.FirewoodItem;
import com.banya.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Dries one stack of firewood. Damp wood put here becomes proper dry firewood after a while; the
 * block's state mirrors the contents so the rack reads from across the yard.
 */
public class DryingRackBlockEntity extends BlockEntity {
    private static final int TICK_INTERVAL = 20;
    /** One stack of split wood is a sensible rack load. */
    public static final int CAPACITY = 16;

    private ItemStack contents = ItemStack.EMPTY;
    private int dryProgress;
    private int tickCounter;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK.get(), pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (++this.tickCounter < TICK_INTERVAL) {
            return;
        }
        this.tickCounter = 0;

        if (this.contents.isEmpty() || isDry()) {
            return;
        }
        if (++this.dryProgress < Config.FIREWOOD_DRY_STEPS.get()) {
            return;
        }
        FirewoodItem.setDry(this.contents, true);
        this.dryProgress = 0;
        setChanged();
        level.setBlockAndUpdate(pos, state.setValue(DryingRackBlock.STATE, RackState.DRY));
    }

    /** @return whether anything was taken from the player's stack */
    public boolean insert(ItemStack stack) {
        if (!(stack.getItem() instanceof FirewoodItem)) {
            return false;
        }
        if (this.contents.isEmpty()) {
            int moved = Math.min(stack.getCount(), CAPACITY);
            this.contents = stack.split(moved);
        } else if (ItemStack.isSameItemSameComponents(this.contents, stack)
                && this.contents.getCount() < CAPACITY) {
            int moved = Math.min(stack.getCount(), CAPACITY - this.contents.getCount());
            this.contents.grow(moved);
            stack.shrink(moved);
        } else {
            return false;
        }
        refreshState();
        return true;
    }

    public void takeAll(Player player) {
        if (this.contents.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(this.contents)) {
            player.drop(this.contents, false);
        }
        this.contents = ItemStack.EMPTY;
        this.dryProgress = 0;
        refreshState();
    }

    private boolean isDry() {
        return !this.contents.isEmpty() && FirewoodItem.isDry(this.contents);
    }

    private void refreshState() {
        setChanged();
        if (this.level == null) {
            return;
        }
        RackState state = this.contents.isEmpty()
                ? RackState.EMPTY
                : (isDry() ? RackState.DRY : RackState.DRYING);
        this.level.setBlock(this.worldPosition,
                getBlockState().setValue(DryingRackBlock.STATE, state), Block.UPDATE_ALL);
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.contents);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.contents.isEmpty()) {
            tag.put("Contents", this.contents.save(registries));
        }
        tag.putInt("DryProgress", this.dryProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.contents = tag.contains("Contents")
                ? ItemStack.parse(registries, tag.getCompound("Contents")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        this.dryProgress = tag.getInt("DryProgress");
    }
}
