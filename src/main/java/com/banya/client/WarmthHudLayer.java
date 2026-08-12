package com.banya.client;

import com.banya.player.PlayerWarmth;
import com.banya.player.WarmthHudData;
import com.banya.player.WarmthZone;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

/**
 * Warmth bar, drawn above the hotbar.
 *
 * <p>Shown whenever there is warmth to report, not only inside the parnaya: warmth carries on
 * draining after you step outside, and hiding the bar there hid live state the player still cares
 * about. It disappears once warmth reaches zero.
 */
public class WarmthHudLayer implements LayeredDraw.Layer {
    /** Same span as a vanilla status row, so it lines up with the hotbar's right edge. */
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 5;
    /** Left inset from screen centre, mirroring where vanilla starts the food row. */
    private static final int CENTRE_INSET = 10;
    /**
     * One row above the air bubbles. Keeps clear of health, armour, air and — importantly — the
     * action bar in the middle, where the overheat and faint messages appear.
     */
    private static final int BOTTOM_OFFSET = 59;

    private static final int COLOR_BORDER = 0xFF000000;
    private static final int COLOR_EMPTY = 0x80303030;
    private static final int COLOR_NEUTRAL = 0xFFB0B0B0;
    private static final int COLOR_LIGHT_STEAM = 0xFF6FCF6F;
    private static final int COLOR_DEEP_WARMTH = 0xFFF2A33C;
    private static final int COLOR_OVERHEAT = 0xFFE04B3A;
    private static final int COLOR_STRAIN = 0xFFC1272D;
    /** Muted while it wears off, so "cooling down" never looks like "still burning". */
    private static final int COLOR_STRAIN_FADING = 0x997A4A4A;
    /** Strain rides as a thinner line beneath the warmth bar. */
    private static final int STRAIN_HEIGHT = 2;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        float warmth = WarmthHudData.warmth();
        if (!WarmthHudData.inBanya() && warmth <= 0.0F) {
            return;
        }
        // Sits in the vanilla status stack rather than floating in the middle of the screen, and
        // carries no number: health and food do not print one either, and the thermometer is there
        // when an exact reading is wanted.
        int barLeft = graphics.guiWidth() / 2 + CENTRE_INSET;
        int top = graphics.guiHeight() - BOTTOM_OFFSET;

        graphics.fill(barLeft - 1, top - 1, barLeft + BAR_WIDTH + 1, top + BAR_HEIGHT + 1, COLOR_BORDER);
        graphics.fill(barLeft, top, barLeft + BAR_WIDTH, top + BAR_HEIGHT, COLOR_EMPTY);

        int filled = Math.round(BAR_WIDTH * (warmth / (float) PlayerWarmth.MAX_WARMTH));
        if (filled > 0) {
            graphics.fill(barLeft, top, barLeft + filled, top + BAR_HEIGHT, colorFor(WarmthZone.of(warmth)));
        }

        renderStrain(graphics, barLeft, top);
    }

    /**
     * A thin red line under the bar showing how much heat strain has built up. Without it the
     * mechanic that decides whether the bather is in danger would be entirely invisible, and the
     * damage would seem to come from nowhere.
     */
    private static void renderStrain(GuiGraphics graphics, int barLeft, int barTop) {
        float strain = WarmthHudData.strain();
        if (strain <= 0.0F) {
            return;
        }
        int top = barTop + BAR_HEIGHT + 1;
        int width = Math.max(1, Math.round(BAR_WIDTH * Math.min(1.0F, strain)));
        graphics.fill(barLeft - 1, top - 1, barLeft + BAR_WIDTH + 1, top + STRAIN_HEIGHT + 1, COLOR_BORDER);
        graphics.fill(barLeft, top, barLeft + BAR_WIDTH, top + STRAIN_HEIGHT, COLOR_EMPTY);
        // Building strain is alarming; strain wearing off is just something to wait out, and the
        // two need to be told apart at a glance or a draining bar reads as a stuck one.
        graphics.fill(barLeft, top, barLeft + width, top + STRAIN_HEIGHT,
                WarmthHudData.strainRising() ? COLOR_STRAIN : COLOR_STRAIN_FADING);
    }

    private static int colorFor(WarmthZone zone) {
        return switch (zone) {
            case NEUTRAL -> COLOR_NEUTRAL;
            case LIGHT_STEAM -> COLOR_LIGHT_STEAM;
            case DEEP_WARMTH -> COLOR_DEEP_WARMTH;
            case OVERHEAT -> COLOR_OVERHEAT;
        };
    }
}
