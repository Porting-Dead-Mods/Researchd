package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.ItemResearchIcon;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ItemSelectorWidget extends AbstractWidget {
    private final ResearchScreen screen;
    private ItemStack selected;

    public ItemSelectorWidget(ResearchScreen screen, int x, int y, Component message) {
        super(x, y, 18, 18, message);
        this.screen = screen;
        this.setTooltip(Tooltip.create(Component.literal("Select Icon")));
        this.selected = new ItemStack(Items.DIRT);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.selected != null) {
            guiGraphics.renderItem(this.selected, this.getX() + 1, this.getY() + 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.screen.openPopupCentered(new SelectorPopupWidget(0, 0, CommonComponents.EMPTY));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public List<ItemStack> getSelected() {
        return List.of(selected);
    }

    public ItemResearchIcon createIcon() {
        return new ItemResearchIcon(this.getSelected());
    }

    public static class SelectorPopupWidget extends DraggablePopupWidget {
        private final LinearLayout layout;

        public SelectorPopupWidget(int x, int y, Component message) {
            super(x, y, 160, 160, message);
            this.layout = LinearLayout.vertical();
            this.layout.addChild(new SelectorContainerWidget(128, 128, 16, 16, BuiltInRegistries.ITEM.stream().map(ItemStack::new).toList(), true), LayoutSettings::alignHorizontallyCenter);
            this.layout.arrangeElements();
            this.layout.visitWidgets(this::addRenderableWidget);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            this.renderElements(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.drawString(getFont(), "Hello", this.getX(), this.getY(), -1);
        }

        @Override
        public Layout getLayout() {
            return this.layout;
        }
    }

    public static class SelectorContainerWidget extends ContainerWidget<ItemStack> {
        private ItemStack hoveredItem;

        public SelectorContainerWidget(int width, int height, int itemWidth, int itemHeight, Collection<ItemStack> items, boolean renderScroller) {
            super(width, height, itemWidth, itemHeight, items, renderScroller);
        }

        @Override
        public void renderContainer(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            this.hoveredItem = null;

            int index = 0;
            int x = 0;
            int y = 0;
            for (ItemStack item : this.getItems()) {
                if (this.isItemHovered(x, y, mouseX, mouseY) && guiGraphics.containsPointInScissor(mouseX, mouseY)) {
                    this.hoveredItem = item;
                }
                this.renderItem(guiGraphics, item, x, y, this.getLeft() + x * this.getItemWidth(), this.getTop() + y * this.getItemHeight() - this.scrollOffset, mouseX, mouseY);
                if (x < (this.width / this.getItemWidth()) - 1) {
                    x++;
                } else {
                    x = 0;
                    y++;
                }
                index++;
            }
        }

        @Override
        protected void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
            super.renderTooltips(guiGraphics, mouseX, mouseY, v);

            if (this.hoveredItem != null) {
                guiGraphics.renderTooltip(PopupWidget.getFont(), this.hoveredItem.getHoverName(), mouseX, mouseY);
            }
        }

        @Override
        protected void renderScroller(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float percentage = (float) this.scrollOffset / (this.getContentHeight() - this.getHeight());
            if (Float.isNaN(percentage)) {
                percentage = 0;
            }
            guiGraphics.blitSprite(SCROLLER_SMALL_SPRITE, this.getLeft() + this.getWidth() + 3, (int) (this.getTop() + percentage * (this.getHeight() - 7)), 4, 7);
        }

        @Override
        protected int getScissorsWidth() {
            return this.getWidth();
        }

        @Override
        public void clickedItem(ItemStack item, int index, int left, int top, int mouseX, int mouseY) {
        }

        @Override
        public void renderItem(GuiGraphics guiGraphics, ItemStack item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
            guiGraphics.renderItem(item, left, top);
        }
    }

}
