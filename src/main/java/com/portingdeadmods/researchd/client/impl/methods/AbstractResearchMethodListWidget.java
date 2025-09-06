package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodList;
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

public abstract class AbstractResearchMethodListWidget<T extends ResearchMethodList> extends AbstractResearchInfoWidget<T> {
    private final List<AbstractResearchInfoWidget<? extends ResearchMethod>> methods;

    public AbstractResearchMethodListWidget(int x, int y, T method) {
        super(x, y, method);
        this.methods = new ArrayList<>();
        List<ResearchMethod> methods = method.methods();
        for (int i = 0; i < methods.size(); i++) {
            ResearchMethod researchMethod = methods.get(i);
            int xSize = i > 0 ? (int) (getSizeFor(i, false).width + i * getPadding()) : 0;
            this.methods.add(ResearchdClient.RESEARCH_METHOD_WIDGETS.get(researchMethod.id()).createMethod(x + xSize + 1, y + 1, researchMethod));
        }
        Size2i size = getSize(true);
        this.setWidth(size.width);
        this.setHeight(size.height);
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        for (AbstractResearchInfoWidget<? extends ResearchMethod> widget : methods) {
            consumer.accept(widget);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = getX();
        int y = getY();
        Size2i firstSize = getSizeFor(1, false);
        guiGraphics.fill(x, y, x + this.width, y + firstSize.height, FastColor.ARGB32.color(109, 109, 109));

        List<? extends ResearchMethod> methods = value.methods();
        for (int i = 0; i < methods.size(); i++) {
            this.methods.get(i).render(guiGraphics, mouseX, mouseY, partialTicks);
            float padding = getPadding();
            if (i > 0 && i - 1 != methods.size()) {
                int xSize1 = (int) (getSizeFor(i, false).width + (i - 1) * padding);
                int ySize = (this.height - Minecraft.getInstance().font.lineHeight) / 2 + 1;
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getSeparatorText(), (int) (x + xSize1 + getPadding() / 2), y + ySize, -1);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for (AbstractResearchInfoWidget<? extends ResearchMethod> researchMethod : this.methods) {
            researchMethod.renderTooltip(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    public abstract @NotNull String getSeparatorText();

    @Override
    public Size2i getSize() {
        return getSizeFor(value.methods().size(), false);
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        for (AbstractResearchInfoWidget<? extends ResearchMethod> method : this.methods) {
            method.setY(y + 1);
        }

    }

    public float getPadding() {
        Font font = Minecraft.getInstance().font;
        return font.width(getSeparatorText()) + 4;
    }

    public Size2i getSize(boolean includePadding) {
        return getSizeFor(this.value.methods().size(), includePadding);
    }

    public @NotNull Size2i getSizeFor(int amount, boolean includePadding) {
        List<AbstractResearchInfoWidget<? extends ResearchMethod>> methods = this.methods;
        // Required cuz we call this method in the super constructor before this.methods is initialized
        if (methods == null) {
            methods = new ArrayList<>();
            for (ResearchMethod researchMethod : value.methods()) {
                methods.add(ResearchdClient.RESEARCH_METHOD_WIDGETS.get(researchMethod.id()).createMethod(0, 0, researchMethod));
            }
        }
        if (!methods.isEmpty()) {
            Size2i firstMethodSize = methods.getFirst().getSize();
            Size2i size = new Size2i(0, firstMethodSize.height + 2);
            for (int i = 0; i < amount; i++) {
                AbstractResearchInfoWidget<? extends ResearchMethod> researchMethod = methods.get(i);
                size = new Size2i((int) (size.width + researchMethod.getWidth() + (includePadding && i > 0 ? getPadding() : 0)) + 2, size.height);
            }
            return size;
        }
        return new Size2i(0, 0);
    }
}
