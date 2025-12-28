package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class PopupWidget extends AbstractContainerWidget implements LayoutWidget<Layout> {
    private final Map<LayoutElement, DropDownWidget<?>> dropDownWidgets;
    private Consumer<PopupWidget> additionalOnCloseAction = w -> {};
    protected final List<AbstractWidget> widgets;

    public PopupWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.dropDownWidgets = new HashMap<>();
        this.widgets = new ArrayList<>();
    }

    protected <W extends AbstractWidget> W addRenderableWidget(W widget) {
        this.widgets.add(widget);
        return widget;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        LayoutWidget.super.renderElements(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void onPositionChanged(int x, int y) {

    }

    @Override
    public void setX(int x) {
        super.setX(x);

        this.onPositionChanged(x, this.getY());
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        this.onPositionChanged(this.getX(), y);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public <W extends PopupWidget> void setOnCloseAction(Consumer<W> onClose) {
        this.additionalOnCloseAction = (Consumer<PopupWidget>) onClose;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
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

    @Override
    public Iterable<? extends LayoutElement> getElements() {
        return this.widgets;
    }

    @Override
    public void arrangeElements() {
        LayoutWidget.super.arrangeElements();
    }
}
