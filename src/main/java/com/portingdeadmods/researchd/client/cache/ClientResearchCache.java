package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.utils.ImmutableLinkedHashSet;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public final class ClientResearchCache {
    public static final Set<ResearchNode> NODES = new LinkedHashSet<>();
    public static Set<ResearchInstance> GLOBAL_READ_ONLY_RESEARCHES;
    public static ResearchNode ROOT_NODE;

    public static void initialize(Player player) {
        NODES.clear();
        GLOBAL_READ_ONLY_RESEARCHES = new LinkedHashSet<>();
        ResearchGraphCache.clearCache();

        Set<ResearchInstance> completedResearches = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByPlayer(player).getResearchProgress().completedResearches();

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
            GLOBAL_READ_ONLY_RESEARCHES.add(new ResearchInstance(holder.getKey(), status));
        });

        // Collect completedResearches
        GLOBAL_READ_ONLY_RESEARCHES.forEach(research -> {
            if (research.getResearchStatus() == ResearchStatus.LOCKED) {
                List<ResourceKey<Research>> parents = registryAccess.holderOrThrow(research.getResearch()).value().parents();
                boolean allParentsUnlocked = true;
                for (ResourceKey<Research> parent : parents) {
                    ResearchInstance researchByKey = getResearchByKey(GLOBAL_READ_ONLY_RESEARCHES, parent);
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
        for (ResearchInstance instance : GLOBAL_READ_ONLY_RESEARCHES) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(instance.getResearch(), registryAccess).parents();

            for (ResourceKey<Research> parentResearch : parents) {
                ResearchInstance parentInstance = getResearchByKey(GLOBAL_READ_ONLY_RESEARCHES, parentResearch);
                if (parentInstance != null) {
                    parentInstance.getChildren().add(instance);
                }
            }
        }

        for (ResearchInstance instance : GLOBAL_READ_ONLY_RESEARCHES) {
            List<ResourceKey<Research>> parents = ResearchHelper.getResearch(instance.getResearch(), registryAccess).parents();

            for (ResourceKey<Research> parent : parents) {
                instance.getParents().add(getResearchByKey(GLOBAL_READ_ONLY_RESEARCHES, parent));
            }
        }

        for (ResearchInstance research : GLOBAL_READ_ONLY_RESEARCHES) {
            NODES.add(new ResearchNode(research));
        }

        for (ResearchNode node : NODES) {
            for (ResearchInstance parent : node.getInstance().getParents()) {
                node.addParent(getNodeByResearch(NODES, parent.getResearch()));
            }

            for (ResearchInstance child : node.getInstance().getChildren()) {
                node.addChild(getNodeByResearch(NODES, child.getResearch()));
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

        GLOBAL_READ_ONLY_RESEARCHES = new ImmutableLinkedHashSet<>(GLOBAL_READ_ONLY_RESEARCHES);
    }

    private static ResearchInstance getResearchByKey(Collection<ResearchInstance> researches, ResourceKey<Research> researchKey) {
        for (ResearchInstance instance : researches) {
            if (instance.getResearch().compareTo(researchKey) == 0) {
                return instance;
            }
        }
        return null;
    }

    public static ResearchNode getNodeByResearch(Collection<ResearchNode> nodes, ResourceKey<Research> research) {
        for (ResearchNode node : nodes) {
            if (node.getInstance().getResearch().compareTo(research) == 0) {
                return node;
            }
        }
        return null;
    }
}
