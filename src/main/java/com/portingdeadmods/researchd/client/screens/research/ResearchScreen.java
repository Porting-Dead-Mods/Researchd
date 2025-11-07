package com.portingdeadmods.researchd.client.screens.research;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.api.client.TechList;
import com.portingdeadmods.researchd.api.research.ResearchInteractionType;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.editor.widgets.SelectPackPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.widgets.*;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResearchScreen extends Screen {
    public static final ResourceLocation TOP_RIGHT_EDGE = Researchd.rl("textures/gui/research_screen/edges/top_right.png");
    public static final ResourceLocation BOTTOM_RIGHT_EDGE = Researchd.rl("textures/gui/research_screen/edges/bottom_right.png");
    public static final ResourceLocation TOP_BAR = Researchd.rl("textures/gui/research_screen/bars/top.png");
    public static final ResourceLocation BOTTOM_BAR = Researchd.rl("textures/gui/research_screen/bars/bottom.png");
    public static final ResourceLocation RIGHT_BAR = Researchd.rl("textures/gui/research_screen/bars/right.png");
    public static final ResourceLocation EDIT_BUTTON_CORNER = Researchd.rl("textures/gui/research_screen/edit_button_corner.png");

    public static final WidgetSprites EDITOR_BUTTON_SPRITES = new WidgetSprites(Researchd.rl("editor_button"), Researchd.rl("editor_button_highlighted"));

    // Singleton since whole client is a singleton
    public static final Map<ResourceLocation, ClientResearchIcon<?>> CLIENT_ICONS = new HashMap<>();

    private TechListWidget techListWidget;
    private ResearchQueueWidget researchQueueWidget;
    private ResearchGraphWidget researchGraphWidget;
    private SelectedResearchWidget selectedResearchWidget;

    private final List<AbstractWidget> popupWidgets;

    private boolean editorOpen;
    private SelectPackPopupWidget selectPackPopupWidget;

    public ResearchScreen() {
        super(ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_TITLE));
        this.popupWidgets = new ArrayList<>();
    }

    @Override
    protected void init() {
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
        if (CommonResearchCache.rootResearch != null) {
            this.researchGraphWidget.setGraph(ResearchGraphCache.computeIfAbsent(CommonResearchCache.rootResearch.getResearchKey()));
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player.getData(ResearchdAttachments.RESEARCH_INTERACTION_TYPE) == ResearchInteractionType.EDIT) {
            this.addRenderableWidget(PDLImageButton.builder(this::openEditor)
                    .pos(this.width - 16 - 8, this.height - 16 - 8)
                    .size(16, 16)
                    .sprites(EDITOR_BUTTON_SPRITES)
                    .tooltip(Tooltip.create(Component.literal("Editor")))
                    .build());
        }

        this.techListWidget.visitWidgets(this::addRenderableWidget);
        this.researchQueueWidget.visitWidgets(this::addRenderableWidget);
        this.selectedResearchWidget.visitWidgets(this::addRenderableWidget);
        this.researchGraphWidget.visitWidgets(this::addRenderableWidget);

    }

    private void openEditor(PDLImageButton button) {
        if (!this.editorOpen) {
            this.selectPackPopupWidget = this.openPopupCentered(new SelectPackPopupWidget());
        } else {
            this.closePopup(this.selectPackPopupWidget);
        }
        this.editorOpen = !this.editorOpen;
    }

    private <W extends PopupWidget> W openPopupCentered(W widget) {
        int x = (this.width - widget.getWidth()) / 2;
        int y = (this.height - widget.getHeight()) / 2;
        widget.setPosition(x, y);

        return this.openPopup(widget);
    }

    private <W extends PopupWidget> W openPopup(W widget) {
        widget.visitWidgets(this.popupWidgets::add);
        return widget;
    }

    private <W extends PopupWidget> void closePopup(W widget) {
        widget.close();
        widget.visitWidgets(this.popupWidgets::remove);
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

        Minecraft mc = Minecraft.getInstance();

        if (mc.player.getData(ResearchdAttachments.RESEARCH_INTERACTION_TYPE) == ResearchInteractionType.EDIT) {
            guiGraphics.blit(EDIT_BUTTON_CORNER, width - 24 - 4, height - 24 - 4, 0, 0, 24, 24, 24, 24);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 300);
            for (AbstractWidget widget : this.popupWidgets) {
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
        poseStack.popPose();

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

    public TechListWidget getTechListWidget() {
        return techListWidget;
    }

    public TechList getTechList() {
        return this.techListWidget.getTechList();
    }

    public ResearchGraph getResearchGraph() {
        return this.researchGraphWidget.getCurrentGraph();
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
