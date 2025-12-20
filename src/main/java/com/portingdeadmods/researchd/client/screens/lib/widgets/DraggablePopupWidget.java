package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class DraggablePopupWidget extends PopupWidget implements LayoutWidget<Layout> {
    private boolean isHovered;
    private boolean updateIsHovered = true;
    private final List<AbstractWidget> widgets;

    public DraggablePopupWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.widgets = new ArrayList<>();
    }

    @Override
    public Iterable<? extends LayoutElement> getElements() {
        return this.widgets;
    }

    protected <W extends AbstractWidget> W addRenderableWidget(W widget) {
        this.widgets.add(widget);
        return widget;
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
    }

    protected void renderElements(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        LayoutWidget.super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        LayoutWidget.super.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (LayoutWidget.super.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (LayoutWidget.super.mouseReleased(mouseX, mouseY, button)) return true;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (LayoutWidget.super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (LayoutWidget.super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (LayoutWidget.super.keyPressed(keyCode, scanCode, modifiers)) return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (LayoutWidget.super.keyReleased(keyCode, scanCode, modifiers)) return true;

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (LayoutWidget.super.charTyped(codePoint, modifiers)) return true;

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
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
