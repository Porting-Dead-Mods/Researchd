package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public abstract class ContainerWidget<E> extends AbstractWidget {
    public static final ResourceLocation SCROLLER_SMALL_SPRITE = Researchd.rl("scroller_small");
    private final int itemWidth;
    private final int itemHeight;
    private final Collection<E> items;
    private final boolean renderScroller;
    private int scrollOffset;

    public ContainerWidget(int width, int height, int itemWidth, int itemHeight, Collection<E> items, boolean renderScroller) {
        this(0, 0, width, height, itemWidth, itemHeight, items, renderScroller);
    }

    public ContainerWidget(int x, int y, int width, int height, int itemWidth, int itemHeight, Collection<E> items, boolean renderScroller) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.items = items;
        this.renderScroller = renderScroller;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.enableScissor(this.getLeft(), this.getTop(), this.getX() + this.getItemWidth(), this.getY() + this.getHeight());
        {
            this.renderContainer(guiGraphics, mouseX, mouseY);
        }
        guiGraphics.disableScissor();

        if (renderScroller) {
            float percentage = (float) this.scrollOffset / (this.getContentHeight() - this.getHeight());
            guiGraphics.blitSprite(SCROLLER_SMALL_SPRITE, this.getLeft() + this.getItemWidth() + 3, (int) (this.getTop() + percentage * (this.getHeight() - 7)), 4, 7);
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.getContentHeight() > this.getHeight()) {
            double rawScrollOffset = Math.max(this.scrollOffset - scrollY * 7, 0);
            if (rawScrollOffset > this.getContentHeight() - this.getHeight() + 1) {
                this.scrollOffset = this.getContentHeight() - this.getHeight();
            } else {
                this.scrollOffset = (int) rawScrollOffset;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean isScrollbarHovered(int mouseX, int mouseY) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered() && !this.isScrollbarHovered((int) mouseX, (int) mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        } else if (this.isHovered() && this.isScrollbarHovered((int) mouseX, (int) mouseY) && this.renderScroller) {
            int scrollableHeight = this.getContentHeight() - this.getHeight();
            int minY = getY() + 7;
            int maxY = getY() + this.getHeight() - 7;

            double scrolledPercentage = ((Math.clamp(mouseY, minY, maxY) - (minY))) / (double) (maxY - minY);

            this.scrollOffset = (int) (scrollableHeight * scrolledPercentage);
            return true;
        }
        return false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (this.isScrollbarHovered((int) mouseX, (int) mouseY)) {
            this.mouseClicked(mouseX, mouseY, 0);
        }
    }

    public Collection<E> getItems() {
        return items;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public int getContentHeight() {
        return this.getItems().size() * this.getItemHeight();
    }

    private int getTop() {
        return this.getY() + 1;
    }

    private int getLeft() {
        return this.getX() + 1;
    }

    public void renderContainer(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int index = 0;
        for (E item : this.items) {
            this.renderItem(guiGraphics, item, index, this.getLeft(), this.getTop() + index * this.getItemHeight() - this.scrollOffset, mouseX, mouseY);
            index++;
        }
    }

    public boolean isItemHovered(int index, int mouseX, int mouseY) {
        return mouseX > this.getLeft()
                && mouseX < this.getLeft() + this.getItemWidth()
                && mouseY > this.getTop() + (this.itemHeight * index) - this.scrollOffset
                && mouseY < this.getTop() + (this.itemHeight * (index + 1)) - this.scrollOffset;
    }

	protected void sortEntriesBy(java.util.Comparator<? super E> comparator) {
		if (this.items instanceof java.util.List<E> list) {
			list.sort(comparator);
		}
	}

    public abstract void clickedItem(E item, int index, int left, int top, int mouseX, int mouseY);

    public abstract void renderItem(GuiGraphics guiGraphics, E item, int index, int left, int top, int mouseX, int mouseY);

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
