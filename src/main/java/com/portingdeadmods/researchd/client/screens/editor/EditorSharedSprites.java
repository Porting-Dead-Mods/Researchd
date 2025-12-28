package com.portingdeadmods.researchd.client.screens.editor;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class EditorSharedSprites {
    public static final ResourceLocation EDITOR_BACKGROUND_SPRITE = Researchd.rl("editor_background");
    public static final ResourceLocation EDITOR_BACKGROUND_HIGHLIGHTED_SPRITE = Researchd.rl("editor_background_highlighted");
    public static final WidgetSprites EDITOR_BACKGROUND_SPRITES = new WidgetSprites(EDITOR_BACKGROUND_SPRITE, EDITOR_BACKGROUND_HIGHLIGHTED_SPRITE);
    public static final ResourceLocation EDITOR_BACKGROUND_INVERTED_SPRITE = Researchd.rl("editor_background_inverted");
    public static final ResourceLocation EDITOR_WIDGET_BACKGROUND_SPRITE = Researchd.rl("widget/editor_widget_background");
}
