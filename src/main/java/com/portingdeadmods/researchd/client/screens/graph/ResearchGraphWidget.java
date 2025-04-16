package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.SelectedResearchWidget;
import com.portingdeadmods.researchd.client.screens.lines.PotentialOverlap;
import com.portingdeadmods.researchd.client.screens.lines.ResearchHead;
import com.portingdeadmods.researchd.client.screens.lines.ResearchLine;
import com.portingdeadmods.researchd.utils.researches.ResearchGraphCache;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import com.portingdeadmods.researchd.utils.researches.layout.ResearchLayoutManager;
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

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.portingdeadmods.researchd.client.screens.ResearchScreenWidget.PANEL_HEIGHT;
import static com.portingdeadmods.researchd.client.screens.ResearchScreenWidget.PANEL_WIDTH;

public class ResearchGraphWidget extends AbstractWidget {
    private @Nullable ResearchGraph graph;
    private List<Layer> layers;
    private Map<ResearchNode, ArrayList<ResearchLine>> researchLines = new HashMap<>();

    private final SelectedResearchWidget selectedResearchWidget;

    public ResearchGraphWidget(SelectedResearchWidget selectedResearchWidget, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.layers = new ArrayList<>();
        this.selectedResearchWidget = selectedResearchWidget;
    }

    /**
     * Set the graph to be displayed, applying layout if needed and calculating connection lines
     */
    public void setGraph(ResearchGraph graph) {
        this.graph = graph;
        this.researchLines.clear();

        if (graph == null || graph.nodes().isEmpty()) {
            return;
        }

        // Try to restore the complete layout for this exact graph view
        boolean layoutRestored = GraphStateManager.getInstance().tryRestoreLastSessionState(graph);

        if (!layoutRestored) {
            // If we don't have a cached layout for this exact view,
            // apply our layout manager to position all nodes
            ResearchLayoutManager.applyLayout(graph, getX() + 10, getY() + 10);
        }

        for (ResearchNode node : graph.nodes()) {
            node.refreshHeads(graph.nodes());
        }

        // Always calculate connection lines after positions are finalized
        calculateLines();
    }

    private void calculateLines() {
        this.researchLines = new HashMap<>();

        // Proceed only if we have nodes to connect
        if (graph == null || graph.nodes().isEmpty()) return;

        // Process nodes layer by layer, bottom to top, then left to right
        List<ResearchNode> sortedNodes = new ArrayList<>(graph.nodes());
        sortedNodes.sort(Comparator
                .comparingInt(ResearchNode::getY)  // First by Y (layer)
                .reversed()                        // Reverse to start from bottom
                .thenComparingInt(ResearchNode::getX)); // Then by X position

        // Track all generated lines
        List<ResearchLine> allLines = new ArrayList<>();

        // Keep track of established vertical paths (X coordinates where vertical line segments exist)
        Set<Integer> verticalPathXCoords = new HashSet<>();

        // Track which heads are already used to prevent duplicates
        Set<ResearchHead> usedOutputHeads = new HashSet<>();
        Set<ResearchHead> usedInputHeads = new HashSet<>();

        // Process each node
        for (ResearchNode parent : sortedNodes) {
            ArrayList<ResearchLine> nodeLines = new ArrayList<>();

            // Group children by layer for consistent path styles
            Map<Integer, List<ResearchNode>> childrenByLayer = new HashMap<>();
            for (ResearchNode child : parent.getChildren()) {
                childrenByLayer
                        .computeIfAbsent(child.getY(), k -> new ArrayList<>())
                        .add(child);
            }

            // Process each layer of children
            for (Map.Entry<Integer, List<ResearchNode>> entry : childrenByLayer.entrySet()) {
                List<ResearchNode> layerChildren = entry.getValue();

                // Sort children left to right
                layerChildren.sort(Comparator.comparingInt(ResearchNode::getX));

                // Choose consistent path style for this parent-layer combination
                boolean preferVerticalFirst =
                        (parent.getX() + PANEL_WIDTH/2) <
                                (layerChildren.stream()
                                        .mapToInt(n -> n.getX() + PANEL_WIDTH/2)
                                        .average()
                                        .orElse(0));

                // Get all available output heads for this parent
                List<ResearchHead> availableOutputHeads = parent.getOutputs().stream()
                        .filter(head -> !usedOutputHeads.contains(head))
                        .sorted(Comparator.comparingInt(ResearchHead::getX))
                        .collect(Collectors.toList());

                // Process each child and assign heads in positional order
                for (int i = 0; i < layerChildren.size(); i++) {
                    ResearchNode child = layerChildren.get(i);

                    // Get available input heads for this child
                    List<ResearchHead> availableInputHeads = child.getInputs().stream()
                            .filter(head -> !usedInputHeads.contains(head))
                            .sorted(Comparator.comparingInt(ResearchHead::getX))
                            .collect(Collectors.toList());

                    // Find the best head pair based on position
                    Pair<ResearchHead, ResearchHead> bestHeads = findBestHeadPairByPosition(
                            parent, child, availableOutputHeads, availableInputHeads,
                            i, layerChildren.size(), usedOutputHeads, usedInputHeads
                    );

                    // Mark these heads as used
                    usedOutputHeads.add(bestHeads.left());
                    usedInputHeads.add(bestHeads.right());

                    // Remove the used heads from available lists to ensure they're not picked again
                    availableOutputHeads.remove(bestHeads.left());

                    // FIXME: Why could these two be null
                    if (bestHeads.left() != null && bestHeads.right() != null) {
                        // Generate the best path
                        ResearchLine bestLine = generateOptimalPath(
                                bestHeads.left().getConnectionPoint(),
                                bestHeads.right().getConnectionPoint(),
                                allLines,
                                verticalPathXCoords,
                                preferVerticalFirst
                        );

                        // Update vertical path zones with any new vertical segments
                        for (int j = 0; j < bestLine.getPoints().size() - 1; j++) {
                            Point p1 = bestLine.getPoints().get(j);
                            Point p2 = bestLine.getPoints().get(j + 1);
                            if (p1.x == p2.x) {
                                verticalPathXCoords.add(p1.x);
                            }
                        }

                        // Store the line
                        nodeLines.add(bestLine);
                        allLines.add(bestLine);
                    }
                }
            }

            // Add all lines for this parent
            if (!nodeLines.isEmpty()) {
                researchLines.put(parent, nodeLines);
            }
        }
    }

    /**
     * Finds the optimal pair of connection heads between two nodes based on their positions
     * in the hierarchy of connections
     */
    private Pair<ResearchHead, ResearchHead> findBestHeadPairByPosition(
            ResearchNode parent,
            ResearchNode child,
            List<ResearchHead> availableOutputHeads,
            List<ResearchHead> availableInputHeads,
            int childIndex,
            int totalChildren,
            Set<ResearchHead> usedOutputHeads,
            Set<ResearchHead> usedInputHeads
    ) {
        // Handle case with no available heads
        if (availableOutputHeads.isEmpty() || availableInputHeads.isEmpty()) {
            // Try to use any heads if we need to
            if (availableOutputHeads.isEmpty() && !parent.getOutputs().isEmpty()) {
                availableOutputHeads = new ArrayList<>(parent.getOutputs());
                availableOutputHeads.removeIf(usedOutputHeads::contains);

                if (availableOutputHeads.isEmpty()) {
                    availableOutputHeads = new ArrayList<>(parent.getOutputs());
                }
            }

            if (availableInputHeads.isEmpty() && !child.getInputs().isEmpty()) {
                availableInputHeads = new ArrayList<>(child.getInputs());
                availableInputHeads.removeIf(usedInputHeads::contains);

                if (availableInputHeads.isEmpty()) {
                    availableInputHeads = new ArrayList<>(child.getInputs());
                }
            }
        }

        if (availableOutputHeads.isEmpty() || availableInputHeads.isEmpty()) {
            ResearchHead outputHead = !parent.getOutputs().isEmpty() ? parent.getOutputs().getFirst() : null;
            ResearchHead inputHead = !child.getInputs().isEmpty() ? child.getInputs().getFirst() : null;
            return Pair.of(outputHead, inputHead);
        }

        // Sort both output and input heads by X position
        availableOutputHeads.sort(Comparator.comparingInt(ResearchHead::getX));
        availableInputHeads.sort(Comparator.comparingInt(ResearchHead::getX));

        // Get the closest output head to the input head's X position
        ResearchHead inputHead = availableInputHeads.getFirst();
        ResearchHead outputHead = availableOutputHeads.stream()
                .min(Comparator.comparingInt(head -> Math.abs(head.getX() - inputHead.getX())))
                .orElse(availableOutputHeads.getFirst());

        return Pair.of(outputHead, inputHead);
    }

    /**
     * Generates the optimal path between two points
     */
    private ResearchLine generateOptimalPath(
            Point start,
            Point end,
            List<ResearchLine> existingLines,
            Set<Integer> verticalPathXCoords,
            boolean preferVerticalFirst
    ) {
        // Generate candidate paths
        List<ResearchLine> candidates = new ArrayList<>();

        // Add direct connection if points are aligned
        if (start.x == end.x) {
            candidates.add(ResearchLine.start(start).then(end));
        }

        // Add L-shaped connections (both vertical-first and horizontal-first)
        candidates.add(ResearchLine.createLConnection(start, end, true));
        candidates.add(ResearchLine.createLConnection(start, end, false));

        // Try S-shaped connections if there's enough vertical distance
        int verticalDistance = Math.abs(end.y - start.y);
        if (verticalDistance > PANEL_HEIGHT) {
            try {
                candidates.add(ResearchLine.createSConnection(start, end, verticalDistance / 3));
            } catch (IllegalArgumentException e) {
                // Invalid offset, skip this candidate
            }

            try {
                candidates.add(ResearchLine.createSConnection(start, end, verticalDistance / 2));
            } catch (IllegalArgumentException e) {
                // Invalid offset, skip this candidate
            }
        }

        // Try paths that reuse existing vertical paths
        for (int x : verticalPathXCoords) {
            // Only consider vertical paths between start and end x positions
            if ((x > Math.min(start.x, end.x) && x < Math.max(start.x, end.x))) {
                try {
                    ResearchLine path = ResearchLine.start(start)
                            .then(start.x, start.y) // Extra point for better handling
                            .then(x, start.y)
                            .then(x, end.y)
                            .then(end.x, end.y)
                            .then(end);
                    candidates.add(path);
                } catch (Exception e) {
                    // Skip invalid path
                }
            }
        }

        // Find the best path
        ResearchLine bestPath = null;
        double bestScore = Double.MAX_VALUE;

        for (ResearchLine candidate : candidates) {
            double score = evaluatePath(candidate, existingLines, preferVerticalFirst);
            if (score < bestScore) {
                bestScore = score;
                bestPath = candidate;
            }
        }

        // If all paths have significant overlaps, try a custom path
        if (bestScore > 2.0 && !existingLines.isEmpty()) {
            ResearchLine customPath = findCustomPath(start, end, existingLines, verticalPathXCoords);
            if (customPath != null) {
                double customScore = evaluatePath(customPath, existingLines, preferVerticalFirst);
                if (customScore < bestScore) {
                    return customPath;
                }
            }
        }

        return bestPath != null ? bestPath : candidates.getFirst();
    }

    /**
     * Evaluates the quality of a path
     */
    private double evaluatePath(
            ResearchLine path,
            List<ResearchLine> existingLines,
            boolean preferVerticalFirst
    ) {
        // Start with a base score = complexity (number of segments)
        double score = path.getPoints().size() - 1;

        // Check if path follows preferred direction
        List<Point> points = path.getPoints();
        if (points.size() >= 3) {
            Point p1 = points.get(0);
            Point p2 = points.get(1);

            boolean isFirstSegmentVertical = p1.x == p2.x;
            if (isFirstSegmentVertical != preferVerticalFirst) {
                score += 0.5; // Penalty for not following preferred direction
            }
        }

        // Check for overlaps with existing lines
        for (ResearchLine existingLine : existingLines) {
            // Find exact overlaps
            Set<PotentialOverlap> overlaps = path.getOverlaps(existingLine);
            for (PotentialOverlap overlap : overlaps) {
                // True overlaps (segments on top of each other) are worse than intersections
                score += overlap.isOverlap() ? 2.0 : 1.0;
            }

            // Also check for near-misses and parallels
            score += calculateProximityPenalty(path, existingLine);
        }

        return score;
    }

    /**
     * Calculates penalty for paths that run close to each other
     */
    private double calculateProximityPenalty(ResearchLine path1, ResearchLine path2) {
        double penalty = 0;
        List<Point> points1 = path1.getPoints();
        List<Point> points2 = path2.getPoints();

        // Check for parallel segments
        for (int i = 0; i < points1.size() - 1; i++) {
            Point a1 = points1.get(i);
            Point a2 = points1.get(i + 1);

            for (int j = 0; j < points2.size() - 1; j++) {
                Point b1 = points2.get(j);
                Point b2 = points2.get(j + 1);

                // Check parallel vertical lines
                if (a1.x == a2.x && b1.x == b2.x) {
                    if (Math.abs(a1.x - b1.x) < 5) { // Close parallel vertical lines
                        // Check if they overlap in Y dimension
                        int yOverlap = Math.min(Math.max(a1.y, a2.y), Math.max(b1.y, b2.y)) -
                                Math.max(Math.min(a1.y, a2.y), Math.min(b1.y, b2.y));
                        if (yOverlap > 0) {
                            penalty += 0.5;
                        }
                    }
                }

                // Check parallel horizontal lines
                if (a1.y == a2.y && b1.y == b2.y) {
                    if (Math.abs(a1.y - b1.y) < 5) { // Close parallel horizontal lines
                        // Check if they overlap in X dimension
                        int xOverlap = Math.min(Math.max(a1.x, a2.x), Math.max(b1.x, b2.x)) -
                                Math.max(Math.min(a1.x, a2.x), Math.min(b1.x, b2.x));
                        if (xOverlap > 0) {
                            penalty += 0.5;
                        }
                    }
                }
            }
        }

        return penalty;
    }

    /**
     * Generates a custom path for difficult cases
     */
    private ResearchLine findCustomPath(
            Point start,
            Point end,
            List<ResearchLine> existingLines,
            Set<Integer> verticalPathXCoords
    ) {
        // First try to find a path using midpoints between start and end
        int minX = Math.min(start.x, end.x);
        int maxX = Math.max(start.x, end.x);
        int width = maxX - minX;

        // Try several potential midpoints
        for (int i = 1; i <= 4; i++) {
            int midX = minX + (width * i / 5);

            // Skip if this vertical path is already congested
            if (verticalPathXCoords.contains(midX)) {
                continue;
            }

            // Try a path with this midpoint
            ResearchLine path = ResearchLine.start(start)
                    .then(midX, start.y)
                    .then(midX, end.y)
                    .then(end);

            double score = evaluatePath(path, existingLines, true);
            if (score < 2.0) {
                return path;
            }
        }

        // If that fails, try an offset between layers
        int minY = Math.min(start.y, end.y);
        int maxY = Math.max(start.y, end.y);
        int midY = minY + (maxY - minY) / 2;

        // Find a good X coordinate for the middle segment
        for (int offset = -20; offset <= 20; offset += 10) {
            int midX = (start.x + end.x) / 2 + offset;

            // Try a custom stepped path (ensuring orthogonal segments)
            ResearchLine path = ResearchLine.start(start)
                    .then(start.x, midY) // Vertical segment from start
                    .then(midX, midY)    // Horizontal segment to middle
                    .then(midX, end.y)   // Vertical segment to end's Y
                    .then(end);          // Horizontal segment to end

            double score = evaluatePath(path, existingLines, true);
            if (score < 3.0) {
                return path;
            }
        }

        // Final fallback: Try a different stepped path with more segments
        int quarterY = start.y + (end.y - start.y) / 3;
        int thirdX = start.x + (end.x - start.x) / 4;

        return ResearchLine.start(start)
                .then(start.x, quarterY)  // First move vertically
                .then(thirdX, quarterY)   // Then horizontally
                .then(thirdX, midY)       // Then vertically again
                .then(end.x, midY)        // Then horizontally
                .then(end.x, end.y)       // Finally vertically
                .then(end);
    }

    public ResearchGraph getCurrentGraph() {
        return this.graph;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        if (researchLines != null) {
            for (List<ResearchLine> lines : researchLines.values()) {
                for (ResearchLine line : lines) {
                    line.render(guiGraphics);
                }
            }
        }

        ResearchNode node = graph.rootNode();
        renderNode(node, guiGraphics, i, i1, v);

        for (ResearchNode parentNode : graph.parents()) {
            parentNode.render(guiGraphics, i ,i1, v);
        }

        guiGraphics.disableScissor();
        renderNodeTooltip(node, guiGraphics, i, i1, v);
    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        for (ResearchNode childNode : node.getChildren()) {
            //ResearchLineHelper.drawLineBetweenNodes(guiGraphics, node, childNode);
            renderNode(childNode, guiGraphics, mouseX, mouseY, partialTick);
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
    public void onClick(double mouseX, double mouseY, int button) {
        for (ResearchNode node : this.graph.nodes()) {
            if (node.isHovered()) {
                this.selectedResearchWidget.setSelectedResearch(node.getInstance());
                this.setGraph(ResearchGraphCache.computeIfAbsent(Minecraft.getInstance().player, node.getInstance().getResearch()));
                break;
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (ResearchNode node : this.graph.nodes()) {
            node.translate((int) dragX, (int) dragY);
        }

        for (List<ResearchLine> lines : researchLines.values()) {
            for (ResearchLine line : lines) {
                line.translate((int) dragX, (int) dragY);
            }
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
                x += setGroupCoordinates(entry.getValue().entries(), x, getY() + i * (PANEL_HEIGHT + 10));
                x += 20;
            }
        }

        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);
            for (Map.Entry<List<ResourceKey<Research>>, Group> entry : layer.nodes().entrySet()) {
                centerGroupUnderGroup(entry.getValue(), entry.getValue().entries().getFirst().getParents().stream()
                        .sorted(Comparator.comparingInt(ResearchNode::getX)).toList(), getY() + i * (PANEL_HEIGHT + 10));
            }
        }

        Int2ObjectMap<Pair<ResearchNode, ResearchNode>> intersectingNodePairs = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < this.layers.size(); i++) {
            Layer layer = this.layers.get(i);
            int x = 0;
            for (Map.Entry<List<ResourceKey<Research>>, Group> entry0 : layer.nodes().entrySet()) {
                for (Map.Entry<List<ResourceKey<Research>>, Group> entry1 : layer.nodes().entrySet()) {
                    Pair<ResearchNode, ResearchNode> result = doNodesIntersect(entry0.getValue(), entry1.getValue());
                    if (result != null && !intersectingNodePairs.containsValue(Pair.of(result.second(), result.first()))) {
                        ResearchNode node1 = result.second();
                        ResearchNode first = node1.getParents().getFirst();
                        ResearchNode intersection = canPlaceNode(node1.getX() + PANEL_WIDTH + 10, i);
                        // TODO: After checking for intersections and moving the nodes accordingly, we also need to move the nodes coming next
                        if (intersection == null) {
                            List<Layer> children = getChildNodes(node1);
                            Nodes nodes = new Nodes(children.stream().flatMap(l -> l.flatten().stream()).toList());
                            nodes.offsetX(PANEL_WIDTH + 10);
                        }
                        intersectingNodePairs.put(i, result);
                    }
                }
            }
        }

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
                        if (doNodesIntersect(x, x + PANEL_WIDTH, node.getX(), node.getX() + PANEL_WIDTH)) {
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

        int targetWidth = target.size() * (PANEL_WIDTH + 10) - 10;
        List<ResearchNode> entries = toCenter.entries();
        int toCenterWidth = entries.size() * (PANEL_WIDTH + 10) - 10;

        int x = target.stream().min(Comparator.comparingInt(ResearchNode::getX)).get().getX();
        int startX = x + (targetWidth / 2) - (toCenterWidth / 2);

        for (int i = 0; i < entries.size(); i++) {
            ResearchNode node = entries.get(i);
            node.setX(startX + i * (PANEL_WIDTH + 10));
            node.setY(y);
        }
    }

    private int setGroupCoordinates(List<ResearchNode> researches, int x, int y) {
        int i;
        for (i = 0; i < researches.size(); i++) {
            ResearchNode node = researches.get(i);
            node.setX(x + i * (PANEL_WIDTH + 10));
            node.setY(y);
        }
        return i * (PANEL_WIDTH + 10);
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
        return doNodesIntersect(node0.getX(), node0.getX() + PANEL_WIDTH, node1.getX(), node1.getX() + PANEL_WIDTH);
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
                node.setX(x + i * (PANEL_WIDTH + 10));
            }
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int width() {
            return this.entries.size() * (PANEL_WIDTH + 10) - 10;
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

    private static @NotNull Research getResearch(ResearchNode node) {
        return ResearchHelper.getResearch(node.getInstance().getResearch(), Minecraft.getInstance().level.registryAccess());
    }

    public void onClose() {
        if (graph != null) {
            // Make sure we save the final state
            GraphStateManager.getInstance().saveLastSessionState(graph);
        }
    }
}
