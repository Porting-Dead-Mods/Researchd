package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class ResearchGraphCache {
    private static final Map<ResourceKey<Research>, ResearchGraph> GRAPH_CACHE = new HashMap<>();

    public static void add(ResourceKey<Research> key, ResearchGraph graph) {
        GRAPH_CACHE.put(key, graph);
    }

    public static @Nullable ResearchGraph get(ResourceKey<Research> key) {
        return GRAPH_CACHE.get(key);
    }

    public static ResearchGraph computeIfAbsent(Player player, ResourceKey<Research> key) {
        return GRAPH_CACHE.computeIfAbsent(key, k -> computeGraph(player, k));
    }

    private static @NotNull ResearchGraph computeGraph(Player player, ResourceKey<Research> key) {
        Set<ResearchInstance> playerResearches = ResearchHelper.getPlayerResearches(player);
        ResearchInstance instance = ResearchHelper.getInstanceByResearch(playerResearches, key);
        return ResearchGraph.fromRootNode(player, new ResearchNode(instance));
    }

}
