package com.portingdeadmods.researchd.api.data;

import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashSet;
import java.util.Set;

public record ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> children, Set<ResearchNode> nodes) {
    private static final int RESEARCH_GRAPH_LAYERS = 2;

    private ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> children) {
        this(rootNode, parents, children, new LinkedHashSet<>());
        this.nodes.addAll(this.parents);
        this.nodes.addAll(this.children);

        createNodes(rootNode.getInstance(), 0, ClientResearchCache.ROOT_INSTANCE.is(rootNode.getInstance()) ? -1 : RESEARCH_GRAPH_LAYERS);

        for (ResearchNode node : this.nodes) {
            node.graph = this;
        }

    }

    private void createNodes(ResearchInstance instance, int nesting, int layers) {
        createNodesDownward(instance, nesting, layers);
        createNodesUpward(instance, nesting, layers);
    }

    private void createNodesUpward(ResearchInstance instance, int nesting, int layers) {
        this.nodes.add(new ResearchNode(instance));
        for (GlobalResearch instance1 : instance.getParents()) {
            if (nesting <= layers && layers != -1) {
                createNodesUpward(instance1, nesting + 1, layers);
            } else {
                return;
            }
        }
    }

    private void createNodesDownward(ResearchInstance instance, int nesting, int layers) {
        this.nodes.add(new ResearchNode(instance));
        for (GlobalResearch instance1 : instance.getChildren()) {
            if (nesting <= layers && layers != -1) {
                createNodesDownward(instance1, nesting + 1, layers);
            } else {
                return;
            }
        }
    }

    public static ResearchGraph formRootResearch(Player player, ResearchInstance root) {
        Set<ResearchNode> parents = new LinkedHashSet<>();

        ResearchNode rootNode = new ResearchNode(root);

        return new ResearchGraph(rootNode, parents, new LinkedHashSet<>());
    }

}
