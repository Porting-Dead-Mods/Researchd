package com.portingdeadmods.researchd.client.screens.team.widgets;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

public class DraggableWidgetImageButton extends ImageButton {
    public DraggableWidgetImageButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
    }
}
