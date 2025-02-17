package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// TODO: We should just store a set of ResearchInstance
public final class ClientResearchCache {
    public static Set<ResearchNode> NODES = new LinkedHashSet<>();
    public static ResearchNode ROOT_NODE;

    public static void initialize(Player player) {
        NODES.clear();

        RegistryAccess registryAccess = player.registryAccess();
        Set<ResearchInstance> playerResearches = ResearchHelper.getPlayerResearches(player);

        Researchd.LOGGER.debug("player: {}", player.getCapability(ResearchdCapabilities.ENTITY).researches());

        // Collect researches
        playerResearches.forEach(research -> {
            NODES.add(new ResearchNode(research.copy()));
        });

        // Add next nodes
        for (ResearchNode node : NODES) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(node.getInstance().getResearch(), registryAccess).parents();

            for (ResourceKey<Research> parentResearch : parents) {
                ResearchNode parentNode = getNodeByResearch(parentResearch);
                if (parentNode != null) {
                    parentNode.addChild(node);
                }
            }
        }

        for (ResearchNode node : NODES) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(node.getInstance().getResearch(), registryAccess).parents();

            for (ResourceKey<Research> parent : parents) {
                node.addParent(getNodeByResearch(parent));
            }
        }

        Set<ResearchNode> referencedNodes = new LinkedHashSet<>();
        for (ResearchNode node : NODES) {
            referencedNodes.addAll(node.getChildren());
        }

        Set<ResearchNode> researchNodesCopy = new LinkedHashSet<>(NODES);
        researchNodesCopy.removeAll(referencedNodes);

        ROOT_NODE = researchNodesCopy.stream().findFirst().orElse(null);
        if (ROOT_NODE != null) {
            ROOT_NODE.setRootNode(true);
        }
    }

    public static ResearchNode getNodeByResearch(ResourceKey<Research> research) {
        for (ResearchNode node : NODES) {
            if (node.getInstance().getResearch().compareTo(research) == 0) {
                return node;
            }
        }
        return null;
    }
}
