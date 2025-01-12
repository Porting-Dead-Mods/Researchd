package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.client.ResearchManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResearchGraph extends AbstractWidget {
    private Set<ResearchNode> rootNodes;
    private Set<ResearchNode> nodes;

    public ResearchGraph(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.nodes = new HashSet<>();
        this.rootNodes = new HashSet<>();
    }

    public ResearchGraph(ResearchManager manager, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.nodes = manager.getNodes();
        this.rootNodes = manager.getRootNodes();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        for (ResearchNode node : this.rootNodes) {
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
