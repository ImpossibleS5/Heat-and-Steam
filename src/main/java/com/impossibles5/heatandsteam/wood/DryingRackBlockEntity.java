package com.impossibles5.heatandsteam.wood;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.item.FirewoodItem;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import com.impossibles5.heatandsteam.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DryingRackBlockEntity extends BlockEntity {
    private static final int TICK_INTERVAL = 20;

    public static final int CAPACITY = 16;

    private static final int FULL_SKY_LIGHT = 15;

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

        if (level.isRainingAt(pos.above())) {
            if (this.dryProgress > 0) {
                this.dryProgress = 0;
                setChanged();
            }
            return;
        }

        int rate = dryingRate(level, pos);
        this.dryProgress += rate;
        if (level instanceof ServerLevel serverLevel && rate > 1) {
            serverLevel.sendParticles(ModParticles.STEAM.get(),
                    pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 1, 0.2, 0.1, 0.2, 0.005);
        }
        if (this.dryProgress < Config.FIREWOOD_DRY_STEPS.get()) {
            return;
        }
        FirewoodItem.setDry(this.contents, true);
        this.dryProgress = 0;
        setChanged();
        level.setBlockAndUpdate(pos, state.setValue(DryingRackBlock.STATE, RackState.DRY));
    }

    private static int dryingRate(Level level, BlockPos pos) {
        boolean fullSun = level.isDay()
                && level.getBrightness(LightLayer.SKY, pos.above()) >= FULL_SKY_LIGHT;

        return fullSun ? Math.max(1, (int) Math.round(Config.FIREWOOD_SUN_DRY_MULTIPLIER.get())) : 1;
    }

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
