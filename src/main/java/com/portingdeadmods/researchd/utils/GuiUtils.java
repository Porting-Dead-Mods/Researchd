package com.portingdeadmods.researchd.utils;

import net.minecraft.client.gui.GuiGraphics;

public class GuiUtils {
    public static void renderRect(GuiGraphics guiGraphics, int x, int y, int width, int height, int lineWidth, int lineColor, int fillColor) {
        guiGraphics.fill(x, y, x + width, y + height, lineColor);
        guiGraphics.fill(x + lineWidth, y + lineWidth, x + width - lineWidth, y + height - lineWidth, fillColor);
    }
}
