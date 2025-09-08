package com.portingdeadmods.researchd.api.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CycledItemRenderer {
    public static final int CYCLE_INTERVAL = 35;
    private List<ItemStack> items;
    private int index;
    private int count;

    public CycledItemRenderer(int count) {
        this(new ArrayList<>(), count);
    }

    public CycledItemRenderer(Ingredient ingredient, int count) {
        this(count);
        this.setItems(ingredient);
    }

    public CycledItemRenderer(List<ItemStack> items, int count) {
        this.items = items;
        this.count = count;
    }

    public void setItems(Ingredient ingredient) {
        this.items.clear();
        for (ItemStack item : ingredient.getItems()) {
            this.items.add(item.copyWithCount(this.count));
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        if (!this.items.isEmpty()) {
            guiGraphics.renderFakeItem(getItem(), x, y);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, getItem(), x, y);
        }
    }

    public ItemStack getItem() {
        if (index < this.items.size()) {
            return this.items.get(index);
        }
        return ItemStack.EMPTY;
    }

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level.getGameTime() % CYCLE_INTERVAL == 0) {
            if (index + 1 < items.size()) {
                index++;
            } else {
                index = 0;
            }
        }
    }
}
