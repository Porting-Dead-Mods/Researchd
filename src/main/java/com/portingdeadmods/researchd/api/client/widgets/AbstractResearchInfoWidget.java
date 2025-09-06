package com.portingdeadmods.researchd.api.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.common.util.Size2i;

public abstract class AbstractResearchInfoWidget<T> extends AbstractWidget {
    public static final int BACKGROUND_COLOR = FastColor.ARGB32.color(89, 89, 89);
    protected final T value;
    protected final Font font;

    public AbstractResearchInfoWidget(int x, int y, T value) {
        super(x, y, 0, 0, Component.empty());
        this.value = value;
        this.font = Minecraft.getInstance().font;
        this.setWidth(getSize().width);
        this.setHeight(getSize().height);
    }

    public abstract Size2i getSize();

    @Override
    protected abstract void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v);

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

}
