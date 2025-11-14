package com.portingdeadmods.researchd.client.screens.editor.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class DraggablePopupWidget extends PopupWidget {
    private boolean isHovered;
    private boolean updateIsHovered = true;

    public DraggablePopupWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
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

    protected abstract @Nullable Layout getLayout();

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.updateIsHovered) {
            this.isHovered = this.isRectHovered(guiGraphics, mouseX, mouseY, this.getWidth(), 12);
        }
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
