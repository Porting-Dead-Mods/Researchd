package com.portingdeadmods.researchd.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.networking.research.ResearchQueueRemovePayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ResearchQueueWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_queue.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 40;

    private final ResearchScreen screen;
    private final ResearchQueue queue;

    private ResearchInstance selected;
    private float selectedX;
    private float selectedY;

    public ResearchQueueWidget(ResearchScreen screen, int x, int y) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.screen = screen;
        this.queue = ResearchdSavedData.TEAM_RESEARCH.get().getData(Minecraft.getInstance().level).getTeamByPlayer(Minecraft.getInstance().player).getResearchProgress().researchQueue();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        int paddingX = 12;
        int paddingY = 14;

        guiGraphics.drawString(Minecraft.getInstance().font, "Research Queue", paddingX - 1, 4, -1);

        List<ResearchInstance> entries = this.queue.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            ResearchInstance instance = entries.get(i);
            if (instance != selected) {
                renderQueuePanel(guiGraphics, instance, paddingX + i * PANEL_WIDTH, paddingY, mouseX, mouseY, i);
            }
        }

        if (selected != null) {
            renderQueuePanel(guiGraphics, selected, (int) selectedX, (int) selectedY, mouseX, mouseY, this.queue.getEntries().indexOf(selected));
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (selected == null) {
            int paddingX = 12;
            int paddingY = 17;

            int index = (int) (mouseX - paddingX) / PANEL_WIDTH;
            if (mouseX > paddingX && mouseY > paddingY && index < this.queue.getEntries().size()) {
                this.selected = this.queue.getEntries().get(index);
                this.selectedX = paddingX + index * PANEL_WIDTH;
                this.selectedY = paddingY;
            }
        }
        this.selectedX += dragX;
        this.selectedY += dragY;
        Researchd.LOGGER.debug("Drag x: {}, Drag y: {}, mouse x: {}, mouse y: {}", dragX, dragY, mouseX, mouseY);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int paddingX = 12;
        int paddingY = 17;

        int index = (int) (mouseX - paddingX) / PANEL_WIDTH;
        if (mouseX > paddingX && mouseY > paddingY && index < this.queue.getEntries().size()) {
            ResearchInstance instance = this.queue.getEntries().get(index);
            if (this.isHovering(null, (int) mouseX, (int) mouseY, index, paddingY + 17, getWidth(), getHeight() - 17)) {
                Researchd.LOGGER.debug("removing");
                this.screen.getResearchQueueWidget().removeResearch(index);
                return super.mouseClicked(mouseX, mouseY, button);
            } else if (isHovering(null, index, paddingY, (int) mouseX, (int) mouseY)) {
                this.screen.getSelectedResearchWidget().setSelectedResearch(instance);
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        return this.isHovered();
    }

    public void removeResearch(int index) {
        if (this.queue.getEntries().size() > index) {
            ResearchInstance instance = this.queue.getEntries().get(index);
            this.queue.remove(index);
            if (index == 0) {
                this.queue.setResearchProgress(0);
            }
            PacketDistributor.sendToServer(new ResearchQueueRemovePayload(instance));
        }
    }

    private void renderQueuePanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, int index) {
        if (instance == null) return;

        if (index == 0) {
            renderResearchingResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, false);
        } else {
            renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, false, PanelSpriteType.NORMAL);
        }

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

    private void renderResearchingResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, boolean hoverable) {
        PanelSpriteType spriteType = PanelSpriteType.NORMAL;
        ResearchStatus status = instance.getResearchStatus();
        GuiUtils.drawImg(guiGraphics, status.getSpriteTexture(spriteType), x, y, PANEL_WIDTH, spriteType.getHeight());
        float progress = (float) queue.getResearchProgress() / queue.getMaxResearchProgress();

        guiGraphics.blit(ResearchStatus.RESEARCHED.getSpriteTexture(spriteType), x, y, 0, 0, (int) (progress * PANEL_WIDTH), spriteType.getHeight(), PANEL_WIDTH, spriteType.getHeight());

        RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();
        guiGraphics.renderItem(ResearchHelperCommon.getResearch(instance.getResearch(), lookup).icon().getDefaultInstance(), x + 2, y + 2);

        if (isHovering(guiGraphics, x, y, mouseX, mouseY) && hoverable) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 20, y + 20, color, color, 0);
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
