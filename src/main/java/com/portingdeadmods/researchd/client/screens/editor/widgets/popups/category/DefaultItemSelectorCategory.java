package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category;

import com.google.common.base.Suppliers;
import com.portingdeadmods.researchd.utils.researches.ResearchEditorHelperClient;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

// TODO(26.1 port, JEI): bring back the JEI category (JEICompat::getItems, Items.APPLE icon, "Jei"), the default
// when JEI is loaded, once the JEI integration is back in the source set
public enum DefaultItemSelectorCategory implements ItemSelectorCategory {
    ALL(
            () -> true,
            () -> BuiltInRegistries.ITEM.stream().map(ItemStack::new).toList(),
            Items.COMPASS::getDefaultInstance,
            () -> Component.literal("All Items")),
    INV(
            () -> true,
            () -> ResearchEditorHelperClient.getPlayerInventory().getNonEquipmentItems(),
            Items.CHEST::getDefaultInstance,
            () -> Component.literal("Inventory"));

    private final BooleanSupplier exists;
    private final Supplier<Collection<ItemStack>> itemsGetter;
    private final Supplier<ItemStack> icon;
    private final Supplier<Component> name;

    DefaultItemSelectorCategory(
            BooleanSupplier exists,
            com.google.common.base.Supplier<Collection<ItemStack>> itemsGetter,
            Supplier<ItemStack> icon,
            Supplier<Component> name) {
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
        return DefaultItemSelectorCategory.ALL;
    }
}
