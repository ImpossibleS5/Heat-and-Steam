package com.impossibles5.heatandsteam.stove;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public enum StoneHeat {
    WARM(1.0F, ChatFormatting.GRAY),

    HOT(100.0F, ChatFormatting.GRAY),

    VERY_HOT(150.0F, ChatFormatting.GRAY),

    FAINT_RED(450.0F, ChatFormatting.DARK_RED),

    RED(700.0F, ChatFormatting.RED);

    public static final float MAX_VISIBLE_TEMPERATURE = 900.0F;

    public static final float COLD_TEMPERATURE = 1.0F;

    private static final StoneHeat[] VALUES = values();

    private final float min;
    private final ChatFormatting color;

    StoneHeat(float min, ChatFormatting color) {
        this.min = min;
        this.color = color;
    }

    @Nullable
    public static StoneHeat of(float temperature) {
        StoneHeat found = null;
        for (StoneHeat heat : VALUES) {
            if (temperature >= heat.min) {
                found = heat;
            }
        }
        return found;
    }

    public float min() {
        return this.min;
    }

    public ChatFormatting color() {
        return this.color;
    }

    public Component describe(float temperature) {
        return Component.translatable("tooltip.heat_and_steam.stone_heat." + name().toLowerCase(java.util.Locale.ROOT),
                        Math.round(temperature))
                .withStyle(this.color);
    }
}
