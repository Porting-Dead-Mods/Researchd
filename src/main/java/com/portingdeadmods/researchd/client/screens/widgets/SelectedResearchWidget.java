package com.portingdeadmods.researchd.client.screens.widgets;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.WidgetConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SelectedResearchWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/selected_research.png");
    private static final ResourceLocation SMALL_SCROLLER_SPRITE = Researchd.rl("scroller_small");
    public static final int BACKGROUND_WIDTH = 174;
    public static final int BACKGROUND_HEIGHT = 72;
    private ResearchInstance selectedInstance;
    public AbstractWidget methodWidget;
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

            guiGraphics.drawString(font, Utils.registryTranslation(this.selectedInstance.getKey()), 11, 49, -1);
            renderResearchPanel(guiGraphics, this.selectedInstance, 12, 60, mouseX, mouseY, 2, false);

            guiGraphics.enableScissor(53, 60, 53 + 115, 55 + 52);

            guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_LABEL_RESEARCH_COST), 53 + padding, offsetY + 62, -1);

            int yPos = 76 + 4 + this.methodWidget.getHeight() + offsetY;
            guiGraphics.fill(53, yPos, 53 + 78, yPos + 1, -1);

            guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_LABEL_RESEARCH_EFFECTS), 56, offsetY + 57 + height + 28, -1);

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
        if (this.selectedInstance != instance) {
            this.selectedInstance = instance;

            // only call after first setting selected research

            Font font = Minecraft.getInstance().font;
            int padding = 3;

            this.scrollOffset = 0;
            RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
            ResearchMethod method = this.selectedInstance.lookup(registryAccess).researchMethod();
            WidgetConstructor<? extends ResearchMethod> widgetConstructor = ResearchdClient.RESEARCH_METHOD_WIDGETS.get(method.id());
            if (widgetConstructor != null) {
                this.methodWidget = widgetConstructor.create(method, 53 + padding, 62 + font.lineHeight + 4);
            } else {
                // in case the dev didn't implement a widget for the research method, we scream at them
                MutableComponent message = Component.literal("!!%s does not have info widget!!".formatted(method.id().toString())).withStyle(ChatFormatting.RED);
                this.methodWidget = new StringWidget(53 + padding + 1, 62 + font.lineHeight + 4, font.width(message), font.lineHeight + 2, message, font);
            }
        }
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
