package com.portingdeadmods.researchd.client;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechList;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.registries.Researches;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.*;

public class ResearchManager {
    private Set<Holder<Research>> researches = new HashSet<>();
    private Set<ResearchNode> nodes = new HashSet<>();
    private Set<ResearchNode> rootNodes;

    public ResearchManager(Level level) {
        RegistryAccess registryAccess = level.registryAccess();
        HolderLookup.RegistryLookup<Research> registry = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_KEY);

        // Collect researches
        registry.listElements().forEach(research -> {
            this.researches.add(research);
            this.nodes.add(new ResearchNode(research));
        });

        for (ResearchNode node : this.nodes) {
            List<Holder<Research>> parents = node.getHolder().value().parents().stream()
                    .map(registryAccess::holderOrThrow).toList();

            for (Holder<Research> parentResearch : parents) {
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
            Researchd.LOGGER.debug("root: {}", node.getHolder().getKey().location());
            setCoordinate(nodes, node, paddingX + i * 40, paddingY, 0);
            i++;
        }
    }

    public void setCoordinate(Set<ResearchNode> remaining, ResearchNode node, int x, int y, int nesting) {
        Researchd.LOGGER.debug("node: {}, nesting: {}", node.getHolder().getKey().location(), nesting);
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

    public ResearchNode getNodeByResearch(Holder<Research> research) {
        for (ResearchNode node : nodes) {
            if (node.getHolder().is(research.getKey())) {
                return node;
            }
        }
        return null;
    }

    public Set<ResearchNode> getNodes() {
        return nodes;
    }

    public Set<Holder<Research>> getResearches() {
        return researches;
    }

    public Set<ResearchNode> getRootNodes() {
        return rootNodes;
    }

    public List<List<TechListEntry>> getEntries(int cols) {
        return chunkList(this.researches.stream()
                .map(holder -> new TechListEntry(holder.value(), EntryType.RESEARCHABLE, 0, 0))
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
