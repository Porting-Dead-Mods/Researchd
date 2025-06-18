package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public final class ClientResearchCache {
    public static final Set<ResearchNode> NODES = new LinkedHashSet<>();
    public static final Set<ResearchInstance> RESEARCHES = new LinkedHashSet<>();
    public static ResearchNode ROOT_NODE;

    public static void initialize(Player player) {
        RESEARCHES.clear();
        NODES.clear();
        ResearchGraphCache.clearCache();

        Set<ResearchInstance> completedResearches = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeam(player.getUUID()).getResearchProgress().completedResearches();

        RegistryAccess registryAccess = player.registryAccess();
        Set<Holder<Research>> levelResearches = ResearchHelper.getLevelResearches(player.level());
        levelResearches.forEach(holder -> {
            ResearchInstance instance = getResearchByKey(completedResearches, holder.getKey());
            ResearchStatus status;
            if (instance != null) {
                status = instance.getResearchStatus();
            } else {
                status = ResearchStatus.LOCKED;
            }
            RESEARCHES.add(new ResearchInstance(holder.getKey(), status));
        });

        // Collect completedResearches
        RESEARCHES.forEach(research -> {
            if (research.getResearchStatus() == ResearchStatus.LOCKED) {
                List<ResourceKey<Research>> parents = registryAccess.holderOrThrow(research.getResearch()).value().parents();
                boolean allParentsUnlocked = true;
                for (ResourceKey<Research> parent : parents) {
                    ResearchInstance researchByKey = getResearchByKey(RESEARCHES, parent);
                    if (researchByKey.getResearchStatus() == ResearchStatus.LOCKED || researchByKey.getResearchStatus() == ResearchStatus.RESEARCHABLE) {
                        allParentsUnlocked = false;
                        break;
                    }
                }
                if (allParentsUnlocked) {
                    research.setResearchStatus(ResearchStatus.RESEARCHABLE);
                }
            }
            NODES.add(new ResearchNode(research));
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

            node.refreshHeads();
        }

        for (ResearchNode node : NODES) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(node.getInstance().getResearch(), registryAccess).parents();

            for (ResourceKey<Research> parent : parents) {
                node.addParent(getNodeByResearch(parent));
            }

            node.refreshHeads();
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

    private static ResearchInstance getResearchByKey(Collection<ResearchInstance> researches, ResourceKey<Research> researchKey) {
        for (ResearchInstance instance : researches) {
            if (instance.getResearch().compareTo(researchKey) == 0) {
                return instance;
            }
        }
        return null;
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
