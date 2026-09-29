package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class GuiUtils {
    /** Draws a whole texture, stretched to the given size. Replaces PDL's, which PDL dropped on 26.1. */
    public static void drawImg(
            GuiGraphicsExtractor guiGraphics, Identifier texture, int x, int y, int width, int height) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, width, height);
    }

    public static boolean spriteExists(TextureAtlas atlas, Identifier spriteId) {
        TextureAtlasSprite sprite = atlas.getSprite(spriteId);
        return sprite != atlas.getSprite(MissingTextureAtlasSprite.getLocation());
    }

    // Kinda hacky, but eh it works
    public static void renderTooltip(List<Component> tooltip) {
        ResearchScreen.setTooltip(tooltip);
    }

    public static Font getFont() {
        return Minecraft.getInstance().font;
    }

    public static StringWidget stringWidget(Component text) {
        return new StringWidget(text, getFont());
    }

    public static StringWidget stringWidget(String text) {
        return stringWidget(Component.literal(text));
    }
}
