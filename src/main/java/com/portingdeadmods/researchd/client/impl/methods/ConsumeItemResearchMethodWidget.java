package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.Size2i;

public class ConsumeItemResearchMethodWidget extends AbstractResearchInfoWidget<ConsumeItemResearchMethod> {
    private final CycledItemRenderer itemRenderer;

    public ConsumeItemResearchMethodWidget(int x, int y, ConsumeItemResearchMethod method) {
        super(x, y, method);
        this.itemRenderer = new CycledItemRenderer(method.toConsume());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int x = getX();
        int y = getY();
        guiGraphics.fill(x, y, x + this.width, y + this.height, FastColor.ARGB32.color(69, 69, 69));
        this.itemRenderer.render(guiGraphics, x, y);
        this.itemRenderer.tick();
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        if (this.isHovered()) {
            Ingredient consume = value.toConsume();
            ItemStack stack = new ItemStack(consume.getItems()[0].getItem(), value.count());
            guiGraphics.renderTooltip(font, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), stack.getTooltipImage(), stack, mouseX, mouseY);
        }
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16);
    }

}
