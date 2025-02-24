package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.clown.NodeConnectionRenderer;
import com.portingdeadmods.researchd.registries.Researches;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import it.unimi.dsi.fastutil.Pair;
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
    private ResearchNode selectedNode = null;

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
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        if (graph != null) {
            // First render connections
            renderNodeConnections(graph.rootNode(), guiGraphics);

            // Then render nodes (so they appear on top of connections)
            renderNode(graph.rootNode(), guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.disableScissor();

        // Render tooltips last
        if (graph != null) {
            renderNodeTooltip(graph.rootNode(), guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the research panel itself
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        // Recursively render all children nodes
        for (ResearchNode childNode : node.getChildren()) {
            renderNode(childNode, guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderNodeConnections(ResearchNode node, GuiGraphics guiGraphics) {
        // Draw connections to children first
        List<ResearchNode> children = node.getChildren();
        if (!children.isEmpty()) {
            drawConnectionTree(guiGraphics, node, children);
        }

        // Then recurse for each child
        for (ResearchNode child : children) {
            renderNodeConnections(child, guiGraphics);
        }
    }

    private void drawConnectionTree(GuiGraphics guiGraphics, ResearchNode parent, List<ResearchNode> children) {
        if (children.isEmpty()) return;

        int parentX = parent.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int parentY = parent.getY() + ResearchScreenWidget.PANEL_HEIGHT / 2;

        if (children.size() == 1) {
            ResearchNode child = children.get(0);
            int childX = child.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
            int childY = child.getY() + ResearchScreenWidget.PANEL_HEIGHT / 2;
            drawConnection(guiGraphics, parentX, parentY, childX, childY);
            return;
        }

        List<ResearchNode> sortedChildren = new ArrayList<>(children);
        sortedChildren.sort(Comparator.comparingInt(ResearchNode::getX));

        int leftmostX = sortedChildren.get(0).getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int rightmostX = sortedChildren.get(sortedChildren.size() - 1).getX() + ResearchScreenWidget.PANEL_WIDTH / 2;

        int junctionY = parentY + 20;

        guiGraphics.vLine(parentX, parentY, junctionY, -1);

        guiGraphics.hLine(leftmostX, rightmostX, junctionY, -1);

        for (ResearchNode child : sortedChildren) {
            int childX = child.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
            int childY = child.getY() + ResearchScreenWidget.PANEL_HEIGHT / 2;

            guiGraphics.vLine(childX, junctionY, childY, -1);
        }
    }

    private void drawConnection(GuiGraphics guiGraphics, int parentX, int parentY, int childX, int childY) {
        if (parentX == childX) {
            guiGraphics.vLine(parentX, parentY, childY, -1);
        } else {
            int midY = parentY + (childY - parentY) / 2;

            guiGraphics.vLine(parentX, parentY, midY, -1);

            guiGraphics.hLine(Math.min(parentX, childX), Math.max(parentX, childX), midY, -1);

            guiGraphics.vLine(childX, midY, childY, -1);
        }
    }

    /* Fix for the immutable collection error */
    private void resolveOverlaps() {
        // Process each layer
        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);

            // Create a mutable copy of the nodes
            List<ResearchNode> nodes = new ArrayList<>(layer.flatten());

            // Sort by X position
            nodes.sort(Comparator.comparingInt(ResearchNode::getX));

            // Check for overlaps between adjacent nodes
            for (int j = 0; j < nodes.size() - 1; j++) {
                ResearchNode current = nodes.get(j);
                ResearchNode next = nodes.get(j + 1);

                int minDistance = ResearchScreenWidget.PANEL_WIDTH + 10;
                int actualDistance = next.getX() - (current.getX() + ResearchScreenWidget.PANEL_WIDTH);

                if (actualDistance < minDistance) {
                    int offset = minDistance - actualDistance;

                    // Shift all subsequent nodes
                    for (int k = j + 1; k < nodes.size(); k++) {
                        ResearchNode nodeToShift = nodes.get(k);
                        nodeToShift.setX(nodeToShift.getX() + offset);

                        // Cascade shift to descendants
                        shiftDescendants(nodeToShift, offset);
                    }
                }
            }
        }
    }

    private void shiftDescendants(ResearchNode node, int xOffset) {
        for (ResearchNode child : node.getChildren()) {
            child.setX(child.getX() + xOffset);
            shiftDescendants(child, xOffset);
        }
    }

    private void renderAllNodes(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render this node
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        // Recursively render all children
        for (ResearchNode child : node.getChildren()) {
            renderAllNodes(child, guiGraphics, mouseX, mouseY, partialTick);
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
        // First pass: Set initial positions
        positionNodesInLayers();

        // Second pass: Fix overlaps
        preventOverlaps();

        // Third pass: Center children under parents
        alignChildrenWithParents();
    }

    private void positionNodesInLayers() {
        int ySpacing = ResearchScreenWidget.PANEL_HEIGHT + 20;
        int xSpacing = ResearchScreenWidget.PANEL_WIDTH + 15;

        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);
            int y = getY() + i * ySpacing;

            // Start from left side with some padding
            int x = getX() + 20;

            // Use ArrayList to create a mutable copy we can sort
            ArrayList<Map.Entry<List<ResourceKey<Research>>, Group>> groupEntries =
                    new ArrayList<>(layer.nodes().entrySet());

            // Sort groups by parent count (can help with better initial positioning)
            groupEntries.sort((a, b) -> {
                int parentsA = a.getValue().entries().stream()
                        .mapToInt(node -> node.getParents().size()).sum();
                int parentsB = b.getValue().entries().stream()
                        .mapToInt(node -> node.getParents().size()).sum();
                return Integer.compare(parentsA, parentsB);
            });

            // Position each group
            for (Map.Entry<List<ResourceKey<Research>>, Group> entry : groupEntries) {
                Group group = entry.getValue();
                List<ResearchNode> nodes = group.entries();

                // Position each node in group
                for (int j = 0; j < nodes.size(); j++) {
                    ResearchNode node = nodes.get(j);
                    node.setX(x);
                    node.setY(y);
                    x += xSpacing;
                }

                // Add extra space between groups
                x += 10;
            }
        }
    }

    private void preventOverlaps() {
        // Process each layer
        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);

            // Get all nodes in this layer and make a mutable copy
            List<ResearchNode> nodesInLayer = new ArrayList<>(layer.flatten());

            // Sort by X position for easier overlap detection
            nodesInLayer.sort(Comparator.comparingInt(ResearchNode::getX));

            // Check each node against its next neighbor
            for (int j = 0; j < nodesInLayer.size() - 1; j++) {
                ResearchNode current = nodesInLayer.get(j);
                ResearchNode next = nodesInLayer.get(j + 1);

                int currentRight = current.getX() + ResearchScreenWidget.PANEL_WIDTH;
                int nextLeft = next.getX();

                // Minimum gap between nodes
                int minGap = 10;

                // If nodes overlap or are too close
                if (nextLeft - currentRight < minGap) {
                    // Calculate how much to move the next node
                    int offset = minGap - (nextLeft - currentRight);

                    // Move this node and all nodes to its right
                    for (int k = j + 1; k < nodesInLayer.size(); k++) {
                        ResearchNode nodeToShift = nodesInLayer.get(k);
                        shiftNodeAndDescendants(nodeToShift, offset, 0);
                    }
                }
            }
        }
    }

    private void shiftNodeAndDescendants(ResearchNode node, int xOffset, int yOffset) {
        // Move this node
        node.setX(node.getX() + xOffset);
        if (yOffset != 0) {
            node.setY(node.getY() + yOffset);
        }

        // Recursively move all children
        for (ResearchNode child : node.getChildren()) {
            shiftNodeAndDescendants(child, xOffset, yOffset);
        }
    }

    private void alignChildrenWithParents() {
        // Process from bottom up to handle multi-level alignments correctly
        for (int i = this.layers.size() - 2; i >= 0; i--) {
            Layer parentLayer = this.layers.get(i);

            for (Group group : parentLayer.nodes().values()) {
                for (ResearchNode parent : group) {
                    List<ResearchNode> children = new ArrayList<>(parent.getChildren());

                    if (children.size() > 1) {
                        // Find the bounding box of all children
                        int leftmostChild = Integer.MAX_VALUE;
                        int rightmostChild = Integer.MIN_VALUE;

                        for (ResearchNode child : children) {
                            leftmostChild = Math.min(leftmostChild, child.getX());
                            rightmostChild = Math.max(rightmostChild,
                                    child.getX() + ResearchScreenWidget.PANEL_WIDTH);
                        }

                        // Calculate ideal center position for parent
                        int childrenCenter = (leftmostChild + rightmostChild) / 2;
                        int parentCenter = parent.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
                        int offset = childrenCenter - parentCenter;

                        // Only move if it would make a significant improvement and won't create overlaps
                        if (Math.abs(offset) > 15 && canMoveNode(parent, parent.getX() + offset, i)) {
                            parent.setX(parent.getX() + offset);
                        }
                    }
                }
            }
        }
    }

    private boolean canMoveNode(ResearchNode node, int newX, int layerIndex) {
        // Get all other nodes in this layer
        List<ResearchNode> otherNodes = new ArrayList<>();
        Layer layer = this.layers.get(layerIndex);

        for (Group group : layer.nodes().values()) {
            for (ResearchNode otherNode : group) {
                if (otherNode != node) {
                    otherNodes.add(otherNode);
                }
            }
        }

        // Check if the new position would overlap with any other node
        int nodeRight = newX + ResearchScreenWidget.PANEL_WIDTH;
        for (ResearchNode other : otherNodes) {
            int otherLeft = other.getX();
            int otherRight = otherLeft + ResearchScreenWidget.PANEL_WIDTH;

            // Add a small padding
            if (Math.max(newX, otherLeft) < Math.min(nodeRight, otherRight) + 5) {
                return false;  // Would overlap
            }
        }

        return true;  // No overlaps
    }

    /**
     * @return null if we can place the node, otherwise it will return the node that intersects
     */
    private @Nullable ResearchNode canPlaceNode(int x, int layerIndex) {
        List<Map.Entry<List<ResourceKey<Research>>, Group>> layer = this.layers.get(layerIndex).nodes.entrySet().stream().sorted(Comparator.comparingInt(e0 -> e0.getValue().x)).toList();
        for (int i = 0; i < layer.size(); i++) {
            Map.Entry<List<ResourceKey<Research>>, Group> group = layer.get(i);
            if (x > group.getValue().x) {
                boolean valid = true;
                if (i + 1 < layer.size()) {
                    Map.Entry<List<ResourceKey<Research>>, Group> nextGroup = layer.get(i + 1);
                    valid = x < nextGroup.getValue().x();
                }
                if (valid) {
                    for (ResearchNode node : group.getValue().entries()) {
                        if (doNodesIntersect(x, x + ResearchScreenWidget.PANEL_WIDTH, node.getX(), node.getX() + ResearchScreenWidget.PANEL_WIDTH)) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
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

    private static List<Layer> getChildNodes(ResearchNode parentNode) {
        List<ResearchNode> nodes = new ArrayList<>();
        collectNodes(nodes, parentNode);
        return Layer.calculate(parentNode, new LinkedHashSet<>(nodes)).int2ObjectEntrySet()
                .stream().sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey))
                .map(Map.Entry::getValue).toList();
    }

    private static void collectNodes(List<ResearchNode> nodes, ResearchNode node) {
        nodes.add(node);

        for (ResearchNode childNode : node.getChildren()) {
            collectNodes(nodes, childNode);
        }
    }

    private static @Nullable Pair<ResearchNode, ResearchNode> doNodesIntersect(Group nodes0, Group nodes1) {
        for (ResearchNode node0 : nodes0) {
            for (ResearchNode node1 : nodes1) {
                if (node0 != node1 && doNodesIntersect(node0, node1)) {
                    ResearchNode newNode0;
                    ResearchNode newNode1;
                    if (node0.getX() < node1.getX()) {
                        newNode0 = node1;
                        newNode1 = node0;
                    } else {
                        newNode0 = node0;
                        newNode1 = node1;
                    }
                    return Pair.of(newNode0, newNode1);
                }
            }
        }
        return null;
    }

    private static boolean doNodesIntersect(ResearchNode node0, ResearchNode node1) {
        return doNodesIntersect(node0.getX(), node0.getX() + ResearchScreenWidget.PANEL_WIDTH, node1.getX(), node1.getX() + ResearchScreenWidget.PANEL_WIDTH);
    }

    private static boolean doNodesIntersect(int x1, int x2, int x3, int x4) {
        // Ensure x1 < x2 and x3 < x4 by sorting
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (x3 > x4) {
            int temp = x3;
            x3 = x4;
            x4 = temp;
        }

        // Check if the lines overlap or touch
        return Math.max(x1, x3) <= Math.min(x2, x4);
    }

    private static int getWidestLayerWidth(List<Layer> layers) {
        int widestLayerWidth = 0;
        for (Layer layer : layers) {
            int size = layer.flatten().size();
            int width = size * 30 - 10;
            if (width > widestLayerWidth) {
                widestLayerWidth = width;
            }
        }
        return widestLayerWidth;
    }

    private record Nodes(List<ResearchNode> nodes) {
        public void offsetX(int offsetX) {
            for (ResearchNode node : nodes) {
                node.setX(node.getX() + offsetX);
            }
        }

        public void offsetY(int offsetY) {
            for (ResearchNode node : nodes) {
                node.setX(node.getY() + offsetY);
            }
        }

    }

    private record Layer(Map<List<ResourceKey<Research>>, Group> nodes) {
        public static Int2ObjectMap<Layer> calculate(ResearchNode rootNode, Set<ResearchNode> nodes) {
            Int2ObjectMap<Layer> layers = new Int2ObjectLinkedOpenHashMap<>();
            traverseTree(rootNode, layers, new LinkedHashSet<>(nodes), 0);

            return layers;
        }

        public static Int2ObjectMap<Layer> calculate(ResearchGraph graph) {
            return calculate(graph.rootNode(), graph.nodes());
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

    private static final class Group implements Iterable<ResearchNode> {
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

        @Override
        public @NotNull Iterator<ResearchNode> iterator() {
            return this.entries.iterator();
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if any node was clicked
        if (graph != null) {
            ResearchNode clicked = findClickedNode(graph.rootNode(), mouseX, mouseY);
            if (clicked != null) {
                // Update selection
                selectedNode = clicked;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private ResearchNode findClickedNode(ResearchNode node, double mouseX, double mouseY) {
        // Check if this node was clicked
        if (mouseX >= node.getX() && mouseX < node.getX() + ResearchScreenWidget.PANEL_WIDTH &&
                mouseY >= node.getY() && mouseY < node.getY() + ResearchScreenWidget.PANEL_HEIGHT) {
            return node;
        }

        // Check children
        for (ResearchNode child : node.getChildren()) {
            ResearchNode result = findClickedNode(child, mouseX, mouseY);
            if (result != null) {
                return result;
            }
        }

        return null; // No node clicked
    }

    public void clearSelection() {
        selectedNode = null;
    }

    // Method to set selection
    public void selectNode(ResearchNode node) {
        selectedNode = node;
    }

    private static @NotNull Research getResearch(ResearchNode node) {
        return ResearchHelper.getResearch(node.getInstance().getResearch(), Minecraft.getInstance().level.registryAccess());
    }
}
