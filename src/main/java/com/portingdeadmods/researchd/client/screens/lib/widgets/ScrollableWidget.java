package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.api.client.screens.widgets.AbstractScroller;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class ScrollableWidget<L extends Layout> extends AbstractLayoutWidget<L> {
    private boolean renderScroller;
    private int scrollOffset;
    private int initialY;
    private boolean horizontalScrollBar;
    private boolean verticalScrollBar;

    public ScrollableWidget(@Nullable L layout, int x, int y, int width, int height, Component message) {
        super(layout, x, y, width, height, message);
        this.renderScroller = true;
        if (layout != null) {
            layout.visitWidgets(this::addRenderableWidget);
        }
        this.initialY = this.layout.getY();
    }

    public ScrollableWidget(@Nullable L layout, int width, int height, Component message) {
        this(layout, 0, 0, width, height, message);
    }

    public void setHorizontalScrollBar(boolean horizontalScrollBar) {
        this.horizontalScrollBar = horizontalScrollBar;
    }

    public void setVerticalScrollBar(boolean verticalScrollBar) {
        this.verticalScrollBar = verticalScrollBar;
    }

    public void setRenderScroller(boolean renderScroller) {
        this.renderScroller = renderScroller;
    }

    public void resetScrollOffset() {
        this.scrollOffset = 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
        {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            {
                //poseStack.translate(0, -this.scrollOffset, 0);
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            }
            poseStack.popPose();
        }
        guiGraphics.disableScissor();
    }

    private int getContentHeight() {
        if (this.layout != null) {
            return this.layout.getHeight();
        }
        return 1;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean canScroll = this.getContentHeight() > this.getHeight();
        if (canScroll) {
            double rawScrollOffset = this.scrollOffset - scrollY * 7;
            int  maxScrollOffset = this.getContentHeight() + 1;
            if (rawScrollOffset > maxScrollOffset) {
                this.scrollOffset = this.getContentHeight();
            } else {
                this.scrollOffset = (int) rawScrollOffset;
                this.layout.setY((int) (this.layout.getY() + scrollY * 7));
            }

            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
                this.layout.setY(initialY);
            }
        }
        return true;
    }

    private void setElementYPositions() {
        for (LayoutElement element : this.getElements()) {
            element.setY(element.getY() - this.scrollOffset);
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        if (this.layout != null) {
            this.layout.setX(x);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        if (this.layout != null) {
            this.layout.setY(y);
            this.initialY = this.layout.getY();
        }
    }
}
