package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

public class BackgroundEditBox extends EditBox implements EditBoxExtension {
    private final WidgetSprites sprites;

    public BackgroundEditBox(Font font, WidgetSprites sprites, int width, int height, Component message) {
        super(font, width, height, message);
        this.sprites = sprites;
    }

    @Override
    public WidgetSprites getSprites(WidgetSprites original) {
        return this.sprites;
    }
}
