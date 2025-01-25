package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class ResearchGraphWidget extends AbstractWidget {
    private @Nullable ResearchGraph graph;
    private List<Layer> layers;

    public ResearchGraphWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.layers = new ArrayList<>();
    }

    public void setGraph(ResearchGraph graph) {
        this.graph = graph;
        calculateLayers();
        calculateMaxWidth();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
        ResearchNode node = graph.rootNode();
        renderNode(node, guiGraphics, i, i1, v);
        //guiGraphics.vLine(node.getX() + (node.getWidth() / 2), node.getY() + node.getWidth(), node.getNext().stream().findFirst().get().getY(), -1);
        guiGraphics.disableScissor();
    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        if (node.isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.renderComponentTooltip(minecraft.font, List.of(
                    Utils.registryTranslation(node.getInstance().getResearch()),
                    Component.translatable("research_desc." + Researchd.MODID + "." + node.getInstance().getResearch().location().getPath())
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
            node.setXExt((int) (node.getX() + dragX));
            node.setYExt((int) (node.getY() + dragY));
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void calculateMaxWidth() {
        List<Layer> sorted = this.layers.stream().sorted(Comparator.comparingInt(l -> l.nodes.size())).toList();
        Layer layerWithHighestWidth = sorted.getLast();
    }

    private void calculateLayers() {
        this.layers = Layer.calculate(graph).int2ObjectEntrySet()
                .stream().sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey))
                .map(Map.Entry::getValue).toList();
    }

    private record Layer(List<ResearchNode> nodes) {
        public static Int2ObjectMap<Layer> calculate(ResearchGraph graph) {
            Int2ObjectMap<Layer> nodes = new Int2ObjectOpenHashMap<>();
            traverseTree(graph.rootNode(), nodes, new HashSet<>(graph.nodes()), 0);

            return nodes;
        }

        private static void traverseTree(ResearchNode node, Int2ObjectMap<Layer> nodes, Set<ResearchNode> remaining, int nesting) {
            nodes.computeIfAbsent(nesting, key -> new Layer(new ArrayList<>())).nodes().add(node);

            for (ResearchNode nextNode : node.getNext()) {
                if (remaining.contains(nextNode)) {
                    remaining.remove(nextNode);
                    traverseTree(nextNode, nodes, remaining, nesting + 1);
                }
            }
        }
    }
}
