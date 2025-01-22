package com.portingdeadmods.researchd.client.screens.queue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import com.portingdeadmods.researchd.utils.researches.data.TechList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;

public class QueueEntryWidget extends AbstractWidget {
    private ResearchScreen screen;
    private ResearchQueue queue;
    private final TechListEntry queueEntry;
    private final int index;

    public QueueEntryWidget(ResearchScreen screen, TechListEntry queueEntry, int x, int y, int index) {
        super(x, y, TechListEntry.WIDTH, TechListEntry.HEIGHT, CommonComponents.EMPTY);
        this.queueEntry = queueEntry.copy();
        this.queueEntry.setX(x);
        this.queueEntry.setY(y);
        this.index = index;
        this.screen = screen;
    }

    public void setQueue(ResearchQueue queue) {
        this.queue = queue;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        this.queueEntry.renderWidget(guiGraphics, mouseX, mouseY, v);

        if (this.isHovering(guiGraphics, mouseX, mouseY, getX(), getY() + 17, getWidth(), getHeight() - 17)) {
            Font font = Minecraft.getInstance().font;

            int color = FastColor.ARGB32.color(120, 90, 90, 90);
            guiGraphics.fillGradient(getX(), getY() + 17, getX() + getWidth(), getY() + getHeight(), color, color);

            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 1000);
                guiGraphics.drawString(font, "x", getX() + 10 - (font.width("x") / 2), getY() + 16, -1, false);
            }
            poseStack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovering(null, (int) mouseX, (int) mouseY, getX(), getY() + 17, getWidth(), getHeight() - 17)) {
            Researchd.LOGGER.debug("removing");
            this.screen.getResearchQueue().removeResearch(index);
            return super.mouseClicked(mouseX, mouseY, button);
        } else if (this.isHovered()) {
            this.screen.getSelectedResearchWidget().setEntry(this.getEntry());
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    public boolean isHovering(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        return (guiGraphics == null ||
                guiGraphics.containsPointInScissor(mouseX, mouseY))
                && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public TechListEntry getEntry() {
        return this.queueEntry;
    }

    public ResourceKey<Research> getResearch() {
        return this.queueEntry.getResearch().getResearch();
    }

    public ResearchInstance getResearchInstance() {
        return this.queueEntry.getResearch();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
