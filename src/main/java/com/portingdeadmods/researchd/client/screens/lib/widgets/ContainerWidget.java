package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

// TODO: Move to pdl
public abstract class ContainerWidget<E> extends AbstractWidget {
    public static final ResourceLocation SCROLLER_SMALL_SPRITE = Researchd.rl("scroller_small");
    private final int itemWidth;
    private final int itemHeight;
    private Collection<E> items;
    private final boolean renderScroller;
    protected int scrollOffset;
    protected @Nullable E hoveredItem;
    protected int hoveredXIndex;
    protected int hoveredYIndex;

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
    protected final void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        this.hoveredItem = null;
        this.hoveredXIndex = -1;
        this.hoveredYIndex = -1;

        guiGraphics.enableScissor(this.getLeft(), this.getTop(), this.getX() + getScissorsWidth(), this.getY() + getScissorsHeight());
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
        float percentage = (float) this.scrollOffset / (this.getContentHeight() - this.getHeight());
        if (Float.isNaN(percentage)) {
            percentage = 0;
        }
        guiGraphics.blitSprite(SCROLLER_SMALL_SPRITE, this.getLeft() + this.getItemWidth() + 3, (int) (this.getTop() + percentage * (this.getHeight() - 7)), 4, 7);
    }

    protected int getScissorsHeight() {
        return this.getHeight();
    }

    protected int getScissorsWidth() {
        return this.getItemWidth();
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
            this.clickedItem(this.hoveredItem, this.hoveredXIndex, this.hoveredYIndex, left, top, (int) mouseX, (int) mouseY);
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

    public int getContentHeight() {
        return this.getItems().size() * this.getItemHeight();
    }

    protected int getTop() {
        return this.getY() + 1;
    }

    protected int getLeft() {
        return this.getX() + 1;
    }

    protected void renderContainer(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int index = 0;
        for (E item : this.items) {
            this.renderItem(guiGraphics, item, index, this.getLeft(), this.getTop() + index * this.getItemHeight() - this.scrollOffset, mouseX, mouseY);
            index++;
        }
    }

    public boolean isItemHovered(int index, int mouseX, int mouseY) {
        return this.isItemHovered(0, index, mouseX, mouseY);
    }

    public boolean isItemHovered(int indexX, int indexY, int mouseX, int mouseY) {
        return mouseX > this.getLeft() + (this.getItemWidth() * indexX)
                && mouseX < this.getLeft() + (this.getItemWidth() * (indexX + 1))
                && mouseY > this.getTop() + (this.getItemHeight() * indexY) - this.scrollOffset
                && mouseY < this.getTop() + (this.getItemHeight() * (indexY + 1)) - this.scrollOffset;
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
}
