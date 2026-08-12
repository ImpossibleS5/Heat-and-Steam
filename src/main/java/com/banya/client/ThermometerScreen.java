package com.banya.client;

import com.banya.Banya;
import com.banya.stove.ThermometerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The thermometer's readout. Four values with a bar apiece, so the room's state can be taken in at
 * a glance instead of parsed out of a line of text.
 */
public class ThermometerScreen extends AbstractContainerScreen<ThermometerMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Banya.MODID, "textures/gui/thermometer.png");

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 108;

    private static final int ROW_START_Y = 24;
    private static final int ROW_HEIGHT = 18;
    private static final int LABEL_X = 12;
    private static final int BAR_X = 78;
    private static final int BAR_WIDTH = 86;
    private static final int BAR_HEIGHT = 6;

    private static final int LABEL_COLOR = 0x404040;
    private static final int BAR_BORDER = 0xFF373737;
    private static final int BAR_EMPTY = 0xFF8B8B8B;
    private static final int COLOR_TEMPERATURE = 0xFFE0662E;
    private static final int COLOR_HUMIDITY = 0xFF3F8FCF;
    private static final int COLOR_HEAT_INDEX = 0xFFE0A72E;
    private static final int COLOR_SMOKE = 0xFF6B6259;

    /** Bars are read against these full-scale values, chosen to match the design's ceilings. */
    private static final float TEMPERATURE_SCALE = 120.0F;
    private static final float HEAT_INDEX_SCALE = 240.0F;
    private static final float PERCENT_SCALE = 100.0F;

    public ThermometerScreen(ThermometerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = PANEL_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 8;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Not calling super: there is no player inventory here, so its "Inventory" label would be
        // a lie. The title is drawn by hand instead.
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, LABEL_COLOR, false);

        row(graphics, 0, "temperature",
                Component.translatable("gui.banya.thermometer.degrees", this.menu.getTemperature()),
                this.menu.getTemperature() / TEMPERATURE_SCALE, COLOR_TEMPERATURE);
        row(graphics, 1, "humidity",
                Component.translatable("gui.banya.thermometer.percent", this.menu.getHumidity()),
                this.menu.getHumidity() / PERCENT_SCALE, COLOR_HUMIDITY);
        row(graphics, 2, "heat_index",
                Component.translatable("gui.banya.thermometer.degrees", this.menu.getHeatIndex()),
                this.menu.getHeatIndex() / HEAT_INDEX_SCALE, COLOR_HEAT_INDEX);
        row(graphics, 3, "smoke",
                Component.translatable("gui.banya.thermometer.percent", this.menu.getSmoke()),
                this.menu.getSmoke() / PERCENT_SCALE, COLOR_SMOKE);

        // Whether the room actually holds its climate is the first thing worth knowing.
        Component seal = this.menu.isSealed()
                ? Component.translatable("gui.banya.thermometer.sealed").withStyle(ChatFormatting.DARK_GREEN)
                : Component.translatable("gui.banya.thermometer.leaking").withStyle(ChatFormatting.DARK_RED);
        graphics.drawString(this.font, seal,
                (this.imageWidth - this.font.width(seal)) / 2,
                ROW_START_Y + 4 * ROW_HEIGHT + 2, LABEL_COLOR, false);
    }

    private void row(GuiGraphics graphics, int index, String key, Component value, float fill, int color) {
        int y = ROW_START_Y + index * ROW_HEIGHT;
        graphics.drawString(this.font, Component.translatable("gui.banya.thermometer." + key),
                LABEL_X, y, LABEL_COLOR, false);

        int barY = y + 1;
        graphics.fill(BAR_X - 1, barY - 1, BAR_X + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, BAR_BORDER);
        graphics.fill(BAR_X, barY, BAR_X + BAR_WIDTH, barY + BAR_HEIGHT, BAR_EMPTY);
        int filled = Math.round(BAR_WIDTH * Math.clamp(fill, 0.0F, 1.0F));
        if (filled > 0) {
            graphics.fill(BAR_X, barY, BAR_X + filled, barY + BAR_HEIGHT, color);
        }

        graphics.drawString(this.font, value,
                BAR_X + BAR_WIDTH - this.font.width(value), y + BAR_HEIGHT + 2, LABEL_COLOR, false);
    }
}
