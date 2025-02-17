package com.portingdeadmods.researchd.impl.client.research;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// Can be a singleton cuz its client only and therefore only needs to exist once at a time
public class ClientConsumePackResearchMethod implements ClientResearchMethod<ConsumePackResearchMethod> {
    public static final ClientConsumePackResearchMethod INSTANCE = new ClientConsumePackResearchMethod();

    // TODO: Make dynamic
    private int height = 8;

    private ClientConsumePackResearchMethod() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void renderMethodTooltip(GuiGraphics guiGraphics, List<ConsumePackResearchMethod> rawMethods, int x, int y, int mouseX, int mouseY) {
        List<ConsumePackResearchMethod> methods = (List<ConsumePackResearchMethod>) rawMethods;

        Font font = Minecraft.getInstance().font;
        ConsumePackResearchMethod method = methods.getFirst();
        Component methodType = method.getTranslation();

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.scale(0.8f, 0.8f, 1f);

            int xMargin = 14;
            int yMargin = 16;

            guiGraphics.drawString(font, methodType, x + xMargin, y + yMargin, -1, false);

            for (int i = 0; i < methods.size(); i++) {
                renderPack(font, guiGraphics, methods.get(i), i, x + 35 + xMargin, y + yMargin);
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

    private void renderPack(Font font, GuiGraphics guiGraphics, ConsumePackResearchMethod method, int i, int x, int y) {
        PoseStack poseStack = guiGraphics.pose();
        int w = 24;

        guiGraphics.drawString(font, String.valueOf(method.count()), x + i * (w * 0.65f), y, -1, false);

        poseStack.pushPose();
        {
            poseStack.scale(0.65f, 0.65f, 1);
            guiGraphics.renderItem(method.asStack(), x + 64 + i * w, y + 44);
        }
        poseStack.popPose();
    }

}
