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

public record ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents, Set<ResearchNode> nodes) {
    public ResearchGraph(ResearchNode rootNode, Set<ResearchNode> parents) {
        this(rootNode, parents, new LinkedHashSet<>());
        collectNodes(rootNode);
        this.nodes.addAll(this.parents);
    }

    private void collectNodes(ResearchNode node) {
        this.nodes.add(node);
        for (ResearchNode node1 : node.getChildren()) {
            collectNodes(node1);
        }
    }

    public static ResearchGraph fromRootNode(Player player, ResearchNode rootNode) {
        RegistryAccess lookup = player.registryAccess();
        Holder<Research> researchHolder = lookup.holderOrThrow(rootNode.getInstance().getResearch());

        Set<ResearchNode> parents = new LinkedHashSet<>();

        for (ResourceKey<Research> parent : researchHolder.value().parents()) {
            // TODO: Set coordinates
            parents.add(ClientResearchCache.getNodeByResearch(parent));
        }

        return new ResearchGraph(rootNode, parents);
    }
}
