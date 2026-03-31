package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.MalformedInputException;
import java.util.List;

public final class GuiUtils {
    public static boolean spriteExists(TextureAtlas atlas, ResourceLocation spriteId) {
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
