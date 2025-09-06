package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectList;
import com.portingdeadmods.researchd.utils.WidgetConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.common.util.Size2i;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractResearchEffectListWidget<T extends ResearchEffectList> extends AbstractResearchInfoWidget<T> {
    private final List<AbstractResearchInfoWidget<? extends ResearchEffect>> effects;

    public AbstractResearchEffectListWidget(int x, int y, T effect) {
        super(x, y, effect);
        this.effects = new ArrayList<>();
        List<ResearchEffect> effects = effect.effects();
        for (int i = 0; i < effects.size(); i++) {
            ResearchEffect researchEffect = effects.get(i);
            int xSize = i > 0 ? (int) (getSizeFor(i, false).width + i * getPadding()) : 0;
            WidgetConstructor<? extends ResearchEffect> widgetConstructor = ResearchdClient.RESEARCH_EFFECT_WIDGETS.get(researchEffect.id());
            if (widgetConstructor != null) {
                this.effects.add(widgetConstructor.createEffect(x + xSize + 1, y + 1, researchEffect));
            }
        }
        Size2i size = getSize(true);
        this.setWidth(size.width);
        this.setHeight(size.height);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        for (AbstractResearchInfoWidget<? extends ResearchEffect> widget : this.effects) {
            consumer.accept(widget);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = getX();
        int y = getY();
        Size2i firstSize = getSizeFor(1, false);
        guiGraphics.fill(x, y, x + this.width, y + firstSize.height, FastColor.ARGB32.color(92, 92, 92));

        List<? extends ResearchEffect> effects = value.effects();
        for (int i = 0; i < effects.size(); i++) {
            if (i < this.effects.size()) {
                this.effects.get(i).render(guiGraphics, mouseX, mouseY, partialTicks);
            }
            float padding = getPadding();
            if (i > 0 && i - 1 != effects.size()) {
                int xSize1 = (int) (getSizeFor(i, false).width + (i - 1) * padding);
                int ySize = (this.height - Minecraft.getInstance().font.lineHeight) / 2;
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getSeparatorText(), (int) (x + xSize1 + getPadding() / 2), y + ySize + 1, -1);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for (AbstractResearchInfoWidget<? extends ResearchEffect> researchEffect : this.effects) {
            researchEffect.renderTooltip(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    public abstract @NotNull String getSeparatorText();

    @Override
    public Size2i getSize() {
        return getSizeFor(this.value.effects().size(), false);
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        for (AbstractResearchInfoWidget<? extends ResearchEffect> effect : this.effects) {
            effect.setY(y + 1);
        }

    }

    public float getPadding() {
        Font font = Minecraft.getInstance().font;
        return font.width(getSeparatorText()) + 4;
    }

    public Size2i getSize(boolean includePadding) {
        return getSizeFor(this.value.effects().size(), includePadding);
    }

    public @NotNull Size2i getSizeFor(int amount, boolean includePadding) {
        List<AbstractResearchInfoWidget<? extends ResearchEffect>> methods = this.effects;
        // Required cuz we call this method in the super constructor before this.methods is initialized
        if (methods == null) {
            methods = new ArrayList<>();
            for (ResearchEffect researchEffect : value.effects()) {
                WidgetConstructor<? extends ResearchEffect> widgetConstructor = ResearchdClient.RESEARCH_EFFECT_WIDGETS.get(researchEffect.id());
                if (widgetConstructor != null) {
                    methods.add(widgetConstructor.createEffect(0, 0, researchEffect));
                }
            }
        }
        if (!methods.isEmpty()) {
            AbstractResearchInfoWidget<? extends ResearchEffect> first = methods.getFirst();
            Size2i firstMethodSize = first.getSize();
            Size2i size = new Size2i(0, firstMethodSize.height + 2);
            for (int i = 0; i < amount; i++) {
                AbstractResearchInfoWidget<? extends ResearchEffect> researchMethod = methods.get(i);
                size = new Size2i((int) (size.width + researchMethod.getWidth() + (includePadding && i > 0 ? getPadding() : 0)) + 2, size.height);
            }
            return size;
        }
        return new Size2i(0, 0);
    }
}

