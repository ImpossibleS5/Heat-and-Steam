package com.impossibles5.heatandsteam.client;

import com.impossibles5.heatandsteam.player.PlayerWarmth;
import com.impossibles5.heatandsteam.player.WarmthHudData;
import com.impossibles5.heatandsteam.player.WarmthZone;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class WarmthHudLayer implements LayeredDraw.Layer {
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 5;

    private static final int CENTRE_INSET = 10;

    private static final int BOTTOM_OFFSET = 59;

    private static final int COLOR_BORDER = 0xFF000000;
    private static final int COLOR_EMPTY = 0x80303030;
    private static final int COLOR_NEUTRAL = 0xFFB0B0B0;
    private static final int COLOR_LIGHT_STEAM = 0xFF6FCF6F;
    private static final int COLOR_DEEP_WARMTH = 0xFFF2A33C;
    private static final int COLOR_OVERHEAT = 0xFFE04B3A;
    private static final int COLOR_STRAIN = 0xFFC1272D;

    private static final int COLOR_STRAIN_FADING = 0x997A4A4A;

    private static final int STRAIN_HEIGHT = 2;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        float warmth = WarmthHudData.warmth();
        if (!WarmthHudData.inSauna() && warmth <= 0.0F) {
            return;
        }

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

    private static void renderStrain(GuiGraphics graphics, int barLeft, int barTop) {
        float strain = WarmthHudData.strain();
        if (strain <= 0.0F) {
            return;
        }
        int top = barTop + BAR_HEIGHT + 1;
        int width = Math.max(1, Math.round(BAR_WIDTH * Math.min(1.0F, strain)));
        graphics.fill(barLeft - 1, top - 1, barLeft + BAR_WIDTH + 1, top + STRAIN_HEIGHT + 1, COLOR_BORDER);
        graphics.fill(barLeft, top, barLeft + BAR_WIDTH, top + STRAIN_HEIGHT, COLOR_EMPTY);

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
