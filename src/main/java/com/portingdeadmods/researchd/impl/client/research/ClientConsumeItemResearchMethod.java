package com.portingdeadmods.researchd.impl.client.research;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumePackResearchMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ClientConsumeItemResearchMethod implements ClientResearchMethod<ConsumeItemResearchMethod> {
    public static final ClientConsumeItemResearchMethod INSTANCE = new ClientConsumeItemResearchMethod();

    // TODO: Make dynamic
    private int height = 8;

    private ClientConsumeItemResearchMethod() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void renderMethodTooltip(GuiGraphics guiGraphics, List<ConsumeItemResearchMethod> methods, int x, int y, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        ConsumeItemResearchMethod method = methods.getFirst();
        Component methodType = method.getTranslation();

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.scale(0.8f, 0.8f, 1f);

            int xMargin = 14;
            int yMargin = 16;

            guiGraphics.drawString(font, methodType, x + xMargin, y + yMargin, -1, false);

            for (int i = 0; i < methods.size(); i++) {
                renderItem(font, guiGraphics, methods.get(i), i, x + 35 + xMargin, y + yMargin);
            }
        }
        poseStack.popPose();

        int xPos = x - 3;
        int yPos = y + height();

        guiGraphics.fill(xPos, yPos, xPos + 80, yPos + 1, -1);
    }

    @Override
    public int height() {
        return height;
    }

    private void renderItem(Font font, GuiGraphics guiGraphics, ConsumeItemResearchMethod method, int i, int x, int y) {
        PoseStack poseStack = guiGraphics.pose();
        int w = 24;

        guiGraphics.drawString(font, String.valueOf(method.count()), x + i * (w * 0.65f), y, -1, false);

        poseStack.pushPose();
        {
            poseStack.scale(0.65f, 0.65f, 1);
            Ingredient consume = method.toConsume();
            guiGraphics.renderItem(consume.getItems()[0], x + 64 + i * w, y + 44);
        }
        poseStack.popPose();
    }
}
