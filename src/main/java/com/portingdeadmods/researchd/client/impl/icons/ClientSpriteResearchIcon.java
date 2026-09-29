package com.portingdeadmods.researchd.client.impl.icons;

import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.impl.research.icons.SpriteResearchIcon;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;

public record ClientSpriteResearchIcon(SpriteResearchIcon icon) implements ClientResearchIcon<SpriteResearchIcon> {
    @Override
    public void render(
            GuiGraphicsExtractor guiGraphics,
            int panelLeft,
            int panelTop,
            int mouseX,
            int mouseY,
            float scale,
            float partialTicks) {
        if (scale != 1) {
            Matrix3x2fStack poseStack = guiGraphics.pose();

            poseStack.pushMatrix();
            {
                poseStack.translate(panelLeft, panelTop);
                poseStack.scale(scale, scale);

                int itemX = (ResearchScreenWidget.PANEL_WIDTH - 16) / 2; // center item horizontally
                int itemY = (ResearchScreenWidget.PANEL_HEIGHT - 18) / 2; // center item vertically
                GuiUtils.drawImg(guiGraphics, this.icon.sprite(), itemX, itemY, this.icon.width(), this.icon.height());
            }
            poseStack.popMatrix();
        } else {
            GuiUtils.drawImg(
                    guiGraphics, this.icon.sprite(), panelLeft, panelTop, this.icon.width(), this.icon.height());
        }
    }
}
