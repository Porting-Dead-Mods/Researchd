package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public interface ItemSelectorCategory {
    boolean exists();

    Collection<ItemStack> getItems();

    ItemStack getIcon();

    Component getName();
}
