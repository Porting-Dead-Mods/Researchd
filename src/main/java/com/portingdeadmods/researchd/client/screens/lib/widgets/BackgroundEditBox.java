package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BackgroundEditBox extends EditBox implements EditBoxExtension {
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);
    private final WidgetSprites sprites;

    public BackgroundEditBox(Font font, WidgetSprites sprites, int width, int height, String message) {
        super(font, width, height, CommonComponents.EMPTY);
        this.setMaxLength(128);
        setValue(message);
        this.sprites = sprites;
    }

    public BackgroundEditBox(Font font, int width, int height, String message) {
        this(font, SPRITES, width, height, message);
    }

    public BackgroundEditBox(Font font, int width, int height) {
        this(font, SPRITES, width, height,"");
    }

    @Override
    public WidgetSprites getSprites(WidgetSprites original) {
        return this.sprites;
    }
}
