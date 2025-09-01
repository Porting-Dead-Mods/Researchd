package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.List;


public class ClientConsumePackResearchMethod extends AbstractResearchInfoWidget<ConsumePackResearchMethod> {
    public static final int GAP_BETWEEN_PACKS = 4;

    public final int COUNT;
    public final int DURATION;
    public final int TYPES;

    public final int TEXT_WIDTH;

    public ClientConsumePackResearchMethod(int x, int y, ConsumePackResearchMethod method) {
        super(x, y, method);

        this.COUNT = method.count();
        this.DURATION = method.duration();
        this.TYPES = method.packs().size();

        this.TEXT_WIDTH = Minecraft.getInstance().font.width(" x %dt".formatted(DURATION));

        this.setWidth(16 + GAP_BETWEEN_PACKS * method.packs().size() + this.TEXT_WIDTH);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int x = getX();
        int y = getY();
        guiGraphics.fill(x, y, x + this.width, y + this.height, FastColor.ARGB32.color(69, 69, 69));

        List<ItemStack> stacks = method.asStacks().stream().map(ItemStack::copy).toList();
        stacks.forEach(s -> s.setCount(1));

        for (int idx = 0; idx < stacks.size(); idx++) {
            ItemStack stack = stacks.get(idx);
            int xPos = x + idx * GAP_BETWEEN_PACKS;
            guiGraphics.renderItem(stack, xPos, y);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            {
                if (idx == stacks.size() - 1) {
                    guiGraphics.drawString(Minecraft.getInstance().font,
                            String.valueOf(COUNT),
                            xPos + 17 - Minecraft.getInstance().font.width(String.valueOf(COUNT)),
                            y + 9,
                            16777215,
                            true);
                }
            }
            guiGraphics.pose().popPose();
        }

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                " x %dt".formatted(DURATION),
                x + 14 + GAP_BETWEEN_PACKS * stacks.size(),
                y + 4,
                16777215,
                true
        );
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        if (this.isHovered()) {
            guiGraphics.renderTooltip(font,
                    Component.literal("Consume ").append(
                    Component.literal("%d".formatted(COUNT)).withStyle(ChatFormatting.GOLD)).append(
                    Component.literal(COUNT == 1 ? " pack for " : " packs for ")).append(
                    Component.literal("%d".formatted(DURATION)).withStyle(ChatFormatting.GOLD)).append(
                    Component.literal(DURATION == 1 ? " tick" : " ticks")).append(
                    Component.literal(COUNT == 1 ? "." : " each.")),
                    mouseX, mouseY);
        }
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16);
    }
}