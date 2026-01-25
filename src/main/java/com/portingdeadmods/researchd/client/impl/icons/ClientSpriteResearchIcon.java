package com.portingdeadmods.researchd.client.impl.icons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.impl.research.icons.SpriteResearchIcon;
import net.minecraft.client.gui.GuiGraphics;

public record ClientSpriteResearchIcon(SpriteResearchIcon icon) implements ClientResearchIcon<SpriteResearchIcon> {
    @Override
    public void render(GuiGraphics guiGraphics, int panelLeft, int panelTop, int mouseX, int mouseY, float scale, float partialTicks) {
        if (scale != 1) {
            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();
            {
                poseStack.translate(panelLeft, panelTop, 0);
                poseStack.scale(scale, scale, scale);

                int itemX = (ResearchScreenWidget.PANEL_WIDTH - 16) / 2;       // center item horizontally
                int itemY = (ResearchScreenWidget.PANEL_HEIGHT - 18) / 2;      // center item vertically
                GuiUtils.drawImg(guiGraphics, this.icon.sprite(), itemX, itemY, this.icon.width(), this.icon.height());
            }
            poseStack.popPose();
        } else {
            GuiUtils.drawImg(guiGraphics, this.icon.sprite(), panelLeft, panelTop, this.icon.width(), this.icon.height());
        }
    }

}
