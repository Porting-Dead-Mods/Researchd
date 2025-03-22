package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import net.minecraft.resources.ResourceKey;

import java.util.*;

/**
 * Manages caching of different graph views and their layouts
 */
public class GraphStateManager {
    // Singleton instance
    private static final GraphStateManager INSTANCE = new GraphStateManager();

    // Cache of complete graph layouts, keyed by a unique identifier
    private final Map<String, GraphLayout> graphLayouts = new HashMap<>();

    // Limit to prevent excessive memory usage
    private static final int MAX_CACHED_LAYOUTS = 30;

    private GraphStateManager() {}

    public static GraphStateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Generate a unique key for a specific graph view based on its visible nodes
     */
    private String generateGraphKey(ResearchGraph graph) {
        if (graph == null || graph.nodes().isEmpty()) {
            return "";
        }

        // Include the root node in the key
        StringBuilder keyBuilder = new StringBuilder("root:");
        keyBuilder.append(graph.rootNode().getInstance().getResearch().location().toString());

        // Add all node IDs in a consistent order
        List<String> nodeKeys = new ArrayList<>();
        for (ResearchNode node : graph.nodes()) {
            nodeKeys.add(node.getInstance().getResearch().location().toString());
        }

        // Sort to ensure consistent ordering
        Collections.sort(nodeKeys);

        // Add to key
        keyBuilder.append(";nodes:");
        for (String nodeKey : nodeKeys) {
            keyBuilder.append(nodeKey).append(",");
        }

        return keyBuilder.toString();
    }

    /**
     * Save the layout of a complete graph
     */
    public void saveGraphLayout(ResearchGraph graph) {
        if (graph == null || graph.nodes().isEmpty()) {
            return;
        }

        // Generate the unique key for this graph view
        String graphKey = generateGraphKey(graph);

        // Create a layout object with node positions
        GraphLayout layout = new GraphLayout();
        for (ResearchNode node : graph.nodes()) {
            ResourceKey<Research> researchKey = node.getInstance().getResearch();
            layout.nodePositions.put(researchKey, new NodePosition(node.getX(), node.getY()));
        }

        // Store layout in cache
        graphLayouts.put(graphKey, layout);

        // Clean up cache if it gets too large
        if (graphLayouts.size() > MAX_CACHED_LAYOUTS) {
            // Just remove the first entry - could be more sophisticated
            String firstKey = graphLayouts.keySet().iterator().next();
            graphLayouts.remove(firstKey);
        }
    }

    /**
     * Attempt to restore a previously saved graph layout
     *
     * @return true if layout was fully restored, false otherwise
     */
    public boolean restoreGraphLayout(ResearchGraph graph) {
        if (graph == null || graph.nodes().isEmpty()) {
            return false;
        }

        // Generate the unique key for this graph view
        String graphKey = generateGraphKey(graph);

        // Look up the saved layout
        GraphLayout layout = graphLayouts.get(graphKey);
        if (layout == null) {
            return false;
        }

        // Restore positions for all nodes
        boolean allRestored = true;
        for (ResearchNode node : graph.nodes()) {
            ResourceKey<Research> researchKey = node.getInstance().getResearch();
            NodePosition position = layout.nodePositions.get(researchKey);

            if (position != null) {
                node.setXExt(position.x);
                node.setYExt(position.y);
            } else {
                allRestored = false;
            }
        }

        return allRestored;
    }

    /**
     * Check if we have a cached layout for this exact graph view
     */
    public boolean hasLayoutForGraph(ResearchGraph graph) {
        if (graph == null) {
            return false;
        }

        String graphKey = generateGraphKey(graph);
        return graphLayouts.containsKey(graphKey);
    }

    /**
     * Clear all cached layouts
     */
    public void clearCache() {
        graphLayouts.clear();
    }

    /**
     * Represents a complete graph layout
     */
    private static class GraphLayout {
        // Map from research key to position
        final Map<ResourceKey<Research>, NodePosition> nodePositions = new HashMap<>();
    }

    /**
     * Simple class to store node position
     */
    private static class NodePosition {
        final int x;
        final int y;

        NodePosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}