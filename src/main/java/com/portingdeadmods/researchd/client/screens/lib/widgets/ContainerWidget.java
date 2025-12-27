package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

// TODO: Move to pdl
public abstract class ContainerWidget<E> extends AbstractWidget {
    public static final ResourceLocation SCROLLER_SMALL_SPRITE = Researchd.rl("scroller_small");
    public static final ResourceLocation SCROLLER_SMALL_HORIZONTAL_SPRITE = Researchd.rl("scroller_small_horizontal");
    private final int itemWidth;
    private final int itemHeight;
    private final Orientation orientation;
    private final int cols;
    private final int rows;
    private Collection<E> items;
    private final boolean renderScroller;
    protected int scrollOffset;
    protected @Nullable E hoveredItem;
    protected int hoveredXIndex;
    protected int hoveredYIndex;

    public ContainerWidget(int width, int height, int itemWidth, int itemHeight, Orientation orientation, int cols, int rows, Collection<E> items, boolean renderScroller) {
        this(0, 0, width, height, itemWidth, itemHeight, orientation, cols, rows, items, renderScroller);
    }

    public ContainerWidget(int x, int y, int width, int height, int itemWidth, int itemHeight, Orientation orientation, int cols, int rows, Collection<E> items, boolean renderScroller) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.orientation = orientation;
        this.cols = cols;
        this.rows = rows;
        this.items = items;
        this.renderScroller = renderScroller;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        this.hoveredItem = null;
        this.hoveredXIndex = -1;
        this.hoveredYIndex = -1;

        guiGraphics.enableScissor(this.getLeft(), this.getTop(), this.getLeft() + getScissorsWidth(), this.getTop() + getScissorsHeight());
        {
            this.renderContainer(guiGraphics, mouseX, mouseY);
        }
        guiGraphics.disableScissor();

        if (renderScroller) {
            renderScroller(guiGraphics, mouseX, mouseY, v);
        }

        renderTooltips(guiGraphics, mouseX, mouseY, v);

    }

    protected void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
    }

    protected void renderScroller(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float percentage = (float) this.scrollOffset / getMaxScrollDistance();
        if (Float.isNaN(percentage)) {
            percentage = 0;
        }
        int x = this.getScrollerX(percentage);
        int y = this.getScrollerY(percentage);
        guiGraphics.blitSprite(orientation.scrollerSprite, x, y, orientation.spriteWidth, orientation.spriteHeight);
    }

    private int getMaxScrollDistance() {
        return this.orientation == Orientation.VERTICAL ? this.getContentHeight() - this.getHeight() : this.getContentWidth() - this.getWidth();
    }

    private int getScrollerY(float scrollPercentage) {
        if (this.orientation == Orientation.VERTICAL) {
            return (int) (this.getTop() + scrollPercentage * (this.getHeight() - 7));
        }
        return this.getTop() + (this.rows * this.getItemHeight());
    }

    protected int getScrollerX(float scrollPercentage) {
        if (this.orientation == Orientation.HORIZONTAL) {
            return (int) (this.getLeft() + scrollPercentage * (this.getWidth() - 7));
        }
        return this.getLeft() + (this.cols * this.getItemWidth());
    }

    protected int getScissorsHeight() {
        return Math.min(this.rows * this.getItemHeight(), this.getHeight());
    }

    protected int getScissorsWidth() {
        return Math.min(this.cols * this.getItemWidth(), this.getWidth());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean canScroll;
        if (this.orientation == Orientation.VERTICAL) {
            canScroll = this.getContentHeight() > this.getHeight();
        } else {
            canScroll = this.getContentWidth() > this.getWidth();
        }
        if (canScroll) {
            double rawScrollOffset = Math.max(this.scrollOffset - scrollY * 7, 0);
            int  maxScrollOffset = this.getMaxScrollDistance() + 1;
            if (rawScrollOffset > maxScrollOffset) {
                this.scrollOffset = this.getMaxScrollDistance();
            } else {
                this.scrollOffset = (int) rawScrollOffset;
            }
        }
        return true;
    }

    public boolean isScrollbarHovered(int mouseX, int mouseY) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered() && !this.isScrollbarHovered((int) mouseX, (int) mouseY)) {
            int left = this.getLeft() + (this.hoveredXIndex * this.getItemWidth());
            int top = this.getTop() + (this.hoveredYIndex * this.getItemHeight());
            if (this.hoveredItem != null) {
                this.clickedItem(this.hoveredItem, this.hoveredXIndex, this.hoveredYIndex, left, top, (int) mouseX, (int) mouseY);
                return super.mouseClicked(mouseX, mouseY, button);
            }
        } else if (this.isHovered() && this.isScrollbarHovered((int) mouseX, (int) mouseY) && this.renderScroller) {
            int scrollableDistance = this.getMaxScrollDistance();
            int minPos = (this.orientation == Orientation.HORIZONTAL ? this.getX() : this.getY()) + 7;
            int maxPos = (this.orientation == Orientation.HORIZONTAL ? this.getX() + this.getWidth() : this.getY() + this.getHeight()) - 7;

            double scrolledPercentage = ((Math.clamp(mouseY, minPos, maxPos) - minPos)) / (double) (maxPos - minPos);

            this.scrollOffset = (int) (scrollableDistance * scrolledPercentage);
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

    public void setItems(Collection<E> items) {
        this.items = items;
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

    public int getContentWidth() {
        return (this.getItems().size() / this.rows) * this.getItemWidth();
    }

    public int getContentHeight() {
        return (this.getItems().size() / this.cols) * this.getItemHeight();
    }

    protected int getTop() {
        return this.getY() + 1;
    }

    protected int getLeft() {
        return this.getX() + 1;
    }

    protected void renderContainer(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int xIndex = 0;
        int yIndex = 0;
        boolean isAnyHovered = false;
        for (E item : this.getItems()) {
            if (this.isItemHovered(xIndex, yIndex, mouseX, mouseY) && guiGraphics.containsPointInScissor(mouseX, mouseY)) {
                this.hoveredItem = item;
                isAnyHovered = true;
            }
            int left = this.getLeft() + xIndex * this.getItemWidth();
            int top = this.getTop() + yIndex * this.getItemHeight();
            if (this.orientation == Orientation.HORIZONTAL) {
                left -= this.scrollOffset;
            } else {
                top -= this.scrollOffset;
            }
            this.renderItem(guiGraphics, item, xIndex, yIndex, left, top, mouseX, mouseY);
            if (this.orientation == Orientation.HORIZONTAL || xIndex < cols - 1) {
                xIndex++;
            } else {
                xIndex = 0;
                yIndex++;
            }
        }

        if (!isAnyHovered) {
            this.hoveredItem = null;
        }
    }

    public boolean isItemHovered(int index, int mouseX, int mouseY) {
        return this.isItemHovered(0, index, mouseX, mouseY);
    }

    public boolean isItemHovered(int indexX, int indexY, int mouseX, int mouseY) {
        int scrollOffsetX = 0;
        int scrollOffsetY = 0;
        if (this.orientation == Orientation.HORIZONTAL) {
            scrollOffsetX = this.scrollOffset;
        } else {
            scrollOffsetY = this.scrollOffset;
        }
        return mouseX > this.getLeft() + (this.getItemWidth() * indexX) - scrollOffsetX
                && mouseX < this.getLeft() + (this.getItemWidth() * (indexX + 1)) - scrollOffsetX
                && mouseY > this.getTop() + (this.getItemHeight() * indexY) - scrollOffsetY
                && mouseY < this.getTop() + (this.getItemHeight() * (indexY + 1)) - scrollOffsetY;
    }

	protected void sortEntriesBy(java.util.Comparator<? super E> comparator) {
		if (this.items instanceof java.util.List<E> list) {
			list.sort(comparator);
		}
	}

    public void clickedItem(E item, int index, int left, int top, int mouseX, int mouseY) {
        this.clickedItem(item, 0, index, left, top, mouseX, mouseY);
    }

    public abstract void clickedItem(E item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY);

    public final void renderItem(GuiGraphics guiGraphics, E item, int index, int left, int top, int mouseX, int mouseY) {
        this.renderItem(guiGraphics, item, 0, index, left, top, mouseX, mouseY);
    }

    public final void renderItem(GuiGraphics guiGraphics, E item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
        if (guiGraphics.containsPointInScissor(mouseX, mouseY) && this.isItemHovered(xIndex, yIndex, mouseX, mouseY)) {
            this.hoveredItem = item;
            this.hoveredXIndex = xIndex;
            this.hoveredYIndex = yIndex;
        }
        this.internalRenderItem(guiGraphics, item, xIndex, yIndex, left, top, mouseX, mouseY);
    }

    protected abstract void internalRenderItem(GuiGraphics guiGraphics, E item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY);

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public enum Orientation {
        HORIZONTAL(SCROLLER_SMALL_HORIZONTAL_SPRITE, 7, 4),
        VERTICAL(SCROLLER_SMALL_SPRITE, 4, 7);

        private final ResourceLocation scrollerSprite;
        private final int spriteWidth;
        private final int spriteHeight;

        Orientation(ResourceLocation scrollerSprite, int spriteWidth, int spriteHeight) {
            this.scrollerSprite = scrollerSprite;
            this.spriteWidth = spriteWidth;
            this.spriteHeight = spriteHeight;
        }
    }
}
