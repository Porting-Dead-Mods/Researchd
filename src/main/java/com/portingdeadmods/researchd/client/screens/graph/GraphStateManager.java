package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.lines.ResearchLine;
import com.portingdeadmods.researchd.utils.Spaghetti;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplified state manager that only caches the most recent graph layout
 */
public class GraphStateManager {
    // Singleton instance
    private static final GraphStateManager INSTANCE = new GraphStateManager();

    // Map from research key to node position and scale
    private final Map<ResourceKey<Research>, NodeState> lastSessionState = new HashMap<>();

    // Root node of last graph (for identification purposes)
    private ResourceKey<Research> lastGraphRoot = null;

    private GraphStateManager() {
    }

    public static GraphStateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Save the current graph state when closing the window
     */
    public void saveLastSessionState(ResearchGraph graph) {
        if (graph == null || graph.nodes().isEmpty() || graph.rootNode() == null) {
            Researchd.debug("Graph State Cache", "No graph to save state for");
            return;
        }

        // Clear previous state
        clearState();

        // Save the root node for identification
        lastGraphRoot = graph.rootNode().getInstance().getResearch();

        // Save position and scale for each node
        for (ResearchNode node : graph.nodes()) {
            ResourceKey<Research> key = node.getInstance().getResearch();
            lastSessionState.put(key, new NodeState(
                    node.getX(),
                    node.getY()
            ));
        }
        Researchd.debug("Graph State Cache","Saved last session state for graph: " + lastGraphRoot);
    }
    /**
     * Try to restore the previous session state if this is the same graph
     *
     * @return true if state was restored, false otherwise
     */
    public boolean tryRestoreLastSessionState(ResearchGraph graph) {
        if (graph == null || graph.nodes().isEmpty() || graph.rootNode() == null ||
                lastGraphRoot == null || lastSessionState.isEmpty()) {
            Researchd.debug("Graph State Cache","No previous session state to restore");
            return false;
        }

        // Check if this is the same graph as last time (by root node)
        if (!graph.rootNode().getInstance().getResearch().equals(lastGraphRoot)) {
            Researchd.debug("Graph State Cache","Graph root has changed, not restoring last session state");
            return false;
        }

        // Restore positions for nodes that exist in both graphs
        Researchd.debug("Graph State Cache","Restoring last session");
        for (ResearchNode node : graph.nodes()) {
            ResourceKey<Research> key = node.getInstance().getResearch();
            NodeState state = lastSessionState.get(key);

            if (state != null) {
                node.setXExt(state.x);
                node.setYExt(state.y);
            }
        }

        return true;
    }

    /**
     * Clear cached state
     */
    public void clearState() {
        lastSessionState.clear();
        lastGraphRoot = null;
    }

    /**
     * Simple class to store a node's state
     */
    private record NodeState(int x, int y) {
    }
}