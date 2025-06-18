package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.client.screens.graph.ResearchGraphWidget;
import com.portingdeadmods.researchd.client.screens.list.TechListWidget;
import com.portingdeadmods.researchd.client.screens.queue.ResearchQueueWidget;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import com.portingdeadmods.researchd.utils.researches.data.TechList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResearchScreen extends Screen {
    public static final ResourceLocation SCREEN_TEXTURE = Researchd.rl("textures/gui/screen.png");

    private final TechListWidget techListWidget;
    private final ResearchQueueWidget researchQueueWidget;
    private final ResearchGraphWidget researchGraphWidget;
    private final SelectedResearchWidget selectedResearchWidget;

    public ResearchScreen() {
        super(Component.translatable("screen.researchd.research"));

        // TECH LIST
        this.techListWidget = new TechListWidget(this, 0, 109, 7);
        this.techListWidget.setTechList(new TechList(ClientResearchCache.RESEARCHES.stream().toList()));

        // THIS NEEDS TO BE BEFORE THE GRAPH
        this.selectedResearchWidget = new SelectedResearchWidget(0, 40, SelectedResearchWidget.BACKGROUND_WIDTH, SelectedResearchWidget.BACKGROUND_HEIGHT);
        if (!this.techListWidget.getTechList().entries().isEmpty()) {
            this.selectedResearchWidget.setSelectedResearch(this.techListWidget.getTechList().entries().getFirst());
        }
        // QUEUE
        this.researchQueueWidget = new ResearchQueueWidget(this, 0, 0);

        // GRAPH
        int x = 174;
        this.researchGraphWidget = new ResearchGraphWidget(selectedResearchWidget, x, 8, 300, 253 - 16);
        Minecraft mc = Minecraft.getInstance();
        if (ClientResearchCache.ROOT_NODE != null) {
            this.researchGraphWidget.setGraph(ResearchGraph.fromRootNode(mc.player, ClientResearchCache.ROOT_NODE));
        }
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(this.techListWidget);
        addRenderableWidget(this.researchQueueWidget);
        addRenderableWidget(this.techListWidget.searchButton);
        addRenderableWidget(this.techListWidget.startResearchButton);
        addRenderableWidget(this.researchGraphWidget);
        addRenderableWidget(this.selectedResearchWidget);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(SCREEN_TEXTURE, 0, 0, guiGraphics.guiWidth() + 3, guiGraphics.guiHeight() + 5, 0, 0, 480, 264, 480, 264);
    }

    public ResearchGraphWidget getResearchGraphWidget() {
        return researchGraphWidget;
    }

    public SelectedResearchWidget getSelectedResearchWidget() {
        return selectedResearchWidget;
    }

    public ResearchQueueWidget getResearchQueueWidget() {
        return researchQueueWidget;
    }

    public TechListWidget getTechListWidget() { return techListWidget; }

    public ResearchGraph getResearchGraph() {
        return this.researchGraphWidget.getCurrentGraph();
    }

    public TechList getTechList() {
        return this.techListWidget.getTechList();
    }

    @Override
    public void onClose() {
        super.onClose();

        // Save graph state on close
        if (this.researchGraphWidget != null) {
            this.researchGraphWidget.onClose();
        }
    }
}
