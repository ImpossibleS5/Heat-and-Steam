package com.banya.client;

import com.banya.Banya;
import com.banya.stove.StoveMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Fuel screen for the T1 stove: one fuel slot, a burn gauge, and the current room temperature.
 * The gauge is drawn with solid fills rather than sprites to keep the texture a plain panel.
 */
public class StoveScreen extends AbstractContainerScreen<StoveMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Banya.MODID, "textures/gui/stove.png");

    /** Burn gauge sits to the right of the fuel slot. */
    private static final int GAUGE_X = 104;
    private static final int GAUGE_Y = 48;
    private static final int GAUGE_WIDTH = 16;
    private static final int GAUGE_HEIGHT = 10;
    /** Climate readout goes between the stone basket and the fuel slot. */
    private static final int CLIMATE_LABEL_Y = 38;
    private static final int GAUGE_BORDER = 0xFF373737;
    private static final int GAUGE_EMPTY = 0xFF8B8B8B;
    private static final int GAUGE_FLAME = 0xFFFF9A2E;

    public StoveScreen(StoveMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // Title sits above the fuel slot; the inventory label keeps its vanilla position.
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
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
        renderBurnGauge(graphics);
    }

    private void renderBurnGauge(GuiGraphics graphics) {
        int x = this.leftPos + GAUGE_X;
        int y = this.topPos + GAUGE_Y;
        graphics.fill(x - 1, y - 1, x + GAUGE_WIDTH + 1, y + GAUGE_HEIGHT + 1, GAUGE_BORDER);
        graphics.fill(x, y, x + GAUGE_WIDTH, y + GAUGE_HEIGHT, GAUGE_EMPTY);

        int total = this.menu.getBurnTimeTotal();
        if (total <= 0) {
            return;
        }
        int filled = Math.round(GAUGE_WIDTH * (this.menu.getBurnTime() / (float) total));
        if (filled > 0) {
            graphics.fill(x, y, x + filled, y + GAUGE_HEIGHT, GAUGE_FLAME);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        Component reading = Component.translatable("container.banya.stove.climate",
                this.menu.getTemperature(), this.menu.getHumidity());
        graphics.drawString(this.font, reading,
                (this.imageWidth - this.font.width(reading)) / 2, CLIMATE_LABEL_Y, 0x404040, false);
    }
}
