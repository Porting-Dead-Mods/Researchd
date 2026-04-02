package com.portingdeadmods.researchd.utils;

import it.unimi.dsi.fastutil.chars.CharPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.Predicate;

public final class TextUtils {
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

    public static String camelToSnake(String str, Predicate<Character> allowed) {
        StringBuilder result = new StringBuilder();

        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        char lastChar = 0;

        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            if (allowed.test(ch) || Character.isSpaceChar(ch)) {
                if (Character.isUpperCase(ch)) {
                    if (lastChar != '_') {
                        result.append('_');
                    }
                    result.append(Character.toLowerCase(ch));
                    lastChar = 0;
                } else if (Character.isSpaceChar(ch)) {
                    if (lastChar != '_') {
                        result.append('_');
                        lastChar = '_';
                    }
                } else {
                    result.append(ch);
                    lastChar = 0;
                }
            }
        }

        return result.toString();
    }

    public static boolean isValidResourceLocation(String s) {
        return ResourceLocation.tryBySeparator(s, ':') != null;
    }

    public static String trimSpecialCharacterAndConvertToSnake(String input) {
        return toSnakeCase(input, c -> Character.isLetterOrDigit(c) || c == '_');
    }

    public static String toSnakeCase(String input, CharPredicate validChar) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder newString = new StringBuilder(input.length());

        String trimmedInput = input.trim();

        int index = 0;
        while (index < trimmedInput.length()) {
            char c = trimmedInput.charAt(index);
            if (Character.isWhitespace(c) || !validChar.test(c)) {
                while ((Character.isWhitespace(c) || !validChar.test(c)) && index + 1 < trimmedInput.length()) {
                    c = trimmedInput.charAt(++index);
                }
                newString.append('_');
                newString.append(c);
            } else if (Character.isUpperCase(c)) {
                newString.append(Character.toLowerCase(c));
            } else if (validChar.test(c)) {
                newString.append(c);
            }
            index++;

        }

        return newString.toString();
    }

    // Ignore special chars like spaces n' : (things that break datapack names etc.)
    public static boolean isValidNamespace(String str) {
        if (str == null) return false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '.' || c == '-')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidInt(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidFloat(String str) {
        if (str == null) {
            return false;
        }
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidIntInRange(String str, int min, int max) {
        if (isValidInt(str)) {
            int i = Integer.parseInt(str);
            return i >= min && i <= max;
        }
        return false;
    }

}
