package com.portingdeadmods.researchd.client.impl.methods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.research.ClientResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.List;

// Can be a singleton cuz its client only and therefore only needs to exist once at a time
public class ClientConsumePackResearchMethod implements ClientResearchMethod<ConsumePackResearchMethod> {
    public static final ClientConsumePackResearchMethod INSTANCE = new ClientConsumePackResearchMethod();

    private ClientConsumePackResearchMethod() {
    }

    @Override
    public void renderInfo(GuiGraphics guiGraphics, ConsumePackResearchMethod method, int x, int y, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        Component methodType = method.getTranslation();

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.scale(0.8f, 0.8f, 1f);

            int xMargin = 14;
            int yMargin = 16;

            guiGraphics.drawString(font, methodType, x + xMargin, y + yMargin, -1, false);

            renderPacks(font, guiGraphics, method, 0, x + 35 + xMargin, y + yMargin);
            
            guiGraphics.drawString(font,  method.duration() + "t", x + xMargin, y + yMargin, -1, false);
        }
        poseStack.popPose();

        int xPos = x - 3;
        int yPos = y + getSize(method, mouseX, mouseY).height;

        guiGraphics.fill(xPos, yPos, xPos + 80, yPos + 1, -1);
    }

    @Override
    public Size2i getSize(ConsumePackResearchMethod methods, int mouseX, int mouseY) {
        return new Size2i(32, 32);
    }

    private void renderPacks(Font font, GuiGraphics guiGraphics, ConsumePackResearchMethod method, int i, int x, int y) {
        PoseStack poseStack = guiGraphics.pose();
        int w = 24;

        guiGraphics.drawString(font, String.valueOf(method.count()), x + i * (w * 0.65f), y, -1, false);

        poseStack.pushPose();
        {
            poseStack.scale(0.65f, 0.65f, 1);

            List<ItemStack> packs = method.asStacks();

            for (int j = 0; j < packs.size(); j++) {
                ItemStack pack = packs.get(j);
                guiGraphics.renderItem(pack, x + 64 + i * w + j * (w / 2), y + 44);
            }
        }
        poseStack.popPose();
    }

}
