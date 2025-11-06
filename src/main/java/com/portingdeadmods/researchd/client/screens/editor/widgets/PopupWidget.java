package com.portingdeadmods.researchd.client.screens.editor.widgets;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class PopupWidget extends AbstractWidget {
    public PopupWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public void close() {
    }

}
