package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.utils.researches.*;
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
    public static final ResourceLocation TOP_BAR_TEXTURE = Researchd.rl("textures/gui/top_bar.png");
    private static final int TOP_BAR_WIDTH = 377;
    private static final int TOP_BAR_HEIGHT = 8;

    private final TechListWidget techList;
    private final ResearchQueueWidget researchQueueWidget;
    private final ResearchGraphWidget researchGraphWidget;
    private final SelectedResearchWidget selectedResearchWidget;

    public ResearchScreen() {
        super(Component.translatable("screen.researchd.research"));

        // TECH LIST
        this.techList = new TechListWidget(this, 0, 103, 7);
        this.techList.setTechList(new TechList(ClientResearchCache.NODES.stream().map(ResearchNode::getInstance).toList()));

        // QUEUE
        this.researchQueueWidget = new ResearchQueueWidget(this, 0, 0);

        // GRAPH
        int x = 174;
        this.researchGraphWidget = new ResearchGraphWidget(x, 0, 300, 253);
        Minecraft mc = Minecraft.getInstance();
        this.researchGraphWidget.setGraph(ResearchGraph.fromRootNode(mc.player, ClientResearchCache.ROOT_NODE));

        this.selectedResearchWidget = new SelectedResearchWidget(0, 42, SelectedResearchWidget.BACKGROUND_WIDTH, SelectedResearchWidget.BACKGROUND_HEIGHT);
        //this.selectedResearchWidget.setSelectedResearch(this.techList.getTechList().entries().getFirst());
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(this.techList);
        addRenderableWidget(this.researchQueueWidget);
        addRenderableWidget(this.techList.searchButton);
        addRenderableWidget(this.techList.startResearchButton);
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

        GuiUtils.drawImg(guiGraphics, TOP_BAR_TEXTURE, 103, 0, TOP_BAR_WIDTH, TOP_BAR_HEIGHT);
        GuiUtils.drawImg(guiGraphics, TOP_BAR_TEXTURE, 103, Minecraft.getInstance().getWindow().getGuiScaledHeight() - TOP_BAR_HEIGHT, TOP_BAR_WIDTH, TOP_BAR_HEIGHT);
    }

    public ResearchGraphWidget getResearchGraphWidget() {
        return researchGraphWidget;
    }

    public SelectedResearchWidget getSelectedResearchWidget() {
        return selectedResearchWidget;
    }

    public ResearchQueueWidget getResearchQueue() {
        return researchQueueWidget;
    }
}
