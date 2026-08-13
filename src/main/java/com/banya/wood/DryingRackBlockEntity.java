package com.banya.wood;

import com.banya.Config;
import com.banya.item.FirewoodItem;
import com.banya.registry.ModBlockEntities;
import com.banya.registry.ModParticles;
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

/**
 * Dries one stack of firewood. Damp wood put here becomes proper dry firewood after a while; the
 * block's state mirrors the contents so the rack reads from across the yard.
 */
public class DryingRackBlockEntity extends BlockEntity {
    private static final int TICK_INTERVAL = 20;
    /** One stack of split wood is a sensible rack load. */
    public static final int CAPACITY = 16;
    /** Sky light with nothing but air — or glass — between the rack and the sun. */
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
            // A soaking undoes the whole drying, as it does in a real yard. isRainingAt already
            // accounts for a roof overhead and for biomes where it does not rain.
            if (this.dryProgress > 0) {
                this.dryProgress = 0;
                setChanged();
            }
            return;
        }

        int rate = dryingRate(level, pos);
        this.dryProgress += rate;
        if (level instanceof ServerLevel serverLevel && rate > 1) {
            // Only while the sun is actually on it: the wisp is the tell that this spot dries
            // faster than a shaded one, which is otherwise invisible until the wood is done.
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

    /**
     * Seconds of drying credited per real second: more in full sun, the plain rate in shade.
     *
     * <p>Sunlight is judged by sky <em>light</em>, not by {@code canSeeSky}. Glass blocks the sky
     * heightmap but has no light opacity, so a rack under a glass roof would otherwise dry as slowly
     * as one in a cellar — and a glazed drying shed is precisely the sensible thing to build. This
     * also gives the roof its due: under boards the rate is plain, and at night it is plain
     * everywhere.
     */
    private static int dryingRate(Level level, BlockPos pos) {
        boolean fullSun = level.isDay()
                && level.getBrightness(LightLayer.SKY, pos.above()) >= FULL_SKY_LIGHT;
        // Kept whole so dryProgress stays an int and old racks load unchanged.
        return fullSun ? Math.max(1, (int) Math.round(Config.FIREWOOD_SUN_DRY_MULTIPLIER.get())) : 1;
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
