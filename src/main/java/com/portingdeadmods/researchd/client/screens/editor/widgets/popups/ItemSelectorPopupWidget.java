package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.ItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
import com.portingdeadmods.researchd.utils.Search;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemSelectorPopupWidget extends PopupWidget {
    public static final ResourceLocation BACKGROUND_SPRITE = Researchd.rl("widget/item_selector_widget");
    public static final ResourceLocation BACKGROUND_NO_SEARCHBAR_SPRITE = Researchd.rl("widget/item_selector_widget_no_searchbar");
    public static final ResourceLocation TAB_BIG_SPRITE = Researchd.rl("tab_big");
    public static final ResourceLocation TAB_SMALL_SPRITE = Researchd.rl("tab_small");
    public static final WidgetSprites SPRITES = new WidgetSprites(Researchd.rl("editor_checkmark_button"), Researchd.rl("editor_checkmark_button_disabled"), Researchd.rl("editor_checkmark_button_highlighted"));

    private EditBox searchBar;
    private final Search search;
    private AbstractWidget containerWidget;
    private final PDLImageButton doneButton;
    private final ItemSelectorWidget parentSelectorWidget;
    @Nullable
    private final PopupWidget parentPopupWidget;
    private Collection<ItemStack> allItems;
    private Collection<ItemStack> filteredItems;
    private ItemSelectorCategory selectedCategory;
    private final List<ItemSelectorCategory> categories;

    public ItemSelectorPopupWidget(ItemSelectorWidget parentSelectorWidget, @Nullable PopupWidget parentPopupWidget, List<ItemSelectorCategory> categories, ItemSelectorCategory defaultCategory, int x, int y) {
        super(x, y, 180, 194, CommonComponents.EMPTY);
        this.categories = categories;
        this.parentSelectorWidget = parentSelectorWidget;
        this.parentPopupWidget = parentPopupWidget;
        this.search = new Search();
        this.selectedCategory = defaultCategory;
        this.allItems = this.selectedCategory.getItems();
        this.filteredItems = allItems;
        if (this.selectedCategory.hasSearchBar()) {
            this.createSearchBar(x, y);
        } else {
            this.searchBar = null;
        }
        AbstractWidget widget = this.selectedCategory.createBodyWidget(this, 160 - 15, 160 - 15, this.filteredItems);
        this.containerWidget = this.addRenderableWidget(widget);
        this.doneButton = this.addRenderableWidget(PDLImageButton.builder(this::onDoneClicked)
                .size(14, 14)
                .tooltip(Tooltip.create(Component.literal("Select Item")))
                .sprites(SPRITES)
                .build());
        this.doneButton.active = false;
        this.setPosition(x, y);
    }

    private void createSearchBar(int x, int y) {
        this.searchBar = this.addRenderableWidget(new EditBox(Minecraft.getInstance().font, x, y, 132, 12, CommonComponents.EMPTY));
        this.searchBar.setBordered(false);
        this.searchBar.setEditable(true);
        this.searchBar.setResponder(this::onSearchBarValueChanged);
    }

    private void onDoneClicked(PDLImageButton button) {
        ResearchScreen screen = Spaghetti.tryGetResearchScreen();

        screen.closePopup(this);
        if (this.parentPopupWidget != null) {
            screen.openPopupCentered(this.parentPopupWidget);
            this.parentSelectorWidget.setSelected(this.selectedCategory.getSelectedItems(this.containerWidget).stream().map(ItemStack::copy).toList());
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
        this.selectedCategory.setItems(this.containerWidget, this.filteredItems);
        this.selectedCategory.resetScrollOffset(this.containerWidget);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        List<ItemSelectorCategory> itemSelectorCategories = this.categories;
        for (int i = 0; i < itemSelectorCategories.size(); i++) {
            ItemSelectorCategory category = itemSelectorCategories.get(i);
            if (category.exists()) {
                boolean selected = category == this.selectedCategory;
                int x = this.getX() + i * 22 + 4 - (selected ? 1 : 0);
                int y = this.getY() + (selected ? 0 : 2);
                guiGraphics.blitSprite(selected ? TAB_BIG_SPRITE : TAB_SMALL_SPRITE, x, y, 20, 20);
                int iconOffset = selected ? 2 : 1;
                guiGraphics.renderItem(category.getIcon(), x + iconOffset, y + iconOffset);
                if (mouseX > x && mouseX < x + (selected ? 20 : 18) && mouseY > y && mouseY < y + 16 + (selected ? 2 : 0)) {
                    guiGraphics.renderTooltip(getFont(), category.getName(), mouseX, mouseY);
                }
            }
        }

        guiGraphics.blitSprite(this.selectedCategory.hasSearchBar() ? BACKGROUND_SPRITE : BACKGROUND_NO_SEARCHBAR_SPRITE, this.getX(), this.getY() + 18, this.width, 176);

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<ItemSelectorCategory> itemSelectorCategories = this.categories;
        for (int i = 0; i < itemSelectorCategories.size(); i++) {
            ItemSelectorCategory category = itemSelectorCategories.get(i);
            if (category.exists()) {
                boolean selected = category == this.selectedCategory;
                int x = this.getX() + i * 22 + 4 - (selected ? 1 : 0);
                int y = this.getY() + (selected ? 0 : 2);
                if (mouseX > x && mouseX < x + (selected ? 20 : 18) && mouseY > y && mouseY < y + 16 + (selected ? 2 : 0)) {
                    this.selectedCategory = category;
                    this.allItems = category.getItems();
                    this.filteredItems = this.allItems;
                    if (this.searchBar != null) {
                        this.searchBar.setValue("");
                    }
                    this.selectedCategory.resetScrollOffset(this.containerWidget);
                    this.widgets.remove(this.containerWidget);
                    this.containerWidget = this.selectedCategory.createBodyWidget(this, 160 - 15, 160 - 15, this.filteredItems);
                    if (this.selectedCategory.hasSearchBar()) {
                        this.createSearchBar(x, y);
                    } else {
                        this.widgets.remove(this.searchBar);
                        this.searchBar = null;
                    }
                    this.addRenderableWidget(this.containerWidget);
                    this.setPosition(this.getX(), this.getY());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        if (this.searchBar != null) {
            this.searchBar.setX(x + 7);
        }
        this.containerWidget.setX(x + 7);
        this.doneButton.setX(x + 162);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        if (this.searchBar != null) {
            this.searchBar.setY(y + 26);
            this.containerWidget.setY(y + 40);
        } else {
            this.containerWidget.setY(y + 26);
        }
        this.doneButton.setY(y + 173);
    }

    @Override
    public Layout getLayout() {
        return null;
    }

    public static class SelectorContainerWidget extends ContainerWidget<ItemStack> {
        private final ItemSelectorPopupWidget selectorWidget;
        private ItemStack selectedItem;

        public SelectorContainerWidget(ItemSelectorPopupWidget selectorWidget, int width, int height, int itemWidth, int itemHeight, Collection<ItemStack> items, boolean renderScroller) {
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

        public List<ItemStack> getSelectedItems() {
            return List.of(selectedItem);
        }

        public void resetScrollOffset() {
            this.scrollOffset = 0;
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
            if (item != null) {
                this.selectedItem = item;
                this.selectorWidget.doneButton.active = true;
            }
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
