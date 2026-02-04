package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.screens.research.graph.ResearchNode;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data of the research graph
 *
 * @param rootNode Root node that is displayed bigger than the rest of the nodes
 * @param nodes    All the nodes in the current graph
 */
public record ResearchGraph(ResearchNode rootNode, Map<ResourceKey<Research>, ResearchNode> nodes, ResearchPage page) {
    private static final int RESEARCH_GRAPH_LAYERS = 2;

    // TODO: Add researchPacks to the team's research progress
    private ResearchGraph(GlobalResearch researchRoot, Map<ResourceKey<Research>, ResearchInstance> researches) {
        this(new ResearchNode(researches.get(researchRoot.getResearchKey())), new LinkedHashMap<>(), CommonResearchCache.pageOf(researchRoot));

        createNodes(rootNode.getInstance(), 0, CommonResearchCache.rootResearch != null && CommonResearchCache.rootResearch.is(rootNode.getInstance().getKey())
                ? -1
                : RESEARCH_GRAPH_LAYERS, researches);
        this.rootNode.setRootNode(true);
        this.nodes.put(this.rootNode.getInstance().getKey(), this.rootNode);
        collectRelatedNodes();

        for (ResearchNode node : this.nodes.values()) {
            node.graph = this;
        }
    }

    private void collectRelatedNodes() {
        for (ResearchNode node : this.nodes.values()) {
            GlobalResearch research = node.getInstance().getResearch();
            Set<GlobalResearch> parents = research.getParents();

            for (GlobalResearch parent : parents) {
                ResearchNode parentNode = this.nodes.get(parent.getResearchKey());
                node.addParent(parentNode);
            }

            Set<GlobalResearch> children = research.getChildren();

            for (GlobalResearch child : children) {
                ResearchNode childNode = this.nodes.get(child.getResearchKey());
                node.addChild(childNode);
            }
        }
    }

    private void createNodes(ResearchInstance instance, int nesting, int layers, Map<ResourceKey<Research>, ResearchInstance> researches) {
        createNodesDownward(instance, nesting, layers, researches);
        createNodesUpward(instance, nesting, layers, researches);
    }

    private void createNodesUpward(ResearchInstance instance, int nesting, int layers, Map<ResourceKey<Research>, ResearchInstance> researches) {
        if (nesting > 0) {
            this.nodes.put(instance.getKey(), new ResearchNode(instance));
        }
        for (GlobalResearch research : instance.getParents()) {
            if (nesting < layers || layers == -1) {
                createNodesUpward(researches.get(research.getResearchKey()), nesting + 1, layers, researches);
            } else {
                return;
            }
        }
    }

    private void createNodesDownward(ResearchInstance instance, int nesting, int layers, Map<ResourceKey<Research>, ResearchInstance> researches) {
        if (nesting > 0) {
            this.nodes.put(instance.getKey(), new ResearchNode(instance));
        }
        for (GlobalResearch research : instance.getChildren()) {
            if (nesting < layers || layers == -1) {
                createNodesDownward(researches.get(research.getResearchKey()), nesting + 1, layers, researches);
            } else {
                return;
            }
        }
    }

    public static ResearchGraph fromRootResearch(ResourceKey<Research> root, Map<ResourceKey<Research>, ResearchInstance> researches) {
        return new ResearchGraph(CommonResearchCache.globalResearches.get(root), researches);
    }

    /**
     * @param page The research page containing all researches to include
     * @param rootNode The root node to use for this graph - TODO: Parse researches from the page to get the root directly
     * @param researches Lookup
     * @return A new ResearchGraph for the page
     */
    public static ResearchGraph fromResearchPage(ResearchPage page, GlobalResearch rootNode, Map<ResourceKey<Research>, ResearchInstance> researches) {
        // TODO: FINISH
        return new ResearchGraph(rootNode, researches);
    }
}
