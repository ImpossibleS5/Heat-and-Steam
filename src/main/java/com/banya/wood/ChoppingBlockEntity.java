package com.banya.wood;

import com.banya.Config;
import com.banya.registry.ModBlockEntities;
import com.banya.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Holds the single log currently sitting on the chopping block, so splitting it can yield firewood
 * of the matching species.
 */
public class ChoppingBlockEntity extends BlockEntity {
    private ItemStack log = ItemStack.EMPTY;

    public ChoppingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHOPPING_BLOCK.get(), pos, state);
    }

    public void setLog(ItemStack stack) {
        this.log = stack;
        setChanged();
    }

    public ItemStack getLog() {
        return this.log;
    }

    /** Turns the loaded log into damp firewood of the closest matching species. */
    public ItemStack splitLog() {
        if (this.log.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack firewood = new ItemStack(firewoodFor(this.log), Config.FIREWOOD_PER_LOG.get());
        // Straight off the block it is still damp; the drying rack fixes that.
        com.banya.item.FirewoodItem.setDry(firewood, false);
        return firewood;
    }

    /** Species is matched by the log item; anything unrecognised splits into birch. */
    private static net.minecraft.world.item.Item firewoodFor(ItemStack log) {
        if (log.is(Items.OAK_LOG) || log.is(Items.OAK_WOOD)
                || log.is(Items.DARK_OAK_LOG) || log.is(Items.DARK_OAK_WOOD)) {
            return ModItems.FIREWOOD_OAK.get();
        }
        if (log.is(Items.SPRUCE_LOG) || log.is(Items.SPRUCE_WOOD)) {
            return ModItems.FIREWOOD_SPRUCE.get();
        }
        return ModItems.FIREWOOD_BIRCH.get();
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.log);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.log.isEmpty()) {
            tag.put("Log", this.log.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.log = tag.contains("Log")
                ? ItemStack.parse(registries, tag.getCompound("Log")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }
}
