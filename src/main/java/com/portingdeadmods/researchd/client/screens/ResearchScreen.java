package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.api.client.TechList;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.widgets.ResearchGraphWidget;
import com.portingdeadmods.researchd.client.screens.widgets.ResearchQueueWidget;
import com.portingdeadmods.researchd.client.screens.widgets.SelectedResearchWidget;
import com.portingdeadmods.researchd.client.screens.widgets.TechListWidget;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ResearchScreen extends Screen {
    public static final ResourceLocation TOP_BAR_TEXTURE = Researchd.rl("textures/gui/top_bar.png");
    public static final ResourceLocation SIDE_BAR_RIGHT_TEXTURE = Researchd.rl("textures/gui/side_bar_right.png");
    public static final int TOP_BAR_WIDTH = 377;
    public static final int TOP_BAR_HEIGHT = 8;
    public static final int SIDE_BAR_WIDTH = 8;
    public static final int SIDE_BAR_HEIGHT = 253;
    public static final int LEFT_MARGIN_WIDTH = 174;
    public static final ResourceLocation TOP_RIGHT_EDGE = Researchd.rl("textures/gui/research_screen/edges/top_right.png");
    public static final ResourceLocation BOTTOM_RIGHT_EDGE = Researchd.rl("textures/gui/research_screen/edges/bottom_right.png");
    public static final ResourceLocation TOP_BAR = Researchd.rl("textures/gui/research_screen/bars/top.png");
    public static final ResourceLocation BOTTOM_BAR = Researchd.rl("textures/gui/research_screen/bars/bottom.png");
    public static final ResourceLocation RIGHT_BAR = Researchd.rl("textures/gui/research_screen/bars/right.png");

    // Singleton since whole client is a singleton
    public static final Map<ResourceLocation, ClientResearchIcon<?>> CLIENT_ICONS = new HashMap<>();

    private final TechListWidget techListWidget;
    private final ResearchQueueWidget researchQueueWidget;
    private final ResearchGraphWidget researchGraphWidget;
    private final SelectedResearchWidget selectedResearchWidget;

    public ResearchScreen() {
        super(ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_TITLE));

        // TECH LIST
        this.techListWidget = new TechListWidget(this, 0, 109, 7);
        this.techListWidget.setTechList(TechList.getClientTechList());

        // QUEUE
        this.researchQueueWidget = new ResearchQueueWidget(this, 0, 0);

        // THIS NEEDS TO BE BEFORE THE GRAPH
        this.selectedResearchWidget = new SelectedResearchWidget(this, 0, 40, SelectedResearchWidget.BACKGROUND_WIDTH, SelectedResearchWidget.BACKGROUND_HEIGHT);
        if (!this.techListWidget.getTechList().entries().isEmpty()) {
            this.selectedResearchWidget.setSelectedResearch(this.techListWidget.getTechList().entries().getFirst());
        }

        // GRAPH
        int x = 174;
        this.researchGraphWidget = new ResearchGraphWidget(this, x, 8, 300, 253 - 16);
        if (CommonResearchCache.ROOT_RESEARCH != null) {
            this.researchGraphWidget.setGraph(ResearchGraphCache.computeIfAbsent(CommonResearchCache.ROOT_RESEARCH.getResearchKey()));
        }
    }

    @Override
    protected void init() {
        super.init();

        this.techListWidget.visitWidgets(this::addRenderableWidget);
        addRenderableWidget(this.researchQueueWidget);
        addRenderableOnly(this.selectedResearchWidget);
        this.selectedResearchWidget.visitWidgets(this::addWidget);
        addRenderableWidget(this.researchGraphWidget);
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
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int w = 174;
        this.researchGraphWidget.setSize(guiGraphics.guiWidth() - 8 - w, guiGraphics.guiHeight() - 8 * 2);
        this.researchGraphWidget.renderNodeTooltips(guiGraphics, mouseX, mouseY, partialTick);

        this.selectedResearchWidget.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
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
