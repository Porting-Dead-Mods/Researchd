package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import net.minecraft.resources.ResourceKey;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores and manages the state of research graphs between UI sessions
 */
public class GraphStateManager {
    // Singleton instance
    private static final GraphStateManager INSTANCE = new GraphStateManager();

    // Cache of node positions by research key
    private final Map<ResourceKey<Research>, Point> nodePositions = new HashMap<>();

    // Maps resource keys to graph instances
    private final Map<ResourceKey<Research>, ResearchGraph> cachedGraphs = new HashMap<>();

    private GraphStateManager() {}

    public static GraphStateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Saves the position of a node
     */
    public void saveNodePosition(ResearchNode node) {
        if (node != null) {
            nodePositions.put(
                    node.getInstance().getResearch(),
                    new Point(node.getX(), node.getY())
            );
        }
    }

    /**
     * Saves positions for all nodes in a graph
     */
    public void saveGraphPositions(ResearchGraph graph) {
        if (graph != null) {
            for (ResearchNode node : graph.nodes()) {
                saveNodePosition(node);
            }
        }
    }

    /**
     * Restores the position of a node if available
     * @return true if position was restored
     */
    public boolean restoreNodePosition(ResearchNode node) {
        if (node == null) return false;

        ResourceKey<Research> key = node.getInstance().getResearch();
        if (nodePositions.containsKey(key)) {
            Point pos = nodePositions.get(key);
            node.setXExt(pos.x);
            node.setYExt(pos.y);
            return true;
        }
        return false;
    }

    /**
     * Attempt to restore positions for all nodes in a graph
     * @return true if all nodes had cached positions
     */
    public boolean restoreGraphPositions(ResearchGraph graph) {
        if (graph == null) return false;

        boolean allRestored = true;
        for (ResearchNode node : graph.nodes()) {
            if (!restoreNodePosition(node)) {
                allRestored = false;
            }
        }
        return allRestored;
    }

    /**
     * Caches a graph by its root research key
     */
    public void cacheGraph(ResearchGraph graph) {
        if (graph != null && graph.rootNode() != null) {
            cachedGraphs.put(
                    graph.rootNode().getInstance().getResearch(),
                    graph
            );
        }
    }

    /**
     * Retrieves a cached graph by research key
     */
    public ResearchGraph getCachedGraph(ResourceKey<Research> key) {
        return cachedGraphs.get(key);
    }

    /**
     * Clears all cached data
     */
    public void clearCache() {
        nodePositions.clear();
        cachedGraphs.clear();
    }
}