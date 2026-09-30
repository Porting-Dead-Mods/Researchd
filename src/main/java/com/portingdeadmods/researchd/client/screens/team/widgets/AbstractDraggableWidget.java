package com.portingdeadmods.researchd.client.screens.team.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public abstract class AbstractDraggableWidget extends AbstractWidget {
    private boolean isHovered;
    private boolean updateIsHovered = true;

    public AbstractDraggableWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int i1, float v) {
        if (this.updateIsHovered) {
            this.isHovered = this.isRectHovered(guiGraphics, i, i1, this.getWidth(), 12);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        super.onDrag(event, dragX, dragY);

        this.updateIsHovered = false;

        if (this.isHovered) {
            this.setPosition(getX() + (int) dragX, getY() + (int) dragY);
            this.onMoved();
        }
    }

    protected void onMoved() {}

    @Override
    public void onRelease(MouseButtonEvent event) {
        super.onRelease(event);

        this.updateIsHovered = true;
        this.isHovered = false;
    }

    public boolean isLazyHovered() {
        return this.isHovered;
    }

    protected boolean isRectHovered(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int width, int height) {
        return guiGraphics.containsPointInScissor(mouseX, mouseY)
                && mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getX() + width
                && mouseY < this.getY() + height;
    }
}
