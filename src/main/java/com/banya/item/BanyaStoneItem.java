package com.banya.item;

import com.banya.Config;
import com.banya.registry.ModSounds;
import com.banya.registry.ModTags;
import com.banya.stove.StoneHeat;
import com.banya.stove.StoveStones;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A banya stone. Its heat and its cracks live on the stack, so a stone carries its warmth out of
 * the basket and wears its damage where you can see it.
 *
 * <p>Heat storage itself is component-based and works on any tagged stack, so third-party stones
 * still bank heat in a stove. What this class adds is the parts that need an item: settling the
 * cooling clock, quenching in water, and refusing to be carried while it is glowing.
 */
public class BanyaStoneItem extends Item {
    /** Width of a full vanilla item bar, in pixels. */
    private static final int BAR_SEGMENTS = 13;
    /** Ember orange, so the heat gauge never reads as durability. */
    private static final int HEAT_BAR_COLOR = 0xFF8A3B;
    private static final int SETTLE_INTERVAL_TICKS = 20;

    public BanyaStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide() || level.getGameTime() % SETTLE_INTERVAL_TICKS != 0) {
            return;
        }
        StoveStones.settle(stack, level.getGameTime(), coolingMultiplier(level, entity.blockPosition()));

        if (entity instanceof Player player && StoveStones.isScalding(stack, level.getGameTime())) {
            scald(player, stack);
        }
    }

    /**
     * A stone straight out of the fire cannot be carried at all — not in the hand, not buried in the
     * pack. It sets light to whoever is holding it and lands on the floor, where it cools off or is
     * quenched.
     *
     * <p>Picking it straight back up is allowed, and simply burns you again a second later. That
     * loop is the mechanic, not a hole in it: the stone teaches its own rule, and it means the only
     * ways past a glowing stone are the two real ones — wait, or throw water on it. Refusing the
     * pickup instead was tidier and said far less.
     */
    private static void scald(Player player, ItemStack stack) {
        ItemStack dropped = stack.copy();
        // Emptying in place is safe here: the inventory tick tolerates a slot going empty under it.
        stack.setCount(0);

        player.igniteForSeconds(Config.STONE_BURN_SECONDS.get().floatValue());
        player.drop(dropped, false);
        player.level().playSound(null, player.blockPosition(), ModSounds.STONE_SCALD.get(),
                SoundSource.PLAYERS, 0.6F, 1.8F);
        player.displayClientMessage(
                Component.translatable("message.banya.stone.too_hot").withStyle(ChatFormatting.RED), true);
    }

    /**
     * Lying in the world a stone cools like anything else, and faster if it landed in water. The
     * fluid is judged by tag, so lava does not count as a quench — see
     * {@link ModTags.Fluids#COOLS_STONES}.
     */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (!level.isClientSide() && level.getGameTime() % SETTLE_INTERVAL_TICKS == 0) {
            double scald = Config.STONE_SCALD_TEMPERATURE.get();
            float before = StoveStones.temperature(stack);
            float after = StoveStones.settle(stack, level.getGameTime(),
                    coolingMultiplier(level, entity.blockPosition()));
            if (before >= scald && after < scald) {
                // The hiss that says it is safe to pick up again — at the moment that becomes true,
                // not when the stone finally reaches the temperature of the room, which for a
                // quenched one is several silent minutes later.
                level.playSound(null, entity.blockPosition(), ModSounds.STONE_QUENCH.get(),
                        SoundSource.BLOCKS, 0.4F, 2.0F);
            }
        }
        return false; // not handled exclusively; vanilla still ticks the entity
    }

    /** How much faster than usual heat is leaving, given what the stone is sitting in. */
    private static double coolingMultiplier(Level level, BlockPos pos) {
        return level.getFluidState(pos).is(ModTags.Fluids.COOLS_STONES)
                ? Config.STONE_WATER_COOLING_MULTIPLIER.get()
                : 1.0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        StoneHeat heat = StoneHeat.of(StoveStones.temperature(stack));
        if (heat != null) {
            tooltip.add(heat.describe(StoveStones.temperature(stack)));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return StoveStones.temperature(stack) >= StoneHeat.COLD_TEMPERATURE;
    }

    /**
     * Against a fixed visible scale rather than anything the stone owns, the way TFC draws its own
     * heat: the gauge answers "how hot is this", and a scale that varied per stone made the same
     * bar mean different things in two different hands.
     */
    @Override
    public int getBarWidth(ItemStack stack) {
        float filled = Math.min(1.0F,
                StoveStones.temperature(stack) / StoneHeat.MAX_VISIBLE_TEMPERATURE);
        return Math.clamp(Math.round(BAR_SEGMENTS * filled), 1, BAR_SEGMENTS);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        StoneHeat heat = StoneHeat.of(StoveStones.temperature(stack));
        if (heat == null || heat.color().getColor() == null) {
            return HEAT_BAR_COLOR;
        }
        return heat.color().getColor();
    }
}
