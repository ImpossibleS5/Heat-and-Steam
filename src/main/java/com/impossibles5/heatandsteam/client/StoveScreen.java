package com.impossibles5.heatandsteam.client;

import com.impossibles5.heatandsteam.HeatAndSteam;
import com.impossibles5.heatandsteam.stove.StoneHeat;
import com.impossibles5.heatandsteam.stove.StoveBlockEntity;
import com.impossibles5.heatandsteam.stove.StoveMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StoveScreen extends AbstractContainerScreen<StoveMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(HeatAndSteam.MODID, "textures/gui/stove.png");

    private static final int GAUGE_X = 68;
    private static final int GAUGE_Y = 57;
    private static final int GAUGE_WIDTH = 16;
    private static final int GAUGE_HEIGHT = 10;

    private static final int SLOT_SHADOW = 0xFF373737;
    private static final int SLOT_HIGHLIGHT = 0xFFFFFFFF;
    private static final int SLOT_FILL = 0xFF8B8B8B;
    private static final int GAUGE_BORDER = 0xFF373737;
    private static final int GAUGE_EMPTY = 0xFF8B8B8B;
    private static final int GAUGE_FLAME = 0xFFFF9A2E;

    private static final int TEXT_X = 92;
    private static final int TEXT_Y = 54;
    private static final int LABEL_COLOR = 0x404040;

    public StoveScreen(StoveMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderStoneSlots(graphics);
        renderBurnGauge(graphics);
    }

    private void renderStoneSlots(GuiGraphics graphics) {
        for (int slot = 0; slot < this.menu.getStoneSlotCount(); slot++) {
            int x = this.leftPos + StoveMenu.stoneSlotX(slot, this.menu.getStoneSlotCount()) - 1;
            int y = this.topPos + StoveMenu.stoneSlotY(slot) - 1;

            graphics.fill(x, y, x + 18, y + 18, SLOT_SHADOW);
            graphics.fill(x + 1, y + 1, x + 18, y + 18, SLOT_HIGHLIGHT);
            graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_FILL);
        }
    }

    private void renderTemperatures(GuiGraphics graphics) {
        Component fire = Component.translatable("gui.heat_and_steam.stove.fire", this.menu.getFireTemperature());
        graphics.drawString(this.font, fire, TEXT_X, TEXT_Y, LABEL_COLOR, false);

        int stone = this.menu.getStoneTemperature();
        StoneHeat heat = StoneHeat.of(stone);
        Component stones = Component.translatable("gui.heat_and_steam.stove.stones", stone)
                .withStyle(heat == null ? ChatFormatting.DARK_GRAY : heat.color());
        graphics.drawString(this.font, stones, TEXT_X, TEXT_Y + 10, LABEL_COLOR, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        renderTemperatures(graphics);
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
}
