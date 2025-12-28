package com.portingdeadmods.researchd.client.impl.icons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.research.icons.TextResearchIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class ClientTextResearchIcon implements ClientResearchIcon<TextResearchIcon> {
    private final TextResearchIcon icon;
    private final Font font;

    public ClientTextResearchIcon(TextResearchIcon icon) {
        this.icon = icon;
        this.font = Minecraft.getInstance().font;
    }

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
                guiGraphics.drawString(this.font, this.icon.text(), itemX, itemY, -1);
            }
            poseStack.popPose();
        } else {
            guiGraphics.drawString(this.font, this.icon.text(), panelLeft, panelTop, -1);
        }
    }

    @Override
    public TextResearchIcon icon() {
        return icon;
    }

}
