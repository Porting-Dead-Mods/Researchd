package com.portingdeadmods.researchd.client;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.utils.ResearchHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class ResearchManager {
    private Set<ResearchNode> nodes = new HashSet<>();
    private Set<ResearchNode> rootNodes;

    public ResearchManager(Player player) {
        RegistryAccess registryAccess = player.registryAccess();
        Set<ResearchInstance> playerResearches = ResearchHelper.getPlayerResearches(player);

        Researchd.LOGGER.debug("player: {}", player.getCapability(ResearchdCapabilities.ENTITY).researches());
        // Collect researches
        playerResearches.forEach(research -> {
            this.nodes.add(new ResearchNode(research));
        });

        for (ResearchNode node : this.nodes) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(node.getResearch().getResearch(), registryAccess).parents().stream().toList();

            for (ResourceKey<Research> parentResearch : parents) {
                ResearchNode parentNode = getNodeByResearch(parentResearch);
                if (parentNode != null) {
                    parentNode.addNext(node);
                }
            }
        }

        Set<ResearchNode> referencedNodes = new HashSet<>();
        for (ResearchNode node : this.nodes) {
            referencedNodes.addAll(node.getNext());
        }

        HashSet<ResearchNode> researchNodesCopy = new HashSet<>(this.nodes);
        researchNodesCopy.removeAll(referencedNodes);

        this.rootNodes = new HashSet<>(researchNodesCopy);
    }

    public void setCoordinates(int paddingX, int paddingY) {
        Set<ResearchNode> nodes = new HashSet<>(this.nodes);

        int i = 0;
        for (ResearchNode node : this.rootNodes) {
            setCoordinate(nodes, node, paddingX + i * 40, paddingY, 0);
            i++;
        }
    }

    public void setCoordinate(Set<ResearchNode> remaining, ResearchNode node, int x, int y, int nesting) {
        node.setX(x);
        node.setY(y);
        Researchd.LOGGER.debug("y: {}", y);
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

    public ResearchNode getNodeByResearch(ResourceKey<Research> research) {
        for (ResearchNode node : nodes) {
            if (node.getResearch().getResearch().compareTo(research) == 0) {
                return node;
            }
        }
        return null;
    }

    public Set<ResearchNode> getNodes() {
        return nodes;
    }

    public Set<ResearchNode> getRootNodes() {
        return rootNodes;
    }

    public List<List<TechListEntry>> getEntries(int cols) {
        return chunkList(this.nodes.stream()
                .map(node -> new TechListEntry(node.getResearch(), 0, 0))
                .toList(), cols);
    }

    public void setEntryCoordinates(List<List<TechListEntry>> entries, int paddingX, int paddingY) {
        int size = entries.size();
        for (int row = 0; row < size; row++) {
            List<TechListEntry> rowList = entries.get(row);
            for (int col = 0; col < rowList.size(); col++) {
                TechListEntry entry = rowList.get(col);
                entry.setX(paddingX + col * TechListEntry.WIDTH);
                entry.setY(paddingY + row * TechListEntry.HEIGHT);
            }
        }
    }

    public static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunkedList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += chunkSize) {
            chunkedList.add(new ArrayList<>(list.subList(i, Math.min(size, i + chunkSize))));
        }
        return chunkedList;
    }
}
