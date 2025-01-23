package com.portingdeadmods.researchd.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;

import javax.annotation.Nullable;

public abstract class ResearchScreenWidget extends AbstractWidget {
    public static final int PANEL_WIDTH = 20;
    public static final int PANEL_HEIGHT = 24;
    
    public ResearchScreenWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, int scale) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, scale, true);
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, int scale, boolean hoverable) {
        int width = PANEL_WIDTH;
        int height = PANEL_HEIGHT;
        guiGraphics.blit(instance.getResearchStatus().getSpriteTexture(), x, y, width * scale, height * scale, 0, 0, width, height, width, height);

        RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.scale(scale, scale, scale);
            guiGraphics.renderItem(ResearchHelper.getResearch(instance.getResearch(), lookup).icon().getDefaultInstance(), x - scale / 2 - 3, y - (height * scale) / (2 * scale) - 13);
        }
        poseStack.popPose();

        if (isHovering(guiGraphics, x, y, mouseX, mouseY, scale) && hoverable) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 20 * scale, y + 20 * scale, color, color, 0);
        }
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, true);
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, boolean hoverable) {
        GuiUtils.drawImg(guiGraphics, instance.getResearchStatus().getSpriteTexture(), x, y, PANEL_WIDTH, PANEL_HEIGHT);

        RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();
        guiGraphics.renderItem(ResearchHelper.getResearch(instance.getResearch(), lookup).icon().getDefaultInstance(), x + 2, y + 2);

        if (isHovering(guiGraphics, x, y, mouseX, mouseY) && hoverable) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 20, y + 20, color, color, 0);
        }
    }

    public static boolean isHovering(@Nullable GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        return isHovering(guiGraphics, x, y, mouseX, mouseY, 1);
    }

    public static boolean isHovering(@Nullable GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, int scale) {
        return (guiGraphics == null || guiGraphics.containsPointInScissor(mouseX, mouseY))
                && mouseX >= x
                && mouseY >= y
                && mouseX < x + PANEL_WIDTH * scale
                && mouseY < y + PANEL_HEIGHT * scale;
    }

}
