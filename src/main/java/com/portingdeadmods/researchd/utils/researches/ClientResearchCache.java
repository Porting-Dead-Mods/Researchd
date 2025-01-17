package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClientResearchCache {
    public static Set<ResearchNode> NODES = new HashSet<>();
    public static ResearchNode ROOT_NODE;

    public static void initialize(Player player) {
        RegistryAccess registryAccess = player.registryAccess();
        Set<ResearchInstance> playerResearches = ResearchHelper.getPlayerResearches(player);

        Researchd.LOGGER.debug("player: {}", player.getCapability(ResearchdCapabilities.ENTITY).researches());

        // Collect researches
        playerResearches.forEach(research -> {
            NODES.add(new ResearchNode(research.copy()));
        });

        // Add next nodes
        for (ResearchNode node : NODES) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(node.getResearch().getResearch(), registryAccess).parents();

            for (ResourceKey<Research> parentResearch : parents) {
                ResearchNode parentNode = getNodeByResearch(parentResearch);
                if (parentNode != null) {
                    parentNode.addNext(node);
                }
            }
        }

        Set<ResearchNode> referencedNodes = new HashSet<>();
        for (ResearchNode node : NODES) {
            referencedNodes.addAll(node.getNext());
        }

        HashSet<ResearchNode> researchNodesCopy = new HashSet<>(NODES);
        researchNodesCopy.removeAll(referencedNodes);

        ROOT_NODE = researchNodesCopy.stream().findFirst().orElse(null);
    }

    public static void setCoordinates(int paddingX, int paddingY) {
        Set<ResearchNode> nodes = new HashSet<>(NODES);

        setCoordinate(nodes, ROOT_NODE, paddingX, paddingY, 0);
    }

    public static void setCoordinate(Set<ResearchNode> remaining, ResearchNode node, int x, int y, int nesting) {
        node.setX(x);
        node.setY(y);
        Set<ResearchNode> next = node.getNext();
        int newNesting = nesting + 1;
        int i = 0;
        for (ResearchNode nextNode : next) {
            if (remaining.contains(nextNode)) {
                remaining.remove(nextNode);
                setCoordinate(remaining, nextNode, x + i * 30, y + newNesting * 30, newNesting);
            }
            i++;
        }
    }

    public static ResearchNode getNodeByResearch(ResourceKey<Research> research) {
        for (ResearchNode node : NODES) {
            if (node.getResearch().getResearch().compareTo(research) == 0) {
                return node;
            }
        }
        return null;
    }
}
