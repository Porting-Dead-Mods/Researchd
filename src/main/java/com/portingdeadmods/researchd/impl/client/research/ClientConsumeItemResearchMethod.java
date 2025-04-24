package com.portingdeadmods.researchd.impl.client.research;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
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

    @Override
    public void renderMethodInfo(GuiGraphics guiGraphics, ConsumeItemResearchMethod method, int x, int y, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        Component methodType = method.getTranslation();

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.scale(0.8f, 0.8f, 1f);

            int xMargin = 14;
            int yMargin = 16;

            guiGraphics.drawString(font, methodType, (int) ((x + xMargin) / 0.8), (int) ((y + yMargin) / 0.8), -1, false);

            renderItem(font, guiGraphics, method, 0, (int) ((x + 35 + xMargin) / 0.8), (int) ((y + yMargin) / 0.8));
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
