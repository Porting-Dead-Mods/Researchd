package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category;

import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ItemSelectorPopupWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public interface ItemSelectorCategory {
    boolean exists();

    Collection<ItemStack> getItems();

    ItemStack getIcon();

    Component getName();

    default boolean hasSearchBar() {
        return true;
    }

    default void resetScrollOffset(AbstractWidget widget) {
        if (widget instanceof ItemSelectorPopupWidget.SelectorContainerWidget containerWidget) {
            containerWidget.resetScrollOffset();
        }
    }

    default List<ItemStack> getSelectedItems(AbstractWidget widget) {
        if (widget instanceof ItemSelectorPopupWidget.SelectorContainerWidget containerWidget) {
            return containerWidget.getSelectedItems();
        }
        return Collections.emptyList();
    }

    default void setItems(AbstractWidget widget, Collection<ItemStack> items) {
        if (widget instanceof ItemSelectorPopupWidget.SelectorContainerWidget containerWidget) {
            containerWidget.setItems(items);
        }
    }

    default AbstractWidget createBodyWidget(ItemSelectorPopupWidget parentPopupWidget, int width, int height, Collection<ItemStack> filteredItems) {
        return new ItemSelectorPopupWidget.SelectorContainerWidget(parentPopupWidget, width, height, 16, 16, filteredItems, true);
    }

    default void visitWidgets(Consumer<AbstractWidget> consumer) {
    }

}
