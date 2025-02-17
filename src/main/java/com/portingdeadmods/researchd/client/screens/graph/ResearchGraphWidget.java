package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.registries.Researches;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
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

    public ResearchGraph getCurrentGraph() {
        return this.graph;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
        ResearchNode node = graph.rootNode();
        renderNode(node, guiGraphics, i, i1, v);
        //guiGraphics.vLine(node.getX() + (node.getWidth() / 2), node.getY() + node.getWidth(), node.getNext().stream().findFirst().get().getY(), -1);
        guiGraphics.disableScissor();
        renderNodeTooltip(node, guiGraphics, i, i1, v);
    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        for (ResearchNode rNode : node.getChildren()) {
            renderNode(rNode, guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderNodeTooltip(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (node.isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.renderComponentTooltip(minecraft.font, List.of(
                    Utils.registryTranslation(node.getInstance().getResearch()),
                    Component.translatable("research_desc." + Researchd.MODID + "." + node.getInstance().getResearch().location().getPath())
            ), mouseX, mouseY);
        }

        for (ResearchNode rNode : node.getChildren()) {
            renderNodeTooltip(rNode, guiGraphics, mouseX, mouseY, partialTick);
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
        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);

            int x = getX();
            for (Map.Entry<List<ResourceKey<Research>>, Group> entry : layer.nodes().entrySet()) {
                x += setGroupCoordinates(entry.getValue().entries(), x, getY() + i * (ResearchScreenWidget.PANEL_HEIGHT + 10));
                x += 20;
            }
        }

        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);
            for (Map.Entry<List<ResourceKey<Research>>, Group> entry : layer.nodes().entrySet()) {
                centerGroupUnderGroup(entry.getValue(), entry.getValue().entries().getFirst().getParents().stream()
                        .sorted(Comparator.comparingInt(ResearchNode::getX)).toList(), getY() + i * (ResearchScreenWidget.PANEL_HEIGHT + 10));
            }
        }

    }

    private void centerGroupUnderGroup(Group toCenter, List<ResearchNode> target, int y) {
        if (target.isEmpty()) return;

        int targetWidth = target.size() * (ResearchScreenWidget.PANEL_WIDTH + 10) - 10;
        List<ResearchNode> entries = toCenter.entries();
        int toCenterWidth = entries.size() * (ResearchScreenWidget.PANEL_WIDTH + 10) - 10;

        int x = target.stream().min(Comparator.comparingInt(ResearchNode::getX)).get().getX();
        int startX = x + (targetWidth / 2) - (toCenterWidth / 2);

        for (int i = 0; i < entries.size(); i++) {
            ResearchNode node = entries.get(i);
            node.setX(startX + i * (ResearchScreenWidget.PANEL_WIDTH + 10));
            node.setY(y);
        }
    }

    private int setGroupCoordinates(List<ResearchNode> researches, int x, int y) {
        int i;
        for (i = 0; i < researches.size(); i++) {
            ResearchNode node = researches.get(i);
            node.setX(x + i * (ResearchScreenWidget.PANEL_WIDTH + 10));
            node.setY(y);
        }
        return i * (ResearchScreenWidget.PANEL_WIDTH + 10);
    }

    private List<ResearchNode> getChildNodes(ResearchNode parentNode) {
        List<ResearchNode> nodes = new ArrayList<>();
        for (ResearchNode childNode : parentNode.getChildren()) {
            nodes.add(childNode);
        }
        return nodes;
    }

    private record Layer(Map<List<ResourceKey<Research>>, Group> nodes) {
        public static Int2ObjectMap<Layer> calculate(ResearchGraph graph) {
            Int2ObjectMap<Layer> nodes = new Int2ObjectLinkedOpenHashMap<>();
            traverseTree(graph.rootNode(), nodes, new LinkedHashSet<>(graph.nodes()), 0);

            return nodes;
        }

        private static void traverseTree(ResearchNode node, Int2ObjectMap<Layer> nodes, Set<ResearchNode> remaining, int nesting) {
            nodes.computeIfAbsent(nesting, key -> new Layer(new LinkedHashMap<>())).nodes()
                    .computeIfAbsent(getResearch(node).parents(), k -> new Group())
                    .add(node);

            for (ResearchNode nextNode : node.getChildren()) {
                if (remaining.contains(nextNode)) {
                    remaining.remove(nextNode);
                    traverseTree(nextNode, nodes, remaining, nesting + 1);
                }
            }
        }

        public List<ResearchNode> flatten() {
            return this.nodes.values().stream().flatMap(e -> e.entries.stream()).toList();
        }
    }

    private static final class Group {
        private final List<ResearchNode> entries;
        private int x;
        private int y;

        private Group(List<ResearchNode> entries, int x, int y) {
            this.entries = entries;
            this.x = x;
            this.y = y;
        }

        public Group() {
            this(new ArrayList<>(), 0, 0);
        }

        public void add(ResearchNode node) {
            this.entries.add(node);
            this.x = this.entries.getFirst().getX();
            this.y = this.entries.getFirst().getY();
        }

        public List<ResearchNode> entries() {
            return entries;
        }

        public void setX(int x) {
            this.x = x;
            List<ResearchNode> researchNodes = this.entries;
            for (int i = 0; i < researchNodes.size(); i++) {
                ResearchNode node = researchNodes.get(i);
                node.setX(x + i * (ResearchScreenWidget.PANEL_WIDTH + 10));
            }
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int width() {
            return this.entries.size() * (ResearchScreenWidget.PANEL_WIDTH + 10) - 10;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Group) obj;
            return Objects.equals(this.entries, that.entries) &&
                    this.x == that.x &&
                    this.y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(entries, x, y);
        }

        @Override
        public String toString() {
            return "Group[" +
                    "entries=" + entries + ", " +
                    "x=" + x + ", " +
                    "y=" + y + ']';
        }

    }

    private static @NotNull Research getResearch(ResearchNode node) {
        return ResearchHelper.getResearch(node.getInstance().getResearch(), Minecraft.getInstance().level.registryAccess());
    }
}
