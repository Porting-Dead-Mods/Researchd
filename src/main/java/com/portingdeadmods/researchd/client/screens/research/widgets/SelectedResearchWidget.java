package com.portingdeadmods.researchd.client.screens.research.widgets;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.ResearchdCommonConfig;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.WidgetConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SelectedResearchWidget extends ResearchScreenWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/selected_research.png");
    private static final ResourceLocation SMALL_SCROLLER_SPRITE = Researchd.rl("scroller_small");
    // For calculating height
    public static final int LABEL_PADDING_TOP_1 = 2;
    public static final int LABEL_PADDING_BOTTOM_1 = 4;
    public static final int METHOD_WIDGET_PADDING_BOTTOM = 4;
    public static final int LINE_HEIGHT = 1;
    public static final int LABEL_PADDING_TOP_2 = 2;
    public static final int LABEL_PADDING_BOTTOM_2 = 4;
    public static final int BOTTOM_PADDING = 6;
    public static final int VISIBLE_CONENT_HEIGHT = 47;
    public static final int PADDING_Y = 20;
    // ---
    public static final int BACKGROUND_WIDTH = 174;
    public static final int BACKGROUND_HEIGHT = 72;
    public static final int SCROLLER_WIDTH = 4;
    public static final int SCROLLER_HEIGHT = 7;
    private @Nullable ResearchInstance selectedInstance;
    public AbstractWidget methodWidget;
    public AbstractWidget effectWidget;
    private int scrollOffset;
    private final Font font;
    private final ResearchScreen researchScreen;

    public SelectedResearchWidget(ResearchScreen screen, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.font = Minecraft.getInstance().font;
        this.researchScreen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);
        float percentage = (float) this.scrollOffset / (this.getInfoHeight() - 47f);
        guiGraphics.blitSprite(SMALL_SCROLLER_SPRITE, getX() + getWidth() - 9, (int) (getY() + PADDING_Y + (41 * percentage)), SCROLLER_WIDTH, SCROLLER_HEIGHT);

        int offsetY = -(this.scrollOffset);

        if (this.selectedInstance != null) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            int padding = 3;

            guiGraphics.drawString(font, this.selectedInstance.getDisplayName(mc.level), 11, 49, -1);
            renderResearchPanel(guiGraphics, this.selectedInstance, 12, 60, mouseX, mouseY, 2, false);

            int startY = 60;

            guiGraphics.enableScissor(53, startY, 53 + 108, startY + 47);
            {
                int yPosMethodLabel = startY + LABEL_PADDING_TOP_1;
                guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_LABEL_RESEARCH_COST), 53 + padding, offsetY + yPosMethodLabel, -1);

                this.methodWidget.render(guiGraphics, mouseX, mouseY, v);

                int yPosLine = startY + LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1 + this.methodWidget.getHeight() + METHOD_WIDGET_PADDING_BOTTOM;
                guiGraphics.fill(53, offsetY + yPosLine, 53 + 78, offsetY + yPosLine + LINE_HEIGHT, -1);

                int yPosEffectsLabel = yPosLine + LINE_HEIGHT + LABEL_PADDING_TOP_2;
                guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.SCREEN_LABEL_RESEARCH_EFFECTS), 56, offsetY + yPosEffectsLabel, -1);

                this.effectWidget.render(guiGraphics, mouseX, mouseY, v);
                //guiGraphics.fill(53, startY + offsetY, 53 + 108, startY + offsetY + this.getInfoHeight(), FastColor.ARGB32.color(100, 0, 0, 155));
            }
            guiGraphics.disableScissor();

            this.renderTooltip(guiGraphics, mouseX, mouseY, v);

        }
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        if (this.methodWidget instanceof AbstractResearchInfoWidget<?> infoWidget) {
            infoWidget.renderTooltip(guiGraphics, mouseX, mouseY, v);
        }

        if (this.effectWidget instanceof AbstractResearchInfoWidget<?> infoWidget) {
            infoWidget.renderTooltip(guiGraphics, mouseX, mouseY, v);
        }
    }

    public int getInfoHeight() {
        int methodHeight = this.methodWidget != null ? this.methodWidget.getHeight() : 0;
        int effectHeight = this.effectWidget != null ? this.effectWidget.getHeight() : 0;
        int methodSectionHeight = LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1 + methodHeight + METHOD_WIDGET_PADDING_BOTTOM;
        int effectSectionHeight = LABEL_PADDING_TOP_2 + font.lineHeight + LABEL_PADDING_BOTTOM_2 + effectHeight;
        return methodSectionHeight + LINE_HEIGHT + effectSectionHeight + BOTTOM_PADDING;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.selectedInstance != null) {
            double rawScrollOffset = Math.max(this.scrollOffset - scrollY * 5, 0);
            if (rawScrollOffset > this.getInfoHeight() - VISIBLE_CONENT_HEIGHT) {
                this.scrollOffset = (this.getInfoHeight() - VISIBLE_CONENT_HEIGHT);
            } else {
                this.scrollOffset = (int) rawScrollOffset;
            }
            this.updateChildWidgetPositions();
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return false;
    }

    /**
     * Recalculate Y positions of method/effect widgets based on current scroll offset.
     */
    private void updateChildWidgetPositions() {
        if (this.methodWidget == null || this.effectWidget == null) return;
        float offsetY = this.scrollOffset;
        this.methodWidget.setY((int) (60 + LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1 - offsetY));
        this.effectWidget.setY((int) (60 + LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1 + methodWidget.getHeight() + METHOD_WIDGET_PADDING_BOTTOM + LINE_HEIGHT + LABEL_PADDING_TOP_2 + font.lineHeight + LABEL_PADDING_BOTTOM_2 - offsetY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.effectWidget != null && this.effectWidget.isHovered()) {
            return this.effectWidget.mouseClicked(mouseX, mouseY, button);
        }

        if (this.methodWidget != null && this.methodWidget.isHovered()) {
            return this.methodWidget.mouseClicked(mouseX, mouseY, button);
        }

        int scrollerX = getX() + getWidth() - 9;
        int trackTop = getY() + PADDING_Y;
        int trackRange = 41;

        if (
                mouseX >= scrollerX &&
                mouseX < scrollerX + SCROLLER_WIDTH &&
                mouseY >= trackTop &&
                mouseY <= trackTop + trackRange + SCROLLER_HEIGHT &&
                getInfoHeight() > VISIBLE_CONENT_HEIGHT
        ) {
            double clamped = Math.clamp(mouseY, trackTop, trackTop + trackRange) - trackTop;
            double percentage = clamped / (double) trackRange;
            int scrollableHeight = getInfoHeight() - VISIBLE_CONENT_HEIGHT;
            this.scrollOffset = (int) (scrollableHeight * percentage);
            this.updateChildWidgetPositions();
            return true;
        }

        return false;
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.mouseClicked(mouseX, mouseY, 0);
    }

    public void setSelectedResearch(@NotNull ResearchInstance instance) {
        if (this.selectedInstance != instance) {
            this.selectedInstance = instance;

            this.researchScreen.getTechListWidget().startResearchButton.active = this.selectedInstance.isResearchable()
                    && !this.researchScreen.getResearchQueueWidget().getQueue().contains(this.selectedInstance.getKey())
                    && !(this.researchScreen.getResearchQueueWidget().getQueue().size() >= ResearchdCommonConfig.researchQueueLength);
            if (this.selectedInstance.isResearchable() && !this.researchScreen.getResearchQueueWidget().getQueue().isEmpty()) {
                this.researchScreen.getTechListWidget().setResearchButtonMode(TechListWidget.ResearchButtonMode.ENQUEUE);
            } else {
                this.researchScreen.getTechListWidget().setResearchButtonMode(TechListWidget.ResearchButtonMode.START);
            }

            // only call after first setting selected research

            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            int padding = 3;

            this.scrollOffset = 0;
            Research research = this.selectedInstance.lookup(mc.level);
            ResearchMethod method = research.researchMethod();
            WidgetConstructor<? extends ResearchMethod> methodWidgetConstructor = ResearchdClient.RESEARCH_METHOD_WIDGETS.get(method.id());
            if (methodWidgetConstructor != null) {
                this.methodWidget = methodWidgetConstructor.createMethod(53 + padding, 60 + LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1, method);
            } else {
                // in case the dev didn't implement a widget for the research method, we scream at them
                MutableComponent message = Component.literal("!!%s does not have info widget!!".formatted(method.id().toString())).withStyle(ChatFormatting.RED);
                this.methodWidget = new MultiLineTextWidget(53 + padding + 1, 60 + LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1, message, font);
                ((MultiLineTextWidget) this.methodWidget).setMaxWidth(108);
            }

            ResearchEffect effect = research.researchEffect();
            WidgetConstructor<? extends ResearchEffect> effectWidgetConstructor = ResearchdClient.RESEARCH_EFFECT_WIDGETS.get(effect.id());
            if (effectWidgetConstructor != null) {
                this.effectWidget = effectWidgetConstructor.createEffect(53 + padding, 60 + LABEL_PADDING_TOP_1 + font.lineHeight + LABEL_PADDING_BOTTOM_1 + methodWidget.getHeight() + METHOD_WIDGET_PADDING_BOTTOM + LINE_HEIGHT + LABEL_PADDING_TOP_2 + font.lineHeight + LABEL_PADDING_BOTTOM_2, effect);
            } else {
                // in case the dev didn't implement a widget for the research method, we *aggressively* scream at them
                MutableComponent message = Component.literal("!!%s does not have info widget!!".formatted(effect.id().toString())).withStyle(ChatFormatting.RED);
                this.effectWidget = new MultiLineTextWidget(53 + padding + 1, 64 + 36 + font.lineHeight + 4, message, font);
                ((MultiLineTextWidget) this.effectWidget).setMaxWidth(108);
            }

        }
    }

    public @Nullable ResearchInstance getSelectedInstance() {
        return selectedInstance;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        //this.methodWidget.visitWidgets(consumer);
        //this.effectWidget.visitWidgets(consumer);
    }
}
