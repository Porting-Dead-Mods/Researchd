package com.portingdeadmods.researchd.api.data;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashSet;
import java.util.Set;

public record ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> children, Set<ResearchNode> nodes) {
    private ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> children) {
        this(rootNode, parents, children, new LinkedHashSet<>());
        this.nodes.addAll(this.parents);
        this.nodes.addAll(this.children);

        collectUpwards(rootNode, null);
        collectDownwards(rootNode);
    }

    private void collectUpwards(ResearchNode node, ResearchNode prevNode) {
        this.nodes.add(node);
        for (ResearchNode node2 : node.getParents()) {
            UniqueArray<ResearchNode> hiddenChildren = node2.getHiddenChildren();
            hiddenChildren.addAll(node2.getChildren());
            hiddenChildren.remove(prevNode);
            collectUpwards(node2, node);
        }
    }

    private void collectDownwards(ResearchNode node) {
        this.nodes.add(node);
        for (ResearchNode node2 : node.getChildren()) {
            collectDownwards(node2);
        }
    }

    public static ResearchGraph fromRootNode(Player player, ResearchNode rootNode) {
        Set<ResearchNode> parents = new LinkedHashSet<>();
        cleanupParentsChildNodes(rootNode, parents);

        return new ResearchGraph(rootNode, parents, new LinkedHashSet<>());
    }

    private static void cleanupParentsChildNodes(ResearchNode rootNode, Set<ResearchNode> parents) {
        for (ResearchNode parent : rootNode.getParents()) {
            ResearchNode parentNode = parent.copy();
            parentNode.getChildren().removeIf(parentChildNode -> parentChildNode.getInstance().getResearch().compareTo(rootNode.getInstance().getResearch()) != 0);
            parents.add(parentNode);
            cleanupParentsChildNodes(parentNode, parents);
        }
    }
}
