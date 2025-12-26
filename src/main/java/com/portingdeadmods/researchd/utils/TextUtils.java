package com.portingdeadmods.researchd.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextUtils {
    /**
     * Draws text wrapped to fit within a maximum width.
     *
     * @param guiGraphics The GuiGraphics context
     * @param component   The text component to draw
     * @param x           X position
     * @param y           Y position
     * @param maxWidth    Maximum width before wrapping
     * @param color       Text color
     * @param dropShadow  Whether to draw with drop shadow
     * @return The total height of the rendered text
     */
    public static int drawWrappedText(GuiGraphics guiGraphics, Component component, int x, int y, int maxWidth, int color, boolean dropShadow) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(component, maxWidth);

        int lineHeight = font.lineHeight;
        int currentY = y;

        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(font, line, x, currentY, color, dropShadow);
            currentY += lineHeight;
        }

        return lines.size() * lineHeight;
    }

    /**
     * Draws text wrapped to fit within a maximum width with default line height.
     *
     * @param guiGraphics The GuiGraphics context
     * @param component   The text component to draw
     * @param x           X position
     * @param y           Y position
     * @param maxWidth    Maximum width before wrapping
     * @param lineSpacing Additional spacing between lines
     * @param color       Text color
     * @param dropShadow  Whether to draw with drop shadow
     * @return The total height of the rendered text
     */
    public static int drawWrappedText(GuiGraphics guiGraphics, Component component, int x, int y, int maxWidth, int lineSpacing, int color, boolean dropShadow) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(component, maxWidth);

        int lineHeight = font.lineHeight + lineSpacing;
        int currentY = y;

        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(font, line, x, currentY, color, dropShadow);
            currentY += lineHeight;
        }

        return lines.size() * lineHeight;
    }

    /**
     * Calculates the height that wrapped text would occupy without rendering it.
     *
     * @param component The text component
     * @param maxWidth  Maximum width before wrapping
     * @return The height the text would occupy
     */
    public static int getWrappedTextHeight(Component component, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(component, maxWidth);
        return lines.size() * font.lineHeight;
    }

    /**
     * Calculates the height that wrapped text would occupy without rendering it.
     *
     * @param component   The text component
     * @param maxWidth    Maximum width before wrapping
     * @param lineSpacing Additional spacing between lines
     * @return The height the text would occupy
     */
    public static int getWrappedTextHeight(Component component, int maxWidth, int lineSpacing) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(component, maxWidth);
        return lines.size() * (font.lineHeight + lineSpacing);
    }
}
