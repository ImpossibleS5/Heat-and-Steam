package com.banya.client;

import com.banya.player.PlayerWarmth;
import com.banya.player.WarmthHudData;
import com.banya.player.WarmthZone;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

/**
 * Warmth bar, drawn above the hotbar.
 *
 * <p>Shown whenever there is warmth to report, not only inside the parnaya: warmth carries on
 * draining after you step outside, and hiding the bar there hid live state the player still cares
 * about. It disappears once warmth reaches zero.
 */
public class WarmthHudLayer implements LayeredDraw.Layer {
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 5;
    /** Distance from the bottom of the screen, clearing the hotbar and its status bars. */
    private static final int BOTTOM_OFFSET = 58;

    private static final int COLOR_BORDER = 0xFF000000;
    private static final int COLOR_EMPTY = 0x80303030;
    private static final int COLOR_NEUTRAL = 0xFFB0B0B0;
    private static final int COLOR_LIGHT_STEAM = 0xFF6FCF6F;
    private static final int COLOR_DEEP_WARMTH = 0xFFF2A33C;
    private static final int COLOR_OVERHEAT = 0xFFE04B3A;
    private static final int COLOR_LABEL = 0xFFFFFFFF;

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
        int left = (graphics.guiWidth() - BAR_WIDTH) / 2;
        int top = graphics.guiHeight() - BOTTOM_OFFSET;

        graphics.fill(left - 1, top - 1, left + BAR_WIDTH + 1, top + BAR_HEIGHT + 1, COLOR_BORDER);
        graphics.fill(left, top, left + BAR_WIDTH, top + BAR_HEIGHT, COLOR_EMPTY);

        int filled = Math.round(BAR_WIDTH * (warmth / (float) PlayerWarmth.MAX_WARMTH));
        if (filled > 0) {
            graphics.fill(left, top, left + filled, top + BAR_HEIGHT, colorFor(WarmthZone.of(warmth)));
        }

        Component label = Component.translatable("hud.banya.warmth", Math.round(warmth));
        graphics.drawString(minecraft.font, label,
                (graphics.guiWidth() - minecraft.font.width(label)) / 2, top - 10, COLOR_LABEL, true);
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
