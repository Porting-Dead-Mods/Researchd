package com.portingdeadmods.researchd.utils.researches.data;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashSet;
import java.util.Set;

public record ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> children, Set<ResearchNode> nodes) {
    public ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> children) {
        this(rootNode, parents, children, new LinkedHashSet<>());
        collectUpwards(rootNode);
        collectDownwards(rootNode);

        this.nodes.addAll(this.parents);
        this.nodes.addAll(this.children);
    }

    private void collectUpwards(ResearchNode node) {
        this.nodes.add(node);
        for (ResearchNode node2 : node.getParents()) {
            collectUpwards(node2);
        }
    }

    private void collectDownwards(ResearchNode node) {
        this.nodes.add(node);
        for (ResearchNode node2 : node.getChildren()) {
            collectDownwards(node2);
        }
    }

    public static ResearchGraph fromRootNode(Player player, ResearchNode rootNode) {
        RegistryAccess lookup = player.registryAccess();
        Holder<Research> researchHolder = lookup.holderOrThrow(rootNode.getInstance().getResearch());

        Set<ResearchNode> parents = new LinkedHashSet<>();
        for (ResourceKey<Research> parent : researchHolder.value().parents()) {
            parents.add(ClientResearchCache.getNodeByResearch(parent));
        }

        return new ResearchGraph(rootNode, parents, new LinkedHashSet<>());
    }
}
