package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResearchGraphWidget extends AbstractWidget {
    private @Nullable ResearchGraph graph;

    public ResearchGraphWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setGraph(ResearchGraph graph) {
        this.graph = graph;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
        renderNode(graph.rootNode(), guiGraphics, i, i1, v);
        guiGraphics.disableScissor();
    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        if (node.isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.renderComponentTooltip(minecraft.font, List.of(
                    Utils.registryTranslation(node.getResearch().getResearch()),
                    Component.translatable("research_desc." + Researchd.MODID + "." + node.getResearch().getResearch().location().getPath())
            ), mouseX, mouseY);
        }

        for (ResearchNode rNode : node.getNext()) {
            renderNode(rNode, guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (ResearchNode node : this.graph.nodes()) {
            node.setX((int) (node.getX() + dragX));
            node.setY((int) (node.getY() + dragY));
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}
