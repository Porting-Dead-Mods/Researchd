package com.portingdeadmods.researchd.client.screens.list;

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
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class TechListEntry extends AbstractWidget {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 24;

    private ResearchInstance research;

    public TechListEntry(ResearchInstance research, int x, int y) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
        this.research = research;
    }

    public ResearchInstance getResearch() {
        return research;
    }

    public void setResearch(ResearchInstance research) {
        this.research = research;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        GuiUtils.drawImg(guiGraphics, getResearch().getResearchStatus().getSpriteTexture(), getX(), getY(), WIDTH, HEIGHT);

        RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();
        guiGraphics.renderItem(ResearchHelper.getResearch(getResearch().getResearch(), lookup).icon().getDefaultInstance(), getX() + 2, getY() + 2);

        if (isHovered()) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), getX(), getY(), getX() + 20, getY() + 20, color, color, 0);
        }
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int scale) {
        if (this.visible) {
            this.isHovered = guiGraphics.containsPointInScissor(mouseX, mouseY)
                    && mouseX >= this.getX()
                    && mouseY >= this.getY()
                    && mouseX < this.getX() + (this.width * scale)
                    && mouseY < this.getY() + (this.height * scale);
            guiGraphics.blit(getResearch().getResearchStatus().getSpriteTexture(), getX(), getY(), WIDTH * scale, HEIGHT * scale, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

            RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();

            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();
            {
                poseStack.scale(scale, scale, scale);
                guiGraphics.renderItem(ResearchHelper.getResearch(getResearch().getResearch(), lookup).icon().getDefaultInstance(), getX() - scale / 2 - 3, getY() - (HEIGHT * scale) / (2 * scale) - 13);
            }
            poseStack.popPose();

            if (isHovered()) {
                int color = -2130706433;
                guiGraphics.fillGradient(RenderType.guiOverlay(), getX(), getY(), getX() + 20 * scale, getY() + 20 * scale, color, color, 0);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.research.setResearchStatus(EntryType.RESEARCHED);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TechListEntry entry)) return false;
        return Objects.equals(research, entry.research);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(research);
    }

    public TechListEntry copy() {
        return new TechListEntry(research.copy(), getX(), getY());
    }

}
