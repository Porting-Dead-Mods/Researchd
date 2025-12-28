package com.portingdeadmods.researchd.client.impl.icons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class ClientItemResearchIcon implements ClientResearchIcon<ItemResearchIcon> {
    private final ItemResearchIcon icon;
    private final CycledItemRenderer renderer;

    public ClientItemResearchIcon(ItemResearchIcon icon) {
        this.icon = icon;
        List<ItemStack> stacks = icon.items();
        this.renderer = new CycledItemRenderer(stacks.size());
        this.renderer.setItems(stacks);
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
                renderer.render(guiGraphics, itemX, itemY);
            }
            poseStack.popPose();
        } else {
            renderer.render(guiGraphics, panelLeft, panelTop);
        }
    }

    @Override
    public ItemResearchIcon icon() {
        return icon;
    }

}
