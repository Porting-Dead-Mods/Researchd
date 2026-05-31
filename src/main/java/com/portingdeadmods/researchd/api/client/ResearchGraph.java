package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.*;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.client.screens.research.graph.ResearchNode;
import com.portingdeadmods.researchd.api.research.ResearchRelations;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data of the researchPack graph
 *
 * @param rootNode Root node that is displayed bigger than the rest of the nodes
 * @param nodes    All the nodes in the current graph
 */
public record ResearchGraph(ResearchNode rootNode, Map<ResourceKey<Research>, ResearchNode> nodes, ResearchPage page) {
    private static final int RESEARCH_GRAPH_LAYERS = 2;

    // TODO: Add researches to the team's research progress
    private ResearchGraph(ResourceKey<Research> researchRoot, Map<ResourceKey<Research>, ResearchInstance> researches) {
        this(new ResearchNode(researches.get(researchRoot)), new LinkedHashMap<>(), ResearchdApi.getResearchManager().getPageByResearch(researchRoot));

        ResearchManager researchManager = ResearchdApi.getResearchManager();
        createNodes(rootNode.getInstance(), 0, researchManager.getRootsForPage(this.page().id()).contains(researchRoot)
                ? -1
                : RESEARCH_GRAPH_LAYERS, researches);
        this.rootNode.setRootNode(true);
        this.nodes.put(this.rootNode.getInstance().getResearch(), this.rootNode);
        collectRelatedNodes();

        for (ResearchNode node : this.nodes.values()) {
            node.graph = this;
        }
    }

    private void collectRelatedNodes() {
        for (ResearchNode node : this.nodes.values()) {
            ResourceKey<Research> research = node.getInstance().getResearch();

            ResearchRelations relations = ResearchdApi.getResearchManager().getRelationsForResearch(research);

            Set<ResearchRelations> parents = relations.getParents();

            for (ResearchRelations parent : parents) {
                ResearchNode parentNode = this.nodes.get(parent.getResearchKey());
                if (parentNode != null) {
                    node.addParent(parentNode);
                }
            }

            Set<ResearchRelations> children = relations.getChildren();

            for (ResearchRelations child : children) {
                ResearchNode childNode = this.nodes.get(child.getResearchKey());
                if (childNode != null) {
                    node.addChild(childNode);
                }
            }
        }
    }

    private void createNodes(ResearchInstance instance, int nesting, int layers, Map<ResourceKey<Research>, ResearchInstance> researches) {
        createNodesDownward(instance, nesting, layers, researches);
        createNodesUpward(instance, nesting, layers, researches);
    }

    private void createNodesUpward(ResearchInstance instance, int nesting, int layers, Map<ResourceKey<Research>, ResearchInstance> researches) {
        if (nesting > 0) {
            this.nodes.put(instance.getResearch(), new ResearchNode(instance));
        }
        ResearchRelations relations = ResearchdApi.getResearchManager().getRelationsForResearch(instance.getResearch());
        for (ResearchRelations research : relations.getParents()) {
            if (nesting < layers || layers == -1) {
                createNodesUpward(researches.get(research.getResearchKey()), nesting + 1, layers, researches);
            } else {
                return;
            }
        }
    }

    private void createNodesDownward(ResearchInstance instance, int nesting, int layers, Map<ResourceKey<Research>, ResearchInstance> researches) {
        if (nesting > 0) {
            this.nodes.put(instance.getResearch(), new ResearchNode(instance));
        }
        ResearchRelations relations = ResearchdApi.getResearchManager().getRelationsForResearch(instance.getResearch());
        for (ResearchRelations research : relations.getChildren()) {
            if (nesting < layers || layers == -1) {
                createNodesDownward(researches.get(research.getResearchKey()), nesting + 1, layers, researches);
            } else {
                return;
            }
        }
    }

    public static ResearchGraph fromRootResearch(ResourceKey<Research> root, Map<ResourceKey<Research>, ResearchInstance> researches) {
        return new ResearchGraph(root, researches);
    }

    /**
     * @param page The researchPack page containing all researches to include
     * @param rootNode The root node to use for this graph - TODO: Parse researches from the page to get the root directly
     * @param researches Lookup
     * @return A new ResearchGraph for the page
     */
    public static ResearchGraph fromResearchPage(ResearchPage page, ResourceKey<Research> rootNode, Map<ResourceKey<Research>, ResearchInstance> researches) {
        // TODO: FINISH
        return new ResearchGraph(rootNode, researches);
    }
}
