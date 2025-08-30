package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.Size2i;

public class ClientConsumeItemResearchMethod implements ClientResearchMethod<ConsumeItemResearchMethod> {
    public static final ClientConsumeItemResearchMethod INSTANCE = new ClientConsumeItemResearchMethod();

    private ClientConsumeItemResearchMethod() {
    }

    @Override
    public void renderInfo(GuiGraphics guiGraphics, ConsumeItemResearchMethod method, int x, int y, int mouseX, int mouseY) {
        // TODO: Render all ingredients
        Ingredient consume = method.toConsume();
        ItemStack stack = new ItemStack(consume.getItems()[0].getItem(), method.count());
        guiGraphics.renderItem(stack, x, y);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, ConsumeItemResearchMethod method, int x, int y, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        if (ClientResearchMethod.isHovered(x, y, mouseX, mouseY, method)) {
            Ingredient consume = method.toConsume();
            ItemStack stack = new ItemStack(consume.getItems()[0].getItem(), method.count());
            guiGraphics.renderTooltip(font, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), stack.getTooltipImage(), stack, mouseX, mouseY);
        }
    }

    @Override
    public Size2i getSize(ConsumeItemResearchMethod methods, int mouseX, int mouseY) {
        return new Size2i(16, 16);
    }

}
