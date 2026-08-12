package com.banya.stove;

import com.banya.Config;
import com.banya.climate.RoomClimate;
import com.banya.climate.RoomScanner;
import com.banya.climate.RoomShape;
import com.banya.climate.Soot;
import com.banya.item.FirewoodItem;
import com.banya.player.Exposure;
import com.banya.registry.ModAttachments;
import com.banya.registry.ModBlockEntities;
import com.banya.registry.ModEffects;
import com.banya.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

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
    public static final int DATA_HUMIDITY = 3;
    public static final int DATA_SMOKE = 4;
    /** Perceived heat, so the thermometer need not know the server's humidity weighting. */
    public static final int DATA_HEAT_INDEX = 5;
    /** 1 when the room is sealed, 0 when the climate is leaking away. */
    public static final int DATA_SEALED = 6;
    public static final int DATA_SIZE = 7;

    /** The basket is sized for the biggest stove; smaller tiers simply refuse the extra slots. */
    public static final int STONE_SLOTS = StoveTier.MAX_STONE_SLOTS;
    /** Smoke effects are refreshed every step, so they need only outlive one interval. */
    private static final int SMOKE_EFFECT_TICKS = 60;

    private final ItemStackHandler fuel = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Tag rather than item class, so KubeJS and other mods can register their own firewood.
            return stack.is(ModTags.Items.FIREWOOD) && stack.getBurnTime(null) > 0;
        }
    };

    private final ItemStackHandler stones = new ItemStackHandler(STONE_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Slots past the tier's basket size stay shut until the stove is built up.
            return slot < StoveBlockEntity.this.tier.stoneSlots() && StoveStones.isStone(stack);
        }

        /**
         * One stone per slot. A каменка holds a handful of big stones, not a bucket of gravel, and
         * without this the basket's heat store and its lifetime both scaled with stack size — four
         * full stacks of soapstone would have held heat for hours and never needed replacing.
         */
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    /** How hard the fuel currently burning drives the room; 1.0 for anything but firewood. */
    private double fuelHeatFactor = 1.0;
    /** Whether the current fuel throws embers (spruce does). */
    private boolean fuelSparks;
    /** Whether the burning piece went in damp, which roughly doubles the smoke. */
    private boolean fuelWasWet;
    /** Ticks of smouldering left after the flames die. */
    private int emberTicks;
    /** Share of the walls blackened by smoke, recomputed when the room is rescanned. */
    private double sootFraction;
    /** What was above the stove on the last step, cached for readouts between scans. */
    private ChimneyState chimneyState = ChimneyState.NONE;
    /** How much stove is built around the firebox. Derived each step, never stored. */
    private StoveTier tier = StoveTier.T1;
    /** Set when a pour just cracked a stone, read once by the block to tell the player. */
    private boolean crackedThisPour;

    /** Ticks of burn time left on the current piece of fuel. */
    private int burnTime;
    /** Burn time the current piece of fuel started with, for the flame gauge. */
    private int burnTimeTotal;
    private double temperature = Config.AMBIENT_TEMPERATURE.get();
    /** Room humidity, 0-100. Raised by throwing water on the stones, decays by condensation. */
    private double humidity;
    /** Room smoke, 0-100. Made by the fire, cleared mainly by opening the place up. */
    private double smoke;

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
                case DATA_HUMIDITY -> (int) Math.round(humidity);
                case DATA_SMOKE -> (int) Math.round(smoke);
                case DATA_HEAT_INDEX -> (int) Math.round(
                        RoomClimate.heatIndex(temperature, humidity) * steamQuality());
                case DATA_SEALED -> room == null ? 0 : 1;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_BURN_TIME -> burnTime = value;
                case DATA_BURN_TIME_TOTAL -> burnTimeTotal = value;
                case DATA_TEMPERATURE -> temperature = value;
                case DATA_HUMIDITY -> humidity = value;
                case DATA_SMOKE -> smoke = value;
                default -> {
                    // Heat index and seal state are derived; the client only ever reads them.
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
        boolean hadEmbers = stove.hasEmbers();

        if (stove.burnTime > 0) {
            stove.burnTime--;
            if (stove.burnTime == 0) {
                // Flames out, coals still alive — the window in which the damper must not be shut.
                stove.emberTicks = Config.EMBER_TICKS.get();
            }
        } else {
            stove.consumeFuel();
            if (stove.emberTicks > 0) {
                stove.emberTicks--;
            }
        }

        if (++stove.tickCounter >= SIMULATION_INTERVAL_TICKS) {
            stove.tickCounter = 0;
            stove.simulationStep(level);
        }

        if (wasBurning != stove.isBurning() || hadEmbers != stove.hasEmbers()) {
            level.setBlock(pos, state
                    .setValue(StoveBlock.LIT, stove.isBurning())
                    .setValue(StoveBlock.EMBERS, stove.hasEmbers()), Block.UPDATE_ALL);
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
        // A bigger stove wrings far more out of the same log.
        burn = (int) Math.round(burn * this.tier.fuelFactor());
        this.burnTime = burn;
        this.burnTimeTotal = burn;
        // Species and dryness decide how hard this piece drives the room.
        this.fuelHeatFactor = stack.getItem() instanceof FirewoodItem firewood
                ? firewood.heatFactor(stack)
                : 1.0;
        this.fuelSparks = stack.getItem() instanceof FirewoodItem firewood2 && firewood2.species().sparks();
        this.fuelWasWet = stack.getItem() instanceof FirewoodItem && !FirewoodItem.isDry(stack);
        // Fresh flames supersede the old coals; without this the stove reported burning and
        // smouldering at once.
        this.emberTicks = 0;
        stack.shrink(1);
        setChanged();
    }

    private void simulationStep(Level level) {
        if (this.needsRescan) {
            this.room = RoomScanner.scan(level, this.worldPosition, Config.MAX_ROOM_VOLUME.get());
            this.needsRescan = false;
            this.stepsSinceScan = 0;
            // Counting soot walks every wall, so it rides along with the scan rather than each step.
            this.sootFraction = this.room == null ? 0.0 : Soot.fractionOf(level, this.room);
        } else if (++this.stepsSinceScan >= RESCAN_EVERY_STEPS) {
            this.needsRescan = true;
        }

        Direction facing = getBlockState().getValue(StoveBlock.FACING);
        this.tier = StoveStructure.detect(level, this.worldPosition, facing);
        ChimneyState chimney = Chimney.detect(level,
                StoveStructure.chimneyBases(this.worldPosition, facing));
        this.temperature = RoomClimate.nextTemperature(this.temperature, heatInputForStep(),
                this.room, level, RoomClimate.leakMultiplier(chimney));
        this.humidity = RoomClimate.nextHumidity(this.humidity, this.room);
        this.smoke = RoomClimate.nextSmoke(this.smoke, smokeOutputForStep(), this.room, chimney);
        this.chimneyState = chimney;
        seasonWalls(level, chimney);
        throwSparks(level);
        exposeOccupants(level);
        setChanged();
    }

    /**
     * Heat offered to the room this step. While the fire burns, part of it also charges the stones;
     * once it goes out the stones pay that heat back, which is what keeps a real каменка warm long
     * after the wood is gone.
     */
    private double heatInputForStep() {
        if (isBurning()) {
            StoveStones.charge(this.stones, Config.STONE_CHARGE_PER_STEP.get(), this.tier.capacityFactor());
            return Config.HEAT_PER_STEP.get() * this.fuelHeatFactor * this.tier.heatFactor();
        }
        // The stones pay their heat back, which is what keeps a каменка warm after the wood is gone.
        return StoveStones.release(this.stones, Config.STONE_RELEASE_PER_STEP.get());
    }

    /**
     * Spruce spits embers. They are mostly for show, but with a non-zero
     * {@code sparkIgniteChance} they can start a fire beside the stove — authentic, and the reason
     * that config value exists.
     */
    private void throwSparks(Level level) {
        if (!this.fuelSparks || !isBurning() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.sendParticles(ParticleTypes.LAVA,
                this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0,
                this.worldPosition.getZ() + 0.5, 2, 0.3, 0.1, 0.3, 0.0);

        double chance = Config.SPARK_IGNITE_CHANCE.get();
        if (chance <= 0.0 || serverLevel.random.nextDouble() >= chance) {
            return;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = this.worldPosition.relative(dir);
            if (level.getBlockState(side).isAir()
                    && level.getBlockState(side.below()).isFlammable(level, side.below(), Direction.UP)) {
                level.setBlockAndUpdate(side, Blocks.FIRE.defaultBlockState());
                return;
            }
        }
    }

    /**
     * Blackens the parnaya a little at a time while it is full of smoke and has no flue. This is
     * how a banya po-chornomu earns its patina — and its steam: {@link #steamQuality()} pays the
     * soot back as a bonus.
     */
    private void seasonWalls(Level level, ChimneyState chimney) {
        if (this.room == null || chimney != ChimneyState.NONE
                || this.smoke < Config.SOOT_SMOKE_LEVEL.get()
                || level.random.nextDouble() >= Config.SOOT_CHANCE_PER_STEP.get()) {
            return;
        }
        // One block at a time, chosen at random, so the blackening creeps rather than snaps on.
        List<BlockPos> walls = List.copyOf(this.room.walls());
        BlockPos wall = walls.get(level.random.nextInt(walls.size()));
        if (Soot.darken(level, wall)) {
            this.sootFraction = Soot.fractionOf(level, this.room);
        }
    }

    /**
     * How good the steam is here. A seasoned black banya beats anything with a chimney, which is
     * the reward the design promises for putting up with the smoke.
     */
    public double steamQuality() {
        if (this.chimneyState != ChimneyState.NONE) {
            return 1.0;
        }
        return 1.0 + this.sootFraction * Config.SOOT_STEAM_BONUS.get();
    }

    /**
     * Smoke made this step. Embers keep smouldering for a while after the flames die, which is what
     * makes shutting the damper a judgement call rather than a formality.
     */
    private double smokeOutputForStep() {
        double output = Config.SMOKE_PER_STEP.get();
        if (isBurning()) {
            return this.fuelWasWet ? output * Config.WET_SMOKE_MULTIPLIER.get() : output;
        }
        if (hasEmbers()) {
            return output * Config.EMBER_SMOKE_FRACTION.get();
        }
        return 0.0;
    }

    /** Coals still glowing: shut the damper now and the room fills with fumes. */
    public boolean hasEmbers() {
        return this.emberTicks > 0;
    }

    /** Whether the basket holds stones hot enough to flash water into light steam. */
    public boolean hasHotStones() {
        return StoveStones.totalHeat(this.stones) > 0;
    }

    /**
     * Throws a ladle of water onto the stones. Without hot stones you get "heavy steam": the
     * humidity still rises, but far less — the design's nudge to load the basket and heat it first.
     *
     * @return whether the steam was proper light steam
     */
    public boolean pourWater() {
        boolean lightSteam = hasHotStones() && this.temperature >= Config.STEAM_TEMPERATURE.get();
        double gain = Config.HUMIDITY_PER_LADLE.get();
        if (!lightSteam) {
            gain *= Config.HEAVY_STEAM_MULTIPLIER.get();
        }
        this.humidity = Math.min(100.0, this.humidity + gain);

        // Only a proper hot-stone pour is violent enough to crack anything.
        if (lightSteam && this.level != null
                && StoveStones.wearOne(this.stones, this.level.random, Config.STONE_POURS_PER_CRACK.get())) {
            this.crackedThisPour = true;
        }
        setChanged();
        return lightSteam;
    }

    /** Whether the last pour cost a stone, so the block can report it to the player. */
    public boolean consumeCrackedFlag() {
        boolean cracked = this.crackedThisPour;
        this.crackedThisPour = false;
        return cracked;
    }

    /**
     * Publishes this room's temperature to the players standing in it. The stove is the one that
     * already knows the room, so pushing from here avoids every player searching for a stove.
     */
    private void exposeOccupants(Level level) {
        if (this.room == null) {
            return;
        }
        // Steam quality rides on the heat index: better steam simply warms you better.
        double heatIndex = RoomClimate.heatIndex(this.temperature, this.humidity) * steamQuality();
        for (Player player : level.getEntitiesOfClass(Player.class, this.room.bounds())) {
            if (isInside(player)) {
                Exposure current = player.getData(ModAttachments.EXPOSURE);
                player.setData(ModAttachments.EXPOSURE,
                        current.merge(heatIndex, relativeHeightOf(player)));
                applySmokeTo(player);
            }
        }
    }

    /**
     * Smoke bites in two stages, as the design describes: first the eyes sting, then breathing it
     * starts doing damage. Both clear the moment the room is aired out.
     */
    private void applySmokeTo(Player player) {
        if (this.smoke < Config.SMOKE_STING_LEVEL.get()) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, SMOKE_EFFECT_TICKS, 0, true, false));

        double choke = Config.SMOKE_CHOKE_LEVEL.get();
        if (this.smoke < choke) {
            return;
        }
        // Thicker smoke works faster rather than hitting harder.
        int amplifier = (int) Math.min(2, (this.smoke - choke) / 15.0);
        player.addEffect(new MobEffectInstance(
                ModEffects.SMOKE_POISONING.getDelegate(), SMOKE_EFFECT_TICKS, amplifier, true, true));
    }

    /**
     * Whether the player counts as being in this room.
     *
     * <p>Checks the head as well as the feet: standing or sitting on a polok, a slab or a stair puts
     * the feet inside a solid cell, which the room scan classes as wall. Only testing the feet made
     * the bather drop out of their own banya the moment they sat down.
     */
    private boolean isInside(Player player) {
        if (this.room.interior().contains(player.blockPosition())) {
            return true;
        }
        return this.room.interior().contains(BlockPos.containing(player.getEyePosition()));
    }

    /** Where the player stands in the room's vertical span, 0 at the floor and 1 at the ceiling. */
    private double relativeHeightOf(Player player) {
        double minY = this.room.bounds().minY;
        double span = this.room.bounds().maxY - minY;
        if (span <= 1.0) {
            return 0.0;
        }
        return Math.clamp((player.getY() - minY) / span, 0.0, 1.0);
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public double getHumidity() {
        return this.humidity;
    }

    public double getSmoke() {
        return this.smoke;
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

    public ItemStackHandler getStoneHandler() {
        return this.stones;
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.fuel.getStackInSlot(0));
        for (int slot = 0; slot < this.stones.getSlots(); slot++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.stones.getStackInSlot(slot));
        }
    }

    /** The screen is titled after what has actually been built, so the tier is never a guess. */
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.banya.stove." + this.tier.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new StoveMenu(containerId, playerInventory, this.fuel, this.stones, this.data,
                ContainerLevelAccess.create(this.level, this.worldPosition), this.tier);
    }

    public StoveTier getTier() {
        return this.tier;
    }

    /** The synced climate values, shared with any thermometer reading this stove's room. */
    public ContainerData getClimateData() {
        return this.data;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Fuel", this.fuel.serializeNBT(registries));
        tag.put("Stones", this.stones.serializeNBT(registries));
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("EmberTicks", this.emberTicks);
        tag.putInt("BurnTimeTotal", this.burnTimeTotal);
        tag.putDouble("Temperature", this.temperature);
        tag.putDouble("Humidity", this.humidity);
        tag.putDouble("Smoke", this.smoke);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Fuel")) {
            this.fuel.deserializeNBT(registries, tag.getCompound("Fuel"));
        }
        if (tag.contains("Stones")) {
            this.stones.deserializeNBT(registries, tag.getCompound("Stones"));
        }
        this.burnTime = tag.getInt("BurnTime");
        this.emberTicks = tag.getInt("EmberTicks");
        this.burnTimeTotal = tag.getInt("BurnTimeTotal");
        this.temperature = tag.contains("Temperature")
                ? tag.getDouble("Temperature")
                : Config.AMBIENT_TEMPERATURE.get();
        this.humidity = tag.getDouble("Humidity");
        this.smoke = tag.getDouble("Smoke");
        this.needsRescan = true;
    }
}
