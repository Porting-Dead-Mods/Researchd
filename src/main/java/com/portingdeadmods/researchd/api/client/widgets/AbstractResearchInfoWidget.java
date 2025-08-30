package com.portingdeadmods.researchd.api.client.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.util.Size2i;

public abstract class AbstractResearchInfoWidget<T> extends AbstractWidget {
    protected final T method;

    public AbstractResearchInfoWidget(int x, int y, T method) {
        super(x, y, 0, 0, Component.empty());
        this.method = method;
        this.setWidth(getSize().width);
        this.setHeight(getSize().height);
    }

    public abstract Size2i getSize();

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

}
