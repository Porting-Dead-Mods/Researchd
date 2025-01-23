package com.portingdeadmods.researchd.client.screens.queue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.List;

public class ResearchQueueWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_queue.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 42;

    private final ResearchScreen screen;
    private final ResearchQueue queue;

    public ResearchQueueWidget(ResearchScreen screen, int x, int y) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.screen = screen;
        this.queue = new ResearchQueue();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        int paddingX = 12;
        int paddingY = 17;

        List<ResearchInstance> entries = this.queue.entries();
        for (int i = 0; i < entries.size(); i++) {
            renderQueuePanel(guiGraphics, entries.get(i), paddingX + i * PANEL_WIDTH, paddingY, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int paddingX = 12;
        int paddingY = 17;

        int index = (int) (mouseX - paddingX) / PANEL_WIDTH;
        if (mouseX > paddingX && mouseY > paddingY && index < this.queue.entries().size()) {
            ResearchInstance instance = this.queue.entries().get(index);
            if (this.isHovering(null, (int) mouseX, (int) mouseY, index, paddingY + 17, getWidth(), getHeight() - 17)) {
                Researchd.LOGGER.debug("removing");
                this.screen.getResearchQueue().removeResearch(index);
                return super.mouseClicked(mouseX, mouseY, button);
            } else if (isHovering(null, index, paddingY, (int) mouseX, (int) mouseY)) {
                this.screen.getSelectedResearchWidget().setSelectedResearch(instance);
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        return false;
    }

    public void removeResearch(int index) {
        if (this.queue.entries().size() > index) {
            this.queue.remove(index);
        }
    }

    private void renderQueuePanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY) {
        if (instance == null) return;

        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, false);

        if (this.isHovering(guiGraphics, mouseX, mouseY, x, y + 17, PANEL_WIDTH, PANEL_HEIGHT - 17)) {
            Font font = Minecraft.getInstance().font;

            int color = FastColor.ARGB32.color(120, 90, 90, 90);
            guiGraphics.fillGradient(x, y + 17, x + PANEL_WIDTH, y + PANEL_HEIGHT, color, color);

            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 1000);
                guiGraphics.drawString(font, "x", x + 10 - (font.width("x") / 2), y + 16, -1, false);
            }
            poseStack.popPose();
        }
    }

    private boolean isHovering(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        return (guiGraphics == null ||
                guiGraphics.containsPointInScissor(mouseX, mouseY))
                && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public ResearchQueue getQueue() {
        return queue;
    }
}
