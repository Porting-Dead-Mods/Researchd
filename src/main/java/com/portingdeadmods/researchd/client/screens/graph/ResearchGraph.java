package com.portingdeadmods.researchd.client.screens.graph;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResearchGraph extends AbstractWidget {
    private @Nullable ResearchNode node;
    private final List<ResearchNode> nodes;

    public ResearchGraph(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.nodes = new ArrayList<>();
    }

    public void setNode(@Nullable ResearchNode node) {
        this.node = node;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (node != null) {
            renderNode(node, guiGraphics, i, i1, v);
        }
    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY,float partialTick) {
        node.render(guiGraphics, mouseX, mouseY, partialTick);
        for (ResearchNode rNode : node.getNext()) {
            renderNode(rNode, guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

}
