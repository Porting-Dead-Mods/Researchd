package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.client.screens.widgets.ResearchGraphWidget;
import com.portingdeadmods.researchd.client.screens.widgets.SelectedResearchWidget;
import com.portingdeadmods.researchd.client.screens.widgets.TechListWidget;
import com.portingdeadmods.researchd.client.screens.widgets.ResearchQueueWidget;
import com.portingdeadmods.researchd.api.data.ResearchGraph;
import com.portingdeadmods.researchd.api.data.TechList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResearchScreen extends Screen {
    public static final ResourceLocation TOP_BAR_TEXTURE = Researchd.rl("textures/gui/top_bar.png");
    public static final ResourceLocation SIDE_BAR_RIGHT_TEXTURE = Researchd.rl("textures/gui/side_bar_right.png");
    private static final int TOP_BAR_WIDTH = 377;
    private static final int TOP_BAR_HEIGHT = 8;
    private static final int SIDE_BAR_WIDTH = 8;
    private static final int SIDE_BAR_HEIGHT = 253;
    public static final ResourceLocation TOP_RIGHT_EDGE = Researchd.rl("textures/gui/research_screen/edges/top_right.png");
    public static final ResourceLocation BOTTOM_RIGHT_EDGE = Researchd.rl("textures/gui/research_screen/edges/bottom_right.png");
    public static final ResourceLocation TOP_BAR = Researchd.rl("textures/gui/research_screen/bars/top.png");
    public static final ResourceLocation BOTTOM_BAR = Researchd.rl("textures/gui/research_screen/bars/bottom.png");
    public static final ResourceLocation RIGHT_BAR = Researchd.rl("textures/gui/research_screen/bars/right.png");

    private final TechListWidget techListWidget;
    private final ResearchQueueWidget researchQueueWidget;
    private final ResearchGraphWidget researchGraphWidget;
    private final SelectedResearchWidget selectedResearchWidget;

    // Used to delete the previous selected method widget
    public AbstractWidget prevSelectedResearchMethodWidget;

    public ResearchScreen() {
        super(Component.translatable("screen.researchd.research"));

        // TECH LIST
        this.techListWidget = new TechListWidget(this, 0, 109, 7);
        this.techListWidget.setTechList(new TechList(ClientResearchCache.GLOBAL_READ_ONLY_RESEARCHES.stream().toList()));

        // THIS NEEDS TO BE BEFORE THE GRAPH
        this.selectedResearchWidget = new SelectedResearchWidget(0, 40, SelectedResearchWidget.BACKGROUND_WIDTH, SelectedResearchWidget.BACKGROUND_HEIGHT);
        if (!this.techListWidget.getTechList().entries().isEmpty()) {
            this.selectedResearchWidget.setSelectedResearch(this.techListWidget.getTechList().entries().getFirst());
        }
        // QUEUE
        this.researchQueueWidget = new ResearchQueueWidget(this, 0, 0);

        // GRAPH
        int x = 174;
        this.researchGraphWidget = new ResearchGraphWidget(this, x, 8, 300, 253 - 16);
        Minecraft mc = Minecraft.getInstance();
        if (ClientResearchCache.ROOT_INSTANCE != null) {
            this.researchGraphWidget.setGraph(ResearchGraph.formRootResearch(mc.player, ClientResearchCache.ROOT_INSTANCE));
        }
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(this.techListWidget);
        addRenderableWidget(this.researchQueueWidget);
        addRenderableWidget(this.techListWidget.searchButton);
        addRenderableWidget(this.techListWidget.startResearchButton);
        addWidget(this.researchGraphWidget);
        this.selectedResearchWidget.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        //GuiUtils.drawImg(guiGraphics, TOP_BAR_TEXTURE, 103, 0, TOP_BAR_WIDTH, TOP_BAR_HEIGHT);
        //GuiUtils.drawImg(guiGraphics, TOP_BAR_TEXTURE, 103, height - TOP_BAR_HEIGHT, TOP_BAR_WIDTH, TOP_BAR_HEIGHT);
        //GuiUtils.drawImg(guiGraphics, SIDE_BAR_RIGHT_TEXTURE, width - 8, 0, SIDE_BAR_WIDTH, SIDE_BAR_HEIGHT);
        GuiUtils.drawImg(guiGraphics, BOTTOM_RIGHT_EDGE, width - 8, height - 8, 8, 8);
        GuiUtils.drawImg(guiGraphics, TOP_RIGHT_EDGE, width - 8, 0, 8, 8);
        int w = 174;
        guiGraphics.blit(TOP_BAR, w, 0, 0, 0, guiGraphics.guiWidth() - w - 8, 8, 256, 8);
        guiGraphics.blit(BOTTOM_BAR, w, guiGraphics.guiHeight() - 8, 0, 0, guiGraphics.guiWidth() - w - 8, 8, 256, 8);
        guiGraphics.blit(RIGHT_BAR, width - 8, 8, 0, 0, 8, guiGraphics.guiHeight() - 8 - 8, 8, 256);

        this.researchGraphWidget.setSize(guiGraphics.guiWidth() - 8 - w, guiGraphics.guiHeight() - 8 * 2);

        guiGraphics.enableScissor(w, 8, guiGraphics.guiWidth() - 8, guiGraphics.guiHeight() - 8);
        {
            this.researchGraphWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.disableScissor();

        this.researchGraphWidget.renderNodeTooltips(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);

            if (renderable instanceof AbstractResearchInfoWidget<?> infoWidget) {
                infoWidget.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        if (prevSelectedResearchMethodWidget != null) {
            this.invalidateSelectedResearch();
            this.prevSelectedResearchMethodWidget = null;
        }

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

    public void invalidateSelectedResearch() {
        this.prevSelectedResearchMethodWidget.visitWidgets(this::removeWidget);
        this.getSelectedResearchWidget().methodWidget.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void onClose() {
        super.onClose();

        // Save graph state on close
        if (this.researchGraphWidget != null) {
            this.researchGraphWidget.onClose();
        }
    }

    public enum InvalidateType {
        ADD,
        REMOVE,
        NONE,
    }

}
