package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.common.util.Size2i;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractResearchMethodList<T extends ResearchMethodList> extends AbstractResearchInfoWidget<T> {
    private final List<AbstractResearchInfoWidget<? extends ResearchMethod>> methods;

    public AbstractResearchMethodList(int x, int y, T method) {
        super(x, y, method);
        this.methods = new ArrayList<>();
        List<ResearchMethod> methods = method.methods();
        for (int i = 0; i < methods.size(); i++) {
            ResearchMethod researchMethod = methods.get(i);
            int xSize = i > 0 ? (int) (getSizeFor(i, false).width + i * getPadding()) : 0;
            this.methods.add(ResearchdClient.RESEARCH_METHOD_WIDGETS.get(researchMethod.id()).create(researchMethod, x + xSize, y));
        }
        Size2i size = getSize(true);
        this.setWidth(size.width);
        this.setHeight(size.height);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        for (AbstractResearchInfoWidget<? extends ResearchMethod> widget : methods) {
            consumer.accept(widget);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = getX();
        int y = getY();
        guiGraphics.fill(x - 1, y - 1, x + this.width + 1, y + this.height + 1, FastColor.ARGB32.color(109, 109, 109));

        List<? extends ResearchMethod> methods = method.methods();
        for (int i = 0; i < methods.size(); i++) {
            float padding = getPadding();
            if (i > 0 && i - 1 != methods.size()) {
                int xSize1 = (int) (getSizeFor(i, false).width + (i - 1) * padding);
                int ySize = (this.height - Minecraft.getInstance().font.lineHeight) / 2;
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getSeparatorText(), (int) (x + xSize1 + getPadding() / 2), y + ySize, -1);
            }
        }
    }

//    @Override
//    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
//        List<? extends ResearchMethod> methods = method.methods();
//        for (int i = 0; i < methods.size(); i++) {
//            ResearchMethod researchMethod = methods.get(i);
//            float padding = getPadding();
//            int xSize = (int) (getSizeFor(method, mouseX, mouseY, i, false).width + i * padding);
//            ClientResearchMethod.renderTooltip(guiGraphics, x + xSize, y, mouseX, mouseY, researchMethod);
//        }
//    }

    public abstract @NotNull String getSeparatorText();

    @Override
    public Size2i getSize() {
        return getSizeFor(method.methods().size(), false);
    }

    public float getPadding() {
        Font font = Minecraft.getInstance().font;
        return font.width(getSeparatorText()) + 4;
    }

    public Size2i getSize(boolean includePadding) {
        return getSizeFor(this.method.methods().size(), includePadding);
    }

    public @NotNull Size2i getSizeFor(int amount, boolean includePadding) {
        List<AbstractResearchInfoWidget<? extends ResearchMethod>> methods = this.methods;
        // Required cuz we call this method in the super constructor before this.methods is initialized
        if (methods == null) {
            methods = new ArrayList<>();
            for (ResearchMethod researchMethod : method.methods()) {
                methods.add(ResearchdClient.RESEARCH_METHOD_WIDGETS.get(researchMethod.id()).create(researchMethod, 0, 0));
            }
        }
        Size2i firstMethodSize = methods.getFirst().getSize();
        Size2i size = new Size2i(0, firstMethodSize.height);
        for (int i = 0; i < amount; i++) {
            AbstractResearchInfoWidget<? extends ResearchMethod> researchMethod = methods.get(i);
            size = new Size2i((int) (size.width + researchMethod.getWidth() + (includePadding && i > 0 ? getPadding() : 0)), size.height);
        }
        return size;
    }
}
