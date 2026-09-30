package com.portingdeadmods.researchd.client.screens.research.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.team.ResearchQueue;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.impl.research.SimpleResearchQueue;
import com.portingdeadmods.researchd.networking.research.ResearchQueueRemovePayload;
import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ResearchQueueWidget extends ResearchScreenWidget {
    private static final Identifier BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_queue.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 40;

    private final ResearchScreen screen;

    // TODO: Not final atm so it can be refreshed
    // For some reason it doesn't update properly when another player adds/removes something from the queue
    // Also it's weird since it's passed by reference from the ClientTeam so it *should* be updated... It's the same
    // issue as the TechList and Graph...
    private ResearchQueue queue;

    public void setQueue(ResearchQueue newQueue) {
        this.queue = newQueue;
    }

    private ResearchInstance selected;
    private float selectedX;
    private float selectedY;

    public ResearchQueueWidget(ResearchScreen screen, int x, int y) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.screen = screen;
        ResearchTeam team = ResearchTeamHelperClient.getTeam();
        this.queue = team != null ? team.getQueue() : new SimpleResearchQueue();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        int paddingX = 12;
        int paddingY = 14;

        guiGraphics.text(Minecraft.getInstance().font, "Research Queue", paddingX - 1, 4, -1);

        ResearchTeam team = ResearchTeamHelperClient.getTeam();

        int selectedIndex = 0;
        for (int i = 0; i < this.queue.size(); i++) {
            ResourceKey<Research> key = this.queue.get(i);
            if (this.selected == null || key != this.selected.getResearch()) {
                ResearchInstance instance = team != null ? team.getResearches().get(key) : null;
                renderQueuePanel(guiGraphics, instance, paddingX + i * PANEL_WIDTH, paddingY, mouseX, mouseY, i);
            } else {
                selectedIndex = i;
            }
        }

        if (this.selected != null) {
            renderQueuePanel(
                    guiGraphics, this.selected, (int) selectedX, (int) selectedY, mouseX, mouseY, selectedIndex);
        }
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        super.onDrag(event, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int paddingX = 12;
        int paddingY = 14;

        int index = (int) (mouseX - paddingX) / PANEL_WIDTH;
        if (mouseX > paddingX && mouseY > paddingY && index < this.queue.size()) {
            ResourceKey<Research> researchKey = this.queue.get(index);
            if (this.isHovering(null, (int) mouseX, (int) mouseY, index, paddingY + 17, getWidth(), getHeight() - 17)) {
                this.removeResearch(index);
                this.screen.getTechListWidget().startResearchButton.active = true;
                return super.mouseClicked(event, doubleClick);
            } else if (isHovering(null, (int) mouseX, (int) mouseY, index, paddingY, getWidth(), getHeight())) {
                ResearchTeam team = ResearchTeamHelperClient.getTeam();
                if (team == null) return false;

                this.screen.getSelectedResearchWidget().setSelectedResearch(researchKey);
                this.screen.showGraphForResearch(researchKey);
                return super.mouseClicked(event, doubleClick);
            }
        }

        return false;
    }

    public void removeResearch(int index) {
        if (this.queue.size() > index) {
            ResourceKey<Research> researchKey = this.queue.get(index);
            this.queue.remove(index, true);
            ClientPacketDistributor.sendToServer(new ResearchQueueRemovePayload(researchKey));

            // Instantaneous Effect
            ResearchTeam team = ResearchTeamHelperClient.getTeam();
            if (team != null) {
                team.refreshResearchStatus();
            }
            ResearchTeamHelperClient.refreshResearchScreenData();
        }
    }

    private void renderQueuePanel(
            GuiGraphicsExtractor guiGraphics,
            ResearchInstance instance,
            int x,
            int y,
            int mouseX,
            int mouseY,
            int index) {
        if (instance == null) return;

        if (index == 0) {
            renderResearchingResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, false);
        } else {
            renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, false, PanelSpriteType.NORMAL);
        }

        if (this.isHovering(guiGraphics, mouseX, mouseY, x, y, PANEL_WIDTH, PANEL_HEIGHT)) {
            Font font = Minecraft.getInstance().font;

            int color = ARGB.color(120, 20, 20, 20);
            if (this.isHovering(guiGraphics, mouseX, mouseY, x, y + 17, PANEL_WIDTH, PANEL_HEIGHT - 17)) {
                color = ARGB.color(120, 90, 90, 90);
            }
            guiGraphics.fillGradient(x, y + 17, x + PANEL_WIDTH, y + PANEL_HEIGHT, color, color);

            guiGraphics.text(font, "x", x + 10 - (font.width("x") / 2), y + 16, -1, false);
        }
    }

    private void renderResearchingResearchPanel(
            GuiGraphicsExtractor guiGraphics,
            ResearchInstance instance,
            int x,
            int y,
            int mouseX,
            int mouseY,
            boolean hoverable) {
        PanelSpriteType spriteType = PanelSpriteType.NORMAL;
        ResearchStatus status = instance.getResearchStatus();
        GuiUtils.drawImg(guiGraphics, status.getSpriteTexture(spriteType), x, y, PANEL_WIDTH, spriteType.getHeight());

        ResearchTeam team = ResearchTeamHelperClient.getTeam();
        ResearchProgress rmp = team != null ? team.getResearchProgresses().get(instance.getResearch()) : null;
        float progress = rmp == null ? 0f : (rmp.getProgress() / rmp.getMaxProgress());

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ResearchStatus.RESEARCHED.getSpriteTexture(spriteType),
                x,
                y,
                0,
                0,
                (int) (progress * PANEL_WIDTH),
                spriteType.getHeight(),
                PANEL_WIDTH,
                spriteType.getHeight());

        ClientResearchIcon<?> icon =
                ResearchScreen.CLIENT_ICONS.get(instance.getResearch().identifier());
        if (icon != null) {
            icon.render(guiGraphics, x + 2, y + 2, mouseX, mouseY, 1, 0);
        }

        if (isPanelHovered(guiGraphics, x, y, mouseX, mouseY) && hoverable) {
            int color = -2130706433;
            guiGraphics.fillGradient(x, y, x + 20, y + 20, color, color);
        }
    }

    private boolean isHovering(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        return (guiGraphics == null || guiGraphics.containsPointInScissor(mouseX, mouseY))
                && mouseX >= x
                && mouseY >= y
                && mouseX < x + width
                && mouseY < y + height;
    }

    public ResearchQueue getQueue() {
        return queue;
    }
}
