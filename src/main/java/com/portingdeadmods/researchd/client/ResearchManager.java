package com.portingdeadmods.researchd.client;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.registries.Researches;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
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
        int i = 0;
        for (ResearchNode node : this.rootNodes) {
            Researchd.LOGGER.debug("root: {}", node.getHolder().getKey().location());
            setCoordinate(node, paddingX + i * 40, paddingY, 0);
            i++;
        }
    }

    public void setCoordinate(ResearchNode node, int x, int y, int nesting) {
        Researchd.LOGGER.debug("node: {}, nesting: {}", node.getHolder().getKey().location(), nesting);
        node.setX(x);
        node.setY(y);
        Researchd.LOGGER.debug("y: {}", y);
        List<ResearchNode> next = node.getNext();
        for (int i = 0; i < next.size(); i++) {
            ResearchNode nextNode = next.get(i);
            int newNesting = nesting + 1;
            setCoordinate(nextNode, x + i * 30, y + newNesting * 30, newNesting + 1);
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
}
