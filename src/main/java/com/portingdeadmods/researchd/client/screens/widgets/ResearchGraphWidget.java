package com.portingdeadmods.researchd.client.screens.widgets;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.ResearchGraph;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.graph.GraphLayoutManager;
import com.portingdeadmods.researchd.client.screens.graph.GraphStateManager;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.client.screens.lines.PotentialOverlap;
import com.portingdeadmods.researchd.client.screens.lines.ResearchHead;
import com.portingdeadmods.researchd.client.screens.lines.ResearchLine;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.portingdeadmods.researchd.client.screens.ResearchScreenWidget.PANEL_HEIGHT;
import static com.portingdeadmods.researchd.client.screens.ResearchScreenWidget.PANEL_WIDTH;

// TODO: Fix null node children and parents
public class ResearchGraphWidget extends AbstractWidget {
    private @Nullable ResearchGraph graph;
    private final Map<ResearchNode, ArrayList<ResearchLine>> researchLines;

    private final ResearchScreen researchScreen;

    public ResearchGraphWidget(ResearchScreen researchScreen, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.researchScreen = researchScreen;
        this.researchLines = new HashMap<>();
    }

    /**
     * Set the graph to be displayed, applying layout if needed and calculating connection lines
     */
    public void setGraph(ResearchGraph graph) {
        if (this.graph != graph) {
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
                GraphLayoutManager.applyLayout(graph, getX() + 10, getY() + 10);
            } else {
                // Don't do anything if we restored the layout
            }

            for (ResearchNode node : graph.nodes().values()) {
                node.refreshHeads();
            }

            // Always calculate connection lines after positions are finalized
            //TODO: REWORK AND REENABLE LINE GENERATION AFTER FINISHING NODE POSITIONING
            calculateLines();
        }
    }

    private void calculateLines() {
        this.researchLines.clear();
        Researchd.debug("Research lines", "Calculating connection lines for graph with ", this.graph.nodes().size(), " nodes.");

        // Proceed only if we have nodes to connect
        if (this.graph == null) {
            Researchd.debug("Research lines", "Graph is null, skipping line calculation.");
            return;
        }

        if (this.graph.nodes().isEmpty()) {
            Researchd.debug("Research lines", "No nodes in graph, skipping line calculation.");
            return;
        }

        // Process nodes layer by layer, bottom to top, then left to right
        List<ResearchNode> sortedNodes = new ArrayList<>(graph.nodes().values());
        sortedNodes.sort(Comparator
                .comparingInt(ResearchNode::getY)  // First by Y (layer)
                .reversed()                        // Reverse to start from bottom
                .thenComparingInt(ResearchNode::getX)); // Then by X position

        Researchd.debug("Research lines", "Sorted ", sortedNodes.size(), " nodes for processing");

        // Track all generated lines
        List<ResearchLine> allLines = new ArrayList<>();

        // Keep track of established vertical paths (X coordinates where vertical line segments exist)
        Set<Integer> verticalPathXCoords = new HashSet<>();

        // Track which heads are already used to prevent duplicates
        Set<ResearchHead> usedOutputHeads = new HashSet<>();
        Set<ResearchHead> usedInputHeads = new HashSet<>();

        // Process each node
        for (ResearchNode parent : sortedNodes) {
            Researchd.debug("Research lines", "Processing parent node: ", parent.getInstance().getKey().location());

            ArrayList<ResearchLine> nodeLines = new ArrayList<>();

            // Group children by layer for consistent path styles
            Map<Integer, List<ResearchNode>> childrenByLayer = new HashMap<>();
            for (ResearchNode child : parent.getChildren()) {
                if (!this.graph.nodes().containsValue(child)) continue;

                childrenByLayer
                        .computeIfAbsent(GraphLayoutManager.calculateDepth(child), k -> new ArrayList<>())
                        .add(child);
            }

            Researchd.debug("Research lines", "Parent has ", childrenByLayer.size(), " child layers with total children: ", parent.getChildren().size());

            // Process each layer of children
            for (Map.Entry<Integer, List<ResearchNode>> entry : childrenByLayer.entrySet()) {
                List<ResearchNode> layerChildren = entry.getValue();
                Researchd.debug("Research lines", "Processing layer ", entry.getKey(), " with ", layerChildren.size(), " children");


                // Sort children left to right
                layerChildren.sort(Comparator.comparingInt(ResearchNode::getX));

                // Choose consistent path style for this parent-layer combination
                boolean preferVerticalFirst =
                        (parent.getX() + PANEL_WIDTH / 2) <
                                (layerChildren.stream()
                                        .mapToInt(n -> n.getX() + PANEL_WIDTH / 2)
                                        .average()
                                        .orElse(0));

                Researchd.debug("Research lines", "Path style for this layer: ", preferVerticalFirst ? "vertical-first" : "horizontal-first");

                // Get all available output heads for this parent
                List<ResearchHead> availableOutputHeads = parent.getOutputs().stream()
                        .filter(head -> !usedOutputHeads.contains(head))
                        .sorted(Comparator.comparingInt(ResearchHead::getX))
                        .collect(Collectors.toList());

                Researchd.debug("Research lines", "Available output heads: ", availableOutputHeads.size(), " out of ", parent.getOutputs().size());

                // Process each child and assign heads in positional order
                for (int i = 0; i < layerChildren.size(); i++) {
                    ResearchNode child = layerChildren.get(i);

                    Researchd.debug("Research lines", "Processing child ", (i + 1), "/", layerChildren.size(), ": ", child.getInstance().getKey().location());
                    Researchd.debug("Research lines", "Total input heads: ", child.getInputs().size());

                    // Get available input heads for this child
                    List<ResearchHead> availableInputHeads = child.getInputs().stream()
                            .filter(head -> !usedInputHeads.contains(head))
                            .sorted(Comparator.comparingInt(ResearchHead::getX))
                            .collect(Collectors.toList());

                    Researchd.debug("Research lines", "Available input heads: ", availableInputHeads.size(), " out of ", child.getInputs().size());

                    // Find the best head pair based on position
                    Pair<ResearchHead, ResearchHead> bestHeads = findBestHeadPairByPosition(
                            parent, child, availableOutputHeads, availableInputHeads,
                            i, layerChildren.size(), usedOutputHeads, usedInputHeads
                    );

                    Researchd.debug("Research lines", "Selected head pair - Output: ", (bestHeads.left() != null ? "found" : "null"), ", Input: ", (bestHeads.right() != null ? "found" : "null"));

                    // Mark these heads as used
                    usedOutputHeads.add(bestHeads.left());
                    usedInputHeads.add(bestHeads.right());

                    // Remove the used heads from available lists to ensure they're not picked again
                    availableOutputHeads.remove(bestHeads.left());

                    // FIXME: Why could these two be null
                    if (bestHeads.left() != null && bestHeads.right() != null) {
                        Researchd.debug("Research lines", "Generating optimal path between connection points");

                        // Generate the best path
                        ResearchLine bestLine = generateOptimalPath(
                                bestHeads.left().getConnectionPoint(),
                                bestHeads.right().getConnectionPoint(),
                                allLines,
                                verticalPathXCoords,
                                preferVerticalFirst
                        );

                        Researchd.debug("Research lines", "Generated path with ", bestLine.getPoints().size(), " points");

                        // Update vertical path zones with any new vertical segments
                        int verticalSegments = 0;
                        for (int j = 0; j < bestLine.getPoints().size() - 1; j++) {
                            Point p1 = bestLine.getPoints().get(j);
                            Point p2 = bestLine.getPoints().get(j + 1);
                            if (p1.x == p2.x) {
                                verticalPathXCoords.add(p1.x);
                                verticalSegments++;
                            }
                        }

                        Researchd.debug("Research lines", "Added ", verticalSegments, " vertical segments to path tracking");

                        // Store the line
                        nodeLines.add(bestLine);
                        allLines.add(bestLine);
                    } else {
                        Researchd.debug("Research lines", "Skipping line generation due to null heads");
                    }
                }
            }

            // Add all lines for this parent
            if (!nodeLines.isEmpty()) {
                this.researchLines.put(parent, nodeLines);
                Researchd.debug("Research lines", "Added ", nodeLines.size(), " lines for parent node");
            } else {
                Researchd.debug("Research lines", "No lines generated for parent node");
            }
        }
        Researchd.debug("Research lines", "Line calculation complete. Generated ", researchLines.size(), " line groups with total lines: ", allLines.size());
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
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        if (this.researchLines != null) {
            for (List<ResearchLine> lines : this.researchLines.values()) {
                for (ResearchLine line : lines) {
                    line.render(guiGraphics);
                }
            }
        }

        if (this.graph == null || this.graph.nodes() == null) {
            return;
        }

        for (ResearchNode node : this.graph.nodes().values()) {
            if (node.isRootNode()) {
                float scale = 1.75f;
                int width = ResearchScreenWidget.PANEL_WIDTH;
                int height = ResearchScreenWidget.PANEL_HEIGHT;

                // getX() and getY() = top-left of normal panel (scale = 1)
                float baseX = node.getX();
                float baseY = node.getY();

                // compute center of unscaled panel
                float centerX = baseX + width / 2f;
                float centerY = baseY + height / 2f;

                // compute top-left of scaled panel to keep same center
                int scaledX = (int) (centerX - (width * scale) / 2f);
                int scaledY = (int) (centerY - (height * scale) / 2f);

                node.setHovered(guiGraphics, scaledX, scaledY, (int) (20 * scale), (int) (24 * scale), mouseX, mouseY);
                ResearchScreenWidget.renderResearchPanel(
                        guiGraphics,
                        node.getInstance(),
                        scaledX + 1,
                        scaledY,
                        mouseX,
                        mouseY,
                        scale
                );
            } else {
                node.render(guiGraphics, mouseX, mouseY, v);
            }
        }


    }

    private void renderNode(ResearchNode node, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        node.render(guiGraphics, mouseX, mouseY, partialTick);

        for (ResearchNode childNode : node.getChildren()) {
            //ResearchLineHelper.drawLineBetweenNodes(guiGraphics, node, childNode);
            renderNode(childNode, guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    // TODO: Cache hovered node like the isHovered field
    public void renderNodeTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.isHovered() || this.graph == null || this.graph.nodes() == null) return;

        for (ResearchNode node : this.graph.nodes().values()) {
            if (node.isHovered()) {
                Minecraft minecraft = Minecraft.getInstance();
                // Debug tooltip
                if (SharedConstants.IS_RUNNING_IN_IDE && !ResearchScreen.hasControlDown()) {
                    guiGraphics.renderComponentTooltip(minecraft.font, List.of(
                            Utils.registryTranslation(node.getInstance().getKey()),
                            Component.translatable("research_desc." + Researchd.MODID + "." + node.getInstance().getKey().location().getPath()),
                            SharedConstants.IS_RUNNING_IN_IDE ? Component.literal("Press Ctrl for debug info") : Component.empty()
                    ), mouseX, mouseY);
                } else {
                    guiGraphics.renderComponentTooltip(minecraft.font, List.of(
                            Utils.registryTranslation(node.getInstance().getKey()),
                            Component.literal("x: %d, y: %d".formatted(node.getX(), node.getY())),
                            Component.literal("w: %d, h: %d".formatted(node.getWidth(), node.getHeight())),
                            Component.literal("hovered: %s".formatted(node.isHovered())),
                            Component.literal("%d parents".formatted(node.getParents().size())),
                            Component.literal("%d children".formatted(node.getChildren().size()))
                    ), mouseX, mouseY);
                }
                break;
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.graph == null || this.graph.nodes() == null) {
            return false;
        }
        
        for (ResearchNode node : this.graph.nodes().values()) {
            if (node.isHovered()) {
                this.setGraph(ResearchGraphCache.computeIfAbsent(node.getInstance().getKey()));
                UniqueArray<ResearchInstance> entries = this.researchScreen.getTechList().entries();
                this.researchScreen.getSelectedResearchWidget().setSelectedResearch(entries.get(entries.indexOf(node.getInstance())));
                this.researchScreen.prevSelectedResearchMethodWidget = this.researchScreen.getSelectedResearchWidget().methodWidget;
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isHovered()) {
            if (this.graph != null && this.graph.nodes() != null) {
                for (ResearchNode node : this.graph.nodes().values()) {
                    node.translate((int) dragX, (int) dragY);
                }
            }

            for (List<ResearchLine> lines : this.researchLines.values()) {
                for (ResearchLine line : lines) {
                    line.translate((int) dragX, (int) dragY);
                }
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public void onClose() {
        if (graph != null) {
            // Make sure we save the final state
            GraphStateManager.getInstance().saveLastSessionState(graph);

            // TODO: Reimpl this
            //ClientResearchCache.ROOT_NODE = graph.rootNode();
        }
    }
}
