package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category;

import com.google.common.base.Suppliers;
import com.portingdeadmods.researchd.compat.JEICompat;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public enum DefaultItemSelectorCategory implements ItemSelectorCategory {
    JEI(ResearchdCompatHandler::isJeiLoaded, JEICompat::getItems, Items.APPLE::getDefaultInstance, () -> Component.literal("Jei")),
    ALL(() -> true, () -> BuiltInRegistries.ITEM.stream().map(ItemStack::new).toList(), Items.COMPASS::getDefaultInstance, () -> Component.literal("All Items")),
    INV(() -> true, () -> ClientEditorHelper.getPlayerInventory().items, Items.CHEST::getDefaultInstance, () -> Component.literal("Inventory"));

    private final BooleanSupplier exists;
    private final Supplier<Collection<ItemStack>> itemsGetter;
    private final Supplier<ItemStack> icon;
    private final Supplier<Component> name;

    DefaultItemSelectorCategory(BooleanSupplier exists, com.google.common.base.Supplier<Collection<ItemStack>> itemsGetter, Supplier<ItemStack> icon, Supplier<Component> name) {
        this.exists = exists;
        this.itemsGetter = Suppliers.memoize(itemsGetter);
        this.icon = icon;
        this.name = name;
    }

    @Override
    public boolean exists() {
        return this.exists.getAsBoolean();
    }

    @Override
    public Collection<ItemStack> getItems() {
        return this.itemsGetter.get();
    }

    @Override
    public ItemStack getIcon() {
        return this.icon.get();
    }

    @Override
    public Component getName() {
        return name.get();
    }

    public static DefaultItemSelectorCategory getDefault() {
        return DefaultItemSelectorCategory.JEI.exists() ? DefaultItemSelectorCategory.JEI : DefaultItemSelectorCategory.ALL;
    }
}
