package com.portingdeadmods.researchd.client.screens.editor.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemSelectorWidget extends AbstractWidget {
    private ItemStack selected;

    public ItemSelectorWidget(int x, int y, Component message) {
        super(x, y, 18, 18, message);
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
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public ItemStack getSelected() {
        return selected;
    }
}
