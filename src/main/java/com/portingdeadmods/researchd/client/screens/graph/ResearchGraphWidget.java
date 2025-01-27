package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

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
        setCoordinates();
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

        for (ResearchNode rNode : node.getChildren()) {
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

    private void calculateLayers() {
        this.layers = Layer.calculate(graph).int2ObjectEntrySet()
                .stream().sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey))
                .map(Map.Entry::getValue).toList();
    }

    public void setCoordinates() {
        int startY = getY() + 10;
        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);

            for (Map.Entry<List<ResourceKey<Research>>, List<ResearchNode>> entry : layer.nodes().entrySet()) {
                for (ResearchNode node : entry.getValue()) {
                    // If the node is the root node
                    if (node.isRootNode()) {
                        node.setX(getX() + getWidth() / 2);
                        node.setY(startY);
                        break;
                    }

                    Set<ResearchNode> children = node.getChildren();
                    Set<ResearchNode> parents = node.getParents();

                    // Check if one of the parents is in same layer
                    for (ResearchNode parentNode : parents) {
                        if (layer.flatten().contains(parentNode)) {
                            // If both the parent and the child are in the same layer.
                            // This means that child has more than one parent, otherwise
                            // the child would be on different layer

                            // Parents of the child without the node that is in same line
                            List<ResearchNode> sortedParents = new ArrayList<>(parents.stream()
                                    .sorted(Comparator.comparingInt(ResearchNode::getX))
                                    .toList());
                            sortedParents.remove(parentNode);

                            int firstX = sortedParents.getFirst().getX();
                            Researchd.LOGGER.debug("first: {} x: {}", firstX, sortedParents.getFirst().getInstance().getResearch());
                            int distance = sortedParents.getLast().getX() - firstX;
                            Researchd.LOGGER.debug("distance: {}", distance);
                            node.setX(firstX + distance / 2);
                            node.setY(i * (ResearchScreenWidget.PANEL_HEIGHT + 20));
                            break;
                        }
                    }


                }
            }
        }
    }

    private void setGroupCoordinates(List<ResearchNode> researches, int x, int y) {
        for (int i = 0; i < researches.size(); i++) {
            ResearchNode node = researches.get(i);
            node.setX(x + i * (ResearchScreenWidget.PANEL_WIDTH + 10));
            node.setY(y);
        }
    }

    private record Layer(Map<List<ResourceKey<Research>>, List<ResearchNode>> nodes) {
        public static Int2ObjectMap<Layer> calculate(ResearchGraph graph) {
            Int2ObjectMap<Layer> nodes = new Int2ObjectOpenHashMap<>();
            traverseTree(graph.rootNode(), nodes, new HashSet<>(graph.nodes()), 0);

            return nodes;
        }

        private static void traverseTree(ResearchNode node, Int2ObjectMap<Layer> nodes, Set<ResearchNode> remaining, int nesting) {
            nodes.computeIfAbsent(nesting, key -> new Layer(new LinkedHashMap<>())).nodes()
                    .computeIfAbsent(getResearch(node).parents(), k -> new ArrayList<>())
                    .add(node);

            for (ResearchNode nextNode : node.getChildren()) {
                if (remaining.contains(nextNode)) {
                    remaining.remove(nextNode);
                    traverseTree(nextNode, nodes, remaining, nesting + 1);
                }
            }
        }

        public List<ResearchNode> flatten() {
            return this.nodes.values().stream().flatMap(List::stream).toList();
        }
    }

    private static @NotNull Research getResearch(ResearchNode node) {
        return ResearchHelper.getResearch(node.getInstance().getResearch(), Minecraft.getInstance().level.registryAccess());
    }
}
