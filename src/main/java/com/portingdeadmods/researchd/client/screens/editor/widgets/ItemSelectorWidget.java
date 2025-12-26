package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
import com.portingdeadmods.researchd.compat.JEICompat;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.impl.research.ItemResearchIcon;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import com.portingdeadmods.researchd.utils.Search;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ItemSelectorWidget extends AbstractWidget {
    private final ResearchScreen screen;
    @Nullable
    private final PopupWidget parentPopupWidget;
    private ItemStack selected;

    public ItemSelectorWidget(ResearchScreen screen, @Nullable PopupWidget parentPopupWidget, int x, int y, Component message) {
        super(x, y, 18, 18, message);
        this.screen = screen;
        this.parentPopupWidget = parentPopupWidget;
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
        if (this.isHovered()) {
            if (this.parentPopupWidget != null) {
                this.screen.closePopup(this.parentPopupWidget);
            }
            this.screen.openPopupCentered(new SelectorPopupWidget(this.screen, this, this.parentPopupWidget, 0, 0, CommonComponents.EMPTY));
        }
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

    public static class SelectorPopupWidget extends PopupWidget {
        public static final ResourceLocation BACKGROUND_SPRITE = Researchd.rl("widget/item_selector_widget");
        public static final ResourceLocation TAB_BIG_SPRITE = Researchd.rl("tab_big");
        public static final ResourceLocation TAB_SMALL_SPRITE = Researchd.rl("tab_small");

        private final EditBox searchBar;
        private final Search search;
        private final SelectorContainerWidget containerWidget;
        private final PDLImageButton doneButton;
        private final ResearchScreen screen;
        private final ItemSelectorWidget parentSelectorWidget;
        @Nullable
        private final PopupWidget parentPopupWidget;
        private Collection<ItemStack> allItems;
        private Collection<ItemStack> filteredItems;
        private Category category;

        public SelectorPopupWidget(ResearchScreen screen, ItemSelectorWidget parentSelectorWidget, @Nullable PopupWidget parentPopupWidget, int x, int y, Component message) {
            super(x, y, 180, 194, message);
            this.screen = screen;
            this.parentSelectorWidget = parentSelectorWidget;
            this.parentPopupWidget = parentPopupWidget;
            this.search = new Search();
            this.category = Category.JEI.exists() ? Category.JEI : Category.ALL;
            this.allItems = this.category.getItems();
            this.filteredItems = allItems;
            this.searchBar = this.addRenderableWidget(new EditBox(Minecraft.getInstance().font, x, y, 132, 12, CommonComponents.EMPTY));
            this.searchBar.setBordered(false);
            this.searchBar.setEditable(true);
            this.searchBar.setResponder(this::onSearchBarValueChanged);
            this.containerWidget = this.addRenderableWidget(new SelectorContainerWidget(this, 160 - 14, 160 - 13, 16, 16, this.filteredItems, true));
            this.doneButton = this.addRenderableWidget(PDLImageButton.builder(this::onDoneClicked)
                    .size(14, 14)
                    .tooltip(Tooltip.create(Component.literal("Select Item")))
                    .sprites(new WidgetSprites(Researchd.rl("editor_checkmark_button"), Researchd.rl("editor_checkmark_button_disabled"), Researchd.rl("editor_checkmark_button_highlighted")))
                    .build());
            this.doneButton.active = false;
            this.setPosition(x, y);
        }

        private void onDoneClicked(PDLImageButton button) {
            this.screen.closePopup(this);
            if (this.parentPopupWidget != null) {
                this.screen.openPopupCentered(this.parentPopupWidget);
                this.parentSelectorWidget.selected = this.containerWidget.selectedItem;
            }
        }

        private void onSearchBarValueChanged(String val) {
            List<ItemStack> items = new ArrayList<>(this.allItems.size());
            for (ItemStack item : allItems) {
                if (this.search.matches(item.getHoverName().getString(), val)) {
                    items.add(item);
                }
            }
            this.filteredItems = items;
            this.containerWidget.setItems(this.filteredItems);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            for (Category category : Category.values()) {
                if (category.exists()) {
                    boolean selected = category == this.category;
                    int x = this.getX() + category.ordinal() * 22 + 4 - (selected ? 1 : 0);
                    int y = this.getY() + (selected ? 0 : 2);
                    guiGraphics.blitSprite(selected ? TAB_BIG_SPRITE : TAB_SMALL_SPRITE, x, y, 20, 20);
                    int iconOffset = selected ? 2 : 1;
                    guiGraphics.renderItem(category.icon.get(), x + iconOffset, y + iconOffset);
                    if (mouseX > x && mouseX < x + (selected ? 20 : 18) && mouseY > y && mouseY < y + 16 + (selected ? 2 : 0)) {
                        guiGraphics.renderTooltip(getFont(), category.name.get(), mouseX, mouseY);
                    }
                }
            }

            guiGraphics.blitSprite(BACKGROUND_SPRITE, this.getX(), this.getY() + 18, this.width, 176);

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            this.renderElements(guiGraphics, mouseX, mouseY, partialTick);
            //guiGraphics.drawString(getFont(), "Hello", this.getX(), this.getY(), -1);
        }

        private boolean isSearchBarHovered(int mouseX, int mouseY) {
            int x = this.getX() + 5;
            int y = this.getY() + 21;
            int width = 150;
            int height = 14;
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (Category category : Category.values()) {
                if (category.exists()) {
                    boolean selected = category == this.category;
                    int x = this.getX() + category.ordinal() * 22 + 4 - (selected ? 1 : 0);
                    int y = this.getY() + (selected ? 0 : 2);
                    if (mouseX > x && mouseX < x + (selected ? 20 : 18) && mouseY > y && mouseY < y + 16 + (selected ? 2 : 0)) {
                        this.category = category;
                        this.allItems = category.getItems();
                        this.filteredItems = this.allItems;
                        this.searchBar.setValue("");
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            this.searchBar.setX(x + 7);
            this.containerWidget.setX(x + 7);
            this.doneButton.setX(x + 162);
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            this.searchBar.setY(y + 26);
            this.containerWidget.setY(y + 40);
            this.doneButton.setY(y + 173);
        }

        @Override
        public Layout getLayout() {
            return null;
        }

        public enum Category {
            JEI(ResearchdCompatHandler::isJeiLoaded, JEICompat::getItems, Items.APPLE::getDefaultInstance, () -> Component.literal("Jei")),
            ALL(() -> true, () -> BuiltInRegistries.ITEM.stream().map(ItemStack::new).toList(), Items.COMPASS::getDefaultInstance, () -> Component.literal("All Items")),
            INV(() -> true, () -> ClientEditorHelper.getPlayerInventory().items, Items.CHEST::getDefaultInstance, () -> Component.literal("Inventory"));

            private final BooleanSupplier exists;
            private final Supplier<Collection<ItemStack>> itemsGetter;
            private final Supplier<ItemStack> icon;
            private final Supplier<Component> name;

            Category(BooleanSupplier exists, Supplier<Collection<ItemStack>> itemsGetter, Supplier<ItemStack> icon, Supplier<Component> name) {
                this.exists = exists;
                this.itemsGetter = itemsGetter;
                this.icon = icon;
                this.name = name;
            }

            public boolean exists() {
                return this.exists.getAsBoolean();
            }

            public Collection<ItemStack> getItems() {
                return this.itemsGetter.get();
            }
        }
    }

    public static class SelectorContainerWidget extends ContainerWidget<ItemStack> {
        private final ItemSelectorWidget.SelectorPopupWidget selectorWidget;
        private ItemStack selectedItem;

        public SelectorContainerWidget(ItemSelectorWidget.SelectorPopupWidget selectorWidget, int width, int height, int itemWidth, int itemHeight, Collection<ItemStack> items, boolean renderScroller) {
            super(width, height, itemWidth, itemHeight, Orientation.VERTICAL, 9, 10, items, renderScroller);
            this.selectorWidget = selectorWidget;
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
        protected int getScissorsHeight() {
            return super.getScissorsHeight() + 1;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean clicked = super.mouseClicked(mouseX, mouseY, button);
            if (this.hoveredItem == null && this.isHovered()) {
                this.selectedItem = null;
                this.selectorWidget.doneButton.active = false;
            }
            return clicked;
        }

        @Override
        public void clickedItem(ItemStack item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
            this.selectedItem = item.copy();
            this.selectorWidget.doneButton.active = true;
        }

        @Override
        protected void internalRenderItem(GuiGraphics guiGraphics, ItemStack item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
            guiGraphics.renderItem(item, left, top);
            if (this.selectedItem == item) {
                guiGraphics.renderOutline(left, top, this.getItemWidth(), this.getItemHeight(), -1);
            }
        }
    }

}
