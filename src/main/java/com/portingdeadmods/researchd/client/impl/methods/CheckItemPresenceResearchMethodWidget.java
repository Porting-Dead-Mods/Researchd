package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.compat.JEICompat;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.impl.research.method.CheckItemPresenceResearchMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckItemPresenceResearchMethodWidget extends AbstractResearchInfoWidget<CheckItemPresenceResearchMethod> {
    private final CycledItemRenderer itemRenderer;

    public CheckItemPresenceResearchMethodWidget(int x, int y, CheckItemPresenceResearchMethod method) {
        super(x, y, method);
        this.itemRenderer = new CycledItemRenderer(method.target(), method.count());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int x = getX();
        int y = getY();
        guiGraphics.fill(x, y, x + this.width, y + this.height, FastColor.ARGB32.color(69, 69, 69));
        this.itemRenderer.render(guiGraphics, x, y);
        this.itemRenderer.tick(v);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        if (this.isHovered()) {
            Ingredient target = value.target();
            ItemStack stack = new ItemStack(target.getItems()[0].getItem(), value.count());
            List<Component> tooltip = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stack));
            tooltip.addFirst(
                    Component.literal("Obtain ").withStyle(ChatFormatting.WHITE).append(
                    Component.literal("%d".formatted(value.count())).withStyle(ChatFormatting.GOLD)).append(
                    Component.literal(":").withStyle(ChatFormatting.WHITE))
            );
            guiGraphics.renderTooltip(font, tooltip, stack.getTooltipImage(), stack, mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (this.isHovered()) {
            if (ResearchdCompatHandler.isJeiLoaded()) {
                Ingredient recipes1 = this.value.target();
                JEICompat.openRecipes(Arrays.asList(recipes1.getItems()));
            }
        }
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16);
    }

}
