package com.impossibles5.heatandsteam.item;

import com.impossibles5.heatandsteam.Config;
import com.impossibles5.heatandsteam.registry.ModSounds;
import com.impossibles5.heatandsteam.registry.ModTags;
import com.impossibles5.heatandsteam.stove.DisplayClock;
import com.impossibles5.heatandsteam.stove.StoneHeat;
import com.impossibles5.heatandsteam.stove.StoveStones;
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

public class SaunaStoneItem extends Item {
    private static final int BAR_SEGMENTS = 13;

    private static final int HEAT_BAR_COLOR = 0xFF8A3B;

    private static final int SETTLE_INTERVAL_TICKS = 20;

    public SaunaStoneItem(Properties properties) {
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

    private static void scald(Player player, ItemStack stack) {
        ItemStack dropped = stack.copy();

        stack.setCount(0);

        player.igniteForSeconds(Config.STONE_BURN_SECONDS.get().floatValue());
        player.drop(dropped, false);
        player.level().playSound(null, player.blockPosition(), ModSounds.STONE_SCALD.get(),
                SoundSource.PLAYERS, 0.6F, 1.8F);
        player.displayClientMessage(
                Component.translatable("message.heat_and_steam.stone.too_hot").withStyle(ChatFormatting.RED), true);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (!level.isClientSide() && level.getGameTime() % SETTLE_INTERVAL_TICKS == 0) {
            double scald = Config.STONE_SCALD_TEMPERATURE.get();
            float before = StoveStones.temperature(stack);
            float after = StoveStones.settle(stack, level.getGameTime(),
                    coolingMultiplier(level, entity.blockPosition()));
            if (before >= scald && after < scald) {
                level.playSound(null, entity.blockPosition(), ModSounds.STONE_QUENCH.get(),
                        SoundSource.BLOCKS, 0.4F, 2.0F);
            }
        }
        return false;
    }

    private static double coolingMultiplier(Level level, BlockPos pos) {
        return level.getFluidState(pos).is(ModTags.Fluids.COOLS_STONES)
                ? Config.STONE_WATER_COOLING_MULTIPLIER.get()
                : 1.0;
    }

    private static float shownTemperature(ItemStack stack) {
        return StoveStones.temperatureAt(stack, DisplayClock.gameTime());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        float shown = shownTemperature(stack);
        StoneHeat heat = StoneHeat.of(shown);
        if (heat != null) {
            tooltip.add(heat.describe(shown));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return shownTemperature(stack) >= StoneHeat.COLD_TEMPERATURE;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float filled = Math.min(1.0F, shownTemperature(stack) / StoneHeat.MAX_VISIBLE_TEMPERATURE);
        return Math.clamp(Math.round(BAR_SEGMENTS * filled), 1, BAR_SEGMENTS);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        StoneHeat heat = StoneHeat.of(shownTemperature(stack));
        if (heat == null || heat.color().getColor() == null) {
            return HEAT_BAR_COLOR;
        }
        return heat.color().getColor();
    }
}
