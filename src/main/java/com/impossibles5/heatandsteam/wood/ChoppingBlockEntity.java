package com.impossibles5.heatandsteam.wood;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import com.impossibles5.heatandsteam.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChoppingBlockEntity extends BlockEntity {
    private ItemStack log = ItemStack.EMPTY;

    public ChoppingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHOPPING_BLOCK.get(), pos, state);
    }

    public void setLog(ItemStack stack) {
        this.log = stack;
        setChanged();

        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(),
                    Block.UPDATE_CLIENTS);
        }
    }

    public ItemStack getLog() {
        return this.log;
    }

    public ItemStack splitLog() {
        if (this.log.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack firewood = new ItemStack(firewoodFor(this.log), Config.FIREWOOD_PER_LOG.get());

        com.impossibles5.heatandsteam.item.FirewoodItem.setDry(firewood, false);
        return firewood;
    }

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
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        if (tag.isEmpty()) {
            tag.putBoolean("Bare", true);
        }
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
