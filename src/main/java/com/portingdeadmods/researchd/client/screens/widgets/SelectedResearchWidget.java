package com.portingdeadmods.researchd.client.screens.widgets;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SelectedResearchWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/selected_research.png");
    private static final ResourceLocation SMALL_SCROLLER_SPRITE = Researchd.rl("scroller_small");
    public static final int BACKGROUND_WIDTH = 174;
    public static final int BACKGROUND_HEIGHT = 72;
    private ResearchInstance selectedInstance;
    private ResearchMethod method;
    private AbstractResearchInfoWidget<? extends ResearchMethod> methodWidget;
    private int scrollOffset;

    public SelectedResearchWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);
        guiGraphics.blitSprite(SMALL_SCROLLER_SPRITE, getX() + getWidth() - 9, getY() + 20, 4, 7);

        int offsetY = (int) -(this.scrollOffset * 1.5f);

        if (this.selectedInstance != null) {
            Font font = Minecraft.getInstance().font;
            int padding = 3;

            guiGraphics.drawString(font, Utils.registryTranslation(this.selectedInstance.getResearch()), 11, 49, -1);
            renderResearchPanel(guiGraphics, this.selectedInstance, 12, 60, mouseX, mouseY, 2, false);

            guiGraphics.enableScissor(53, 60, 53 + 115, 55 + 52);

            guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_LABEL_RESEARCHED_BY), 53 + padding, offsetY + 62, -1);

            int yPos = 76 + 4 + this.methodWidget.getHeight() + offsetY;
            guiGraphics.fill(53, yPos, 53 + 78, yPos + 1, -1);

            guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_LABEL_EFFECTS), 56, offsetY + 57 + height + 28, -1);

            guiGraphics.disableScissor();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset = (int) Math.max(Math.max(this.scrollOffset - scrollY * 2, 0), 0);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public void setSelectedResearch(ResearchInstance instance) {
        this.selectedInstance = instance;

        Font font = Minecraft.getInstance().font;
        int padding = 3;

        this.scrollOffset = 0;
        this.method = ResearchHelperCommon.getResearch(this.selectedInstance.getResearch(), Minecraft.getInstance().level.registryAccess()).researchMethod();
        this.methodWidget = ResearchdClient.RESEARCH_METHOD_WIDGETS.get(this.method.id()).create(this.method, 53 + padding, 62 + font.lineHeight);
    }

    public ResearchInstance getSelectedInstance() {
        return selectedInstance;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        this.methodWidget.visitWidgets(consumer);
    }
}
