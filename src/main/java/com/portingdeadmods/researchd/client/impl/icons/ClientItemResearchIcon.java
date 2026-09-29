package com.portingdeadmods.researchd.client.impl.icons;

import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

public class ClientItemResearchIcon implements ClientResearchIcon<ItemResearchIcon> {
    private final ItemResearchIcon icon;
    private final CycledItemRenderer renderer;

    public ClientItemResearchIcon(ItemResearchIcon icon) {
        this.icon = icon;
        List<ItemStack> stacks = icon.stacks();
        this.renderer = new CycledItemRenderer(stacks.size());
        this.renderer.setItems(stacks);
    }

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
                renderer.render(guiGraphics, itemX, itemY);
            }
            poseStack.popMatrix();
        } else {
            renderer.render(guiGraphics, panelLeft, panelTop);
        }
    }

    @Override
    public ItemResearchIcon icon() {
        return icon;
    }
}
