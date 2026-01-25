package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category;

import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ItemSelectorPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.TagCreationWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TagItemSelectorCategory implements ItemSelectorCategory{
    public static final TagItemSelectorCategory INSTANCE = new TagItemSelectorCategory();
    private static final ItemStack ICON = new ItemStack(Items.NAME_TAG);

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Collection<ItemStack> getItems() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getIcon() {
        return ICON;
    }

    @Override
    public Component getName() {
        return Component.literal("Tag");
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }

    @Override
    public AbstractWidget createBodyWidget(ItemSelectorPopupWidget parentPopupWidget, int width, int height, Collection<ItemStack> filteredItems) {
        return new TagCreationWidget(0, 0, width, height);
    }
}
