package com.impossibles5.heatandsteam.stove;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.climate.RoomClimate;
import com.impossibles5.heatandsteam.climate.RoomScanner;
import com.impossibles5.heatandsteam.climate.RoomShape;
import com.impossibles5.heatandsteam.climate.Soot;
import com.impossibles5.heatandsteam.item.FirewoodItem;
import com.impossibles5.heatandsteam.player.Exposure;
import com.impossibles5.heatandsteam.player.PlayerWarmth;
import com.impossibles5.heatandsteam.player.WarmthZone;
import com.impossibles5.heatandsteam.registry.ModAttachments;
import com.impossibles5.heatandsteam.registry.ModBlockEntities;
import com.impossibles5.heatandsteam.registry.ModEffects;
import com.impossibles5.heatandsteam.registry.ModParticles;
import com.impossibles5.heatandsteam.registry.ModTags;
import com.impossibles5.heatandsteam.registry.ModTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoveBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SIMULATION_INTERVAL_TICKS = 20;

    private static final int RESCAN_EVERY_STEPS = 1;

    public static final int DATA_BURN_TIME = 0;
    public static final int DATA_BURN_TIME_TOTAL = 1;
    public static final int DATA_TEMPERATURE = 2;
    public static final int DATA_HUMIDITY = 3;
    public static final int DATA_SMOKE = 4;

    public static final int DATA_HEAT_INDEX = 5;

    public static final int DATA_SEALED = 6;

    public static final int DATA_ROOM_VOLUME = 7;
    public static final int DATA_ROOM_WALLS = 8;

    public static final int DATA_FIRE_TEMPERATURE = 9;

    public static final int DATA_STONE_TEMPERATURE = 10;
    public static final int DATA_SIZE = 11;

    public static final int STONE_SLOTS = StoveTier.MAX_STONE_SLOTS;

    private static final int SMOKE_EFFECT_TICKS = 60;

    private static final int PARTICLE_RISE = 3;

    private static final float FLUE_SMOKE_CHANCE = 0.11F;

    private static final double EMBER_FIRE_FRACTION = 0.6;

    private final ItemStackHandler fuel = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
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
            return slot < StoveBlockEntity.this.tier.stoneSlots() && StoveStones.isStone(stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private double fuelHeatFactor = 1.0;

    private boolean fuelSparks;

    private boolean fuelWasWet;

    private int emberTicks;

    private double sootFraction;

    private ChimneyState chimneyState = ChimneyState.NONE;

    private double roomThermalMass = 1.0;

    @Nullable
    private RoomShape lastSealedRoom;

    @Nullable
    private BlockPos ventOutlet;

    private StoveTier tier = StoveTier.T1;

    private boolean crackedThisPour;

    private int burnTime;

    private int burnTimeTotal;
    private double temperature = Config.AMBIENT_TEMPERATURE.get();

    private double fireTemperature = Config.AMBIENT_TEMPERATURE.get();

    private double humidity;

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
                case DATA_ROOM_VOLUME -> room == null ? 0 : room.volume();
                case DATA_ROOM_WALLS -> room == null ? 0 : room.shell();
                case DATA_FIRE_TEMPERATURE -> (int) Math.round(fireTemperature);
                case DATA_STONE_TEMPERATURE -> Math.round(StoveStones.averageTemperature(stones));
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

        stove.showFlueSmoke(level);

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
            return;
        }

        burn = (int) Math.round(burn * this.tier.fuelFactor());
        this.burnTime = burn;
        this.burnTimeTotal = burn;

        this.fuelHeatFactor = stack.getItem() instanceof FirewoodItem firewood
                ? firewood.heatFactor(stack)
                : 1.0;
        this.fuelSparks = stack.getItem() instanceof FirewoodItem firewood2 && firewood2.species().sparks();
        this.fuelWasWet = stack.getItem() instanceof FirewoodItem && !FirewoodItem.isDry(stack);

        this.emberTicks = 0;
        stack.shrink(1);
        setChanged();
    }

    private void simulationStep(Level level) {
        if (this.needsRescan || ++this.stepsSinceScan >= RESCAN_EVERY_STEPS) {
            this.room = RoomScanner.scan(level, this.worldPosition, Config.MAX_ROOM_VOLUME.get());
            this.needsRescan = false;
            this.stepsSinceScan = 0;

            this.sootFraction = this.room == null ? 0.0 : Soot.fractionOf(level, this.room);
        }

        StoveStones.stampAll(this.stones, level.getGameTime());

        Direction facing = getBlockState().getValue(StoveBlock.FACING);
        this.tier = StoveStructure.detect(level, this.worldPosition, facing);

        double target = targetFireTemperature();
        double rate = Config.FIRE_TEMPERATURE_PER_STEP.get();
        if (target < this.fireTemperature) {
            rate /= this.tier.fuelFactor();
        }
        this.fireTemperature = RoomClimate.approach(this.fireTemperature, target, rate);
        StoveStones.heatTowards(this.stones, (float) this.fireTemperature,
                Config.STONE_HEATING_MODIFIER.get());
        ChimneyState chimney = Chimney.detect(level,
                StoveStructure.chimneyBases(level, this.worldPosition, facing));
        this.chimneyState = chimney;

        StoveBlockEntity owner = roomOwner(level);
        if (owner == this) {
            if (this.room != null) {
                this.roomThermalMass = RoomClimate.thermalMass(this.room);
                this.lastSealedRoom = this.room;
            }
            this.temperature = RoomClimate.nextTemperature(this.temperature, roomHeatInput(level),
                    this.room, this.roomThermalMass, level,
                    RoomClimate.leakMultiplier(chimney) * Soot.insulationMultiplier(this.sootFraction));
            this.humidity = RoomClimate.nextHumidity(this.humidity, this.room, this.roomThermalMass);
            this.smoke = RoomClimate.nextSmoke(this.smoke, roomSmokeOutput(level), this.room,
                    this.roomThermalMass, chimney);
            seasonWalls(level, chimney);
            exposeOccupants(level);
        } else {
            this.temperature = owner.temperature;
            this.humidity = owner.humidity;
            this.smoke = owner.smoke;
        }

        this.ventOutlet = chimney == ChimneyState.OPEN
                ? Chimney.ventOutlet(level,
                        StoveStructure.chimneyBases(level, this.worldPosition, facing))
                : null;
        showSteamOffStones(level);
        throwSparks(level);
        setChanged();
    }

    private double heatInputForStep(double roomTemperature) {
        double fromFire = Config.STOVE_ROOM_COEFFICIENT.get() * this.tier.roomFactor()
                * Math.max(0.0, this.fireTemperature - roomTemperature);
        return fromFire + StoveStones.giveToRoom(this.stones, roomTemperature);
    }

    private List<StoveBlockEntity> stovesInRoom(Level level) {
        if (this.room == null) {
            return List.of(this);
        }
        List<StoveBlockEntity> found = new ArrayList<>();
        for (BlockPos wall : this.room.walls()) {
            if (level.getBlockEntity(wall) instanceof StoveBlockEntity stove) {
                found.add(stove);
            }
        }

        if (!found.contains(this)) {
            found.add(this);
        }
        return found;
    }

    private StoveBlockEntity roomOwner(Level level) {
        StoveBlockEntity owner = this;
        for (StoveBlockEntity stove : stovesInRoom(level)) {
            if (stove.worldPosition.asLong() < owner.worldPosition.asLong()) {
                owner = stove;
            }
        }
        return owner;
    }

    private double roomHeatInput(Level level) {
        double total = 0.0;
        for (StoveBlockEntity stove : stovesInRoom(level)) {
            total += stove.heatInputForStep(this.temperature);
        }
        return total;
    }

    private double targetFireTemperature() {
        double full = Config.FIRE_TEMPERATURE.get() * this.fuelHeatFactor * this.tier.heatFactor();
        if (isBurning()) {
            return full;
        }
        if (hasEmbers()) {
            return full * EMBER_FIRE_FRACTION;
        }
        return this.temperature;
    }

    private void showFlueSmoke(Level level) {
        BlockPos top = this.ventOutlet;
        if (top == null || !(isBurning() || hasEmbers())
                || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RandomSource random = serverLevel.random;

        if (random.nextFloat() >= (isBurning() ? FLUE_SMOKE_CHANCE : FLUE_SMOKE_CHANCE / 4.0F)) {
            return;
        }
        for (int puff = random.nextInt(2) + 2; puff > 0; puff--) {
            double x = top.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
            double y = top.getY() + 1.0 + random.nextDouble();
            double z = top.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(player, ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
                        x, y, z, 0, 0.0, 1.0, 0.0, 0.07);
            }
        }
    }

    @Nullable
    public static BlockPos particleCellAbove(Level level, BlockPos firebox) {
        for (int offset = 1; offset <= PARTICLE_RISE; offset++) {
            BlockPos pos = firebox.above(offset);
            if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return pos;
            }
        }
        return null;
    }

    private void showSteamOffStones(Level level) {
        if (isBurning() || !(level instanceof ServerLevel serverLevel)
                || StoveStones.averageTemperature(this.stones) <= this.temperature) {
            return;
        }
        BlockPos cell = particleCellAbove(level, this.worldPosition);
        if (cell == null) {
            return;
        }
        serverLevel.sendParticles(ModParticles.STEAM.get(),
                cell.getX() + 0.5, cell.getY() + 0.1, cell.getZ() + 0.5, 1, 0.25, 0.05, 0.25, 0.01);
    }

    private void throwSparks(Level level) {
        if (!this.fuelSparks || !isBurning() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos cell = particleCellAbove(level, this.worldPosition);
        if (cell != null) {
            serverLevel.sendParticles(ParticleTypes.LAVA,
                    cell.getX() + 0.5, cell.getY() + 0.1, cell.getZ() + 0.5, 2, 0.3, 0.1, 0.3, 0.0);
        }

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

    private void seasonWalls(Level level, ChimneyState chimney) {
        if (this.room == null || chimney == ChimneyState.OPEN
                || this.smoke < Config.SOOT_SMOKE_LEVEL.get()
                || level.random.nextDouble() >= Config.SOOT_CHANCE_PER_STEP.get()) {
            return;
        }

        List<BlockPos> walls = List.copyOf(this.room.walls());
        BlockPos wall = walls.get(level.random.nextInt(walls.size()));
        if (Soot.darken(level, wall)) {
            this.sootFraction = Soot.fractionOf(level, this.room);
        }
    }

    public double steamQuality() {
        if (this.chimneyState == ChimneyState.OPEN) {
            return 1.0;
        }
        return 1.0 + Soot.bonusFactor(this.sootFraction) * Config.SOOT_STEAM_BONUS.get();
    }

    private double roomSmokeOutput(Level level) {
        double total = 0.0;
        for (StoveBlockEntity stove : stovesInRoom(level)) {
            total += stove.smokeOutputForStep();
        }
        return total;
    }

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

    public boolean hasEmbers() {
        return this.emberTicks > 0;
    }

    public boolean hasHotStones() {
        return StoveStones.averageTemperature(this.stones) >= Config.STEAM_STONE_TEMPERATURE.get();
    }

    public boolean pourWater() {
        boolean lightSteam = hasHotStones() && this.temperature >= Config.STEAM_TEMPERATURE.get();
        double gain = Config.HUMIDITY_PER_LADLE.get();
        if (!lightSteam) {
            gain *= Config.HEAVY_STEAM_MULTIPLIER.get();
        }

        StoveBlockEntity owner = this.level == null ? this : roomOwner(this.level);

        owner.humidity = Math.min(100.0, owner.humidity + gain / owner.roomThermalMass);
        this.humidity = owner.humidity;

        StoveStones.quench(this.stones, Config.STONE_POUR_COOLING.get(), this.temperature);
        this.fireTemperature = Math.max(this.temperature,
                this.fireTemperature - Config.POUR_FIREBOX_COOLING.get());

        if (lightSteam && this.level != null
                && StoveStones.wearOne(this.stones, this.level.random, Config.STONE_POURS_PER_CRACK.get())) {
            this.crackedThisPour = true;
        }
        setChanged();
        return lightSteam;
    }

    public boolean consumeCrackedFlag() {
        boolean cracked = this.crackedThisPour;
        this.crackedThisPour = false;
        return cracked;
    }

    private void exposeOccupants(Level level) {
        if (this.room == null) {
            return;
        }

        double quality = steamQuality();
        double heatIndex = RoomClimate.heatIndex(this.temperature, this.humidity) * quality;
        for (Player player : level.getEntitiesOfClass(Player.class, this.room.bounds())) {
            if (isInside(player)) {
                Exposure current = player.getData(ModAttachments.EXPOSURE);
                player.setData(ModAttachments.EXPOSURE,
                        current.merge(heatIndex, relativeHeightOf(player)));
                applySmokeTo(player);

                if (quality > 1.0 && player instanceof ServerPlayer served
                        && WarmthZone.of(PlayerWarmth.get(served)) != WarmthZone.NEUTRAL) {
                    ModTriggers.SMOKE_SAUNA.get().trigger(served);
                }
            }
        }
    }

    private void applySmokeTo(Player player) {
        if (this.smoke < Config.SMOKE_STING_LEVEL.get()) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, SMOKE_EFFECT_TICKS, 0, true, false));
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(player instanceof ServerPlayer served ? served : null,
                    ParticleTypes.SMOKE, false,
                    player.getX(), player.getEyeY(), player.getZ(),
                    2, 0.8, 0.5, 0.8, 0.005);
        }

        double choke = Config.SMOKE_CHOKE_LEVEL.get();
        if (this.smoke < choke) {
            return;
        }

        int amplifier = (int) Math.min(2, (this.smoke - choke) / 15.0);
        player.addEffect(new MobEffectInstance(
                ModEffects.SMOKE_POISONING.getDelegate(), SMOKE_EFFECT_TICKS, amplifier, true, true));
    }

    private boolean isInside(Player player) {
        if (this.room.interior().contains(player.blockPosition())) {
            return true;
        }
        return this.room.interior().contains(BlockPos.containing(player.getEyePosition()));
    }

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

    @Nullable
    public RoomShape getRoomOrLastSealed() {
        return this.room != null ? this.room : this.lastSealedRoom;
    }

    @Nullable
    public RoomShape getRoom() {
        return this.room;
    }

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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.heat_and_steam.stove." + this.tier.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new StoveMenu(containerId, playerInventory, this.fuel, this.stones, this.data,
                ContainerLevelAccess.create(this.level, this.worldPosition), this.tier);
    }

    public StoveTier getTier() {
        return this.tier;
    }

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
        tag.putDouble("FireTemperature", this.fireTemperature);
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
        this.fireTemperature = tag.contains("FireTemperature")
                ? tag.getDouble("FireTemperature")
                : Config.AMBIENT_TEMPERATURE.get();
        this.humidity = tag.getDouble("Humidity");
        this.smoke = tag.getDouble("Smoke");
        this.needsRescan = true;
    }
}
