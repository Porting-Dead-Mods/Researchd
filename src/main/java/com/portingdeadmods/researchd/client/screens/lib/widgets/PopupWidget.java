package com.portingdeadmods.researchd.client.screens.lib.widgets;

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

public abstract class PopupWidget extends AbstractContainerWidget implements LayoutWidget<Layout> {
    private final Map<LayoutElement, DropDownWidget<?>> dropDownWidgets;
    protected final List<AbstractWidget> widgets;
    protected boolean draggable;
    private boolean hovered;
    private boolean updateHovered = true;

    public PopupWidget(int x, int y, int width, int height, boolean draggable, Component message) {
        super(x, y, width, height, message);
        this.dropDownWidgets = new HashMap<>();
        this.widgets = new ArrayList<>();
        this.draggable = draggable;
    }

    protected <W extends AbstractWidget> W addRenderableWidget(W widget) {
        this.widgets.add(widget);
        return widget;
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.updateHovered) {
            this.hovered = this.isRectHovered(guiGraphics, mouseX, mouseY, this.getWidth(), 12);
        }

        LayoutWidget.super.renderElements(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, dragX, dragY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);

        if (this.isDraggable()) {
            this.updateHovered = false;

            if (this.hovered) {
                this.setPosition(getX() + (int) dragX, getY() + (int) dragY);
                this.onMoved();
            }
        }

    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);

        this.updateHovered = true;
        this.hovered = false;
    }

    protected void onPositionChanged(int x, int y) {
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        this.onPositionChanged(x, this.getY());
        if (this.getLayout() != null) {
            this.getLayout().setX(x);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        this.onPositionChanged(this.getX(), y);
        if (this.getLayout() != null) {
            this.getLayout().setY(y);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return widgets;
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    public final void close() {
        this.onClose();
    }

    public final void open() {
        this.onOpen();
    }

    protected void onOpen() {

    }

    protected void onClose() {

    }

    protected void onMoved() {
    }

    @Override
    public Iterable<? extends LayoutElement> getElements() {
        return this.widgets;
    }

    @Override
    public void arrangeElements() {
        LayoutWidget.super.arrangeElements();
    }

    public boolean isLazyHovered() {
        return this.hovered;
    }

    protected boolean isRectHovered(GuiGraphics guiGraphics, int mouseX, int mouseY, int width, int height) {
        return guiGraphics.containsPointInScissor(mouseX, mouseY)
                && mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getX() + width
                && mouseY < this.getY() + height;
    }
}
