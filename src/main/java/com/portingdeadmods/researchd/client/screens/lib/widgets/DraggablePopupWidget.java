package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class DraggablePopupWidget extends PopupWidget {
    private GuiEventListener focused;
    private boolean isHovered;
    private boolean dragging;
    private boolean updateIsHovered = true;

    public DraggablePopupWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public Iterable<? extends LayoutElement> getElements() {
        return this.widgets;
    }

    @Override
    public void setFocused(GuiEventListener focused) {
        this.focused = focused;
        this.focused.setFocused(true);
    }

    @Override
    public GuiEventListener getFocused() {
        return focused;
    }

    protected void onMoved() {
    }

    @Override
    public void setX(int x) {
        super.setX(x);

        if (this.getLayout() != null) {
            this.getLayout().setX(x);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        if (this.getLayout() != null) {
            this.getLayout().setY(y);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.updateIsHovered) {
            this.isHovered = this.isRectHovered(guiGraphics, mouseX, mouseY, this.getWidth(), 12);
        }

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);

        this.updateIsHovered = false;

        if (this.isHovered) {
            this.setPosition(getX() + (int) dragX, getY() + (int) dragY);
            this.onMoved();
        }

    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);

        this.updateIsHovered = true;
        this.isHovered = false;
    }

    public boolean isLazyHovered() {
        return this.isHovered;
    }

    protected boolean isRectHovered(GuiGraphics guiGraphics, int mouseX, int mouseY, int width, int height) {
        return guiGraphics.containsPointInScissor(mouseX, mouseY)
                && mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getX() + width
                && mouseY < this.getY() + height;
    }

}
