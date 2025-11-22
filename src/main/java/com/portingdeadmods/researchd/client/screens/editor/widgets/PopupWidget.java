package com.portingdeadmods.researchd.client.screens.editor.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class PopupWidget extends AbstractWidget {
    private final Map<LayoutElement, DropDownWidget<?>> dropDownWidgets;
    private Consumer<PopupWidget> additionalOnCloseAction = w -> {};

    public PopupWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.dropDownWidgets = new HashMap<>();
    }

    public <W extends PopupWidget> void setOnCloseAction(Consumer<W> onClose) {
        this.additionalOnCloseAction = (Consumer<PopupWidget>) onClose;
    }

    public final void close() {
        this.onClose();
        this.additionalOnCloseAction.accept(this);
    }

    protected void onClose() {

    }

    public static @NotNull Font getFont() {
        return Minecraft.getInstance().font;
    }

    protected <P extends LayoutElement, D extends DropDownWidget<P>> D attachDropDown(P parent, D dropDownWidget) {
        this.dropDownWidgets.put(parent, dropDownWidget);
        dropDownWidget.rebuildOptions();
        return dropDownWidget;
    }

    public void dropDownFor(LayoutElement parent) {
        DropDownWidget<?> dropDownWidget = this.dropDownWidgets.get(parent);
        if (dropDownWidget != null) {
            if (!dropDownWidget.isVisible()) {
                dropDownWidget.open();
            } else {
                dropDownWidget.close();
            }
        }
    }

}
