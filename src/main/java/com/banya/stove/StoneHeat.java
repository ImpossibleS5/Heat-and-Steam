package com.banya.stove;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * The bands a stone's temperature is read in, after TerraFirmaCraft's own heat scale.
 *
 * <p>A number in °C is precise and says nothing at a glance; a name and a colour say whether the
 * stone can be picked up, whether it will make steam, and whether it is worth waiting for. The
 * colours follow TFC's discipline: everything below a glow is grey, because a hot rock looks exactly
 * like a cold one, and only the bands that genuinely glow get a colour.
 */
public enum StoneHeat {
    /** Warm to the touch and nothing more. */
    WARM(40.0F, ChatFormatting.GRAY),
    /** Past the point where it can be held — see {@code stoneScaldTemperature}. */
    HOT(100.0F, ChatFormatting.GRAY),
    /** Hot enough to flash a ladle of water into proper light steam. */
    VERY_HOT(150.0F, ChatFormatting.GRAY),
    /** The first visible glow, in a dark parnaya. */
    FAINT_RED(450.0F, ChatFormatting.DARK_RED),
    /** Properly glowing: a stove that has been fired hard on good wood. */
    RED(700.0F, ChatFormatting.RED);

    /**
     * Where the item gauge reads full. Temperatures can and do run past it — the bar simply stops
     * growing, which is what TFC does with its own scale.
     */
    public static final float MAX_VISIBLE_TEMPERATURE = 900.0F;
    /** Below this a stone reads as cold: no bar, no tooltip, no band. */
    public static final float COLD_TEMPERATURE = 40.0F;

    private static final StoneHeat[] VALUES = values();

    private final float min;
    private final ChatFormatting color;

    StoneHeat(float min, ChatFormatting color) {
        this.min = min;
        this.color = color;
    }

    /** The band a temperature falls in, or null if the stone is cold enough not to mention. */
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

    /** "Раскалённый (720 °C)" — the band and the figure behind it, in the band's colour. */
    public Component describe(float temperature) {
        return Component.translatable("tooltip.banya.stone_heat." + name().toLowerCase(java.util.Locale.ROOT),
                        Math.round(temperature))
                .withStyle(this.color);
    }
}
