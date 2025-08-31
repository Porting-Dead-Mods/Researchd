package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.data.ResearchGraph;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ResearchGraphCache {
    private static final Map<ResourceKey<Research>, ResearchGraph> GRAPH_CACHE = new LinkedHashMap<>();

    public static void add(ResourceKey<Research> key, ResearchGraph graph) {
        GRAPH_CACHE.put(key, graph);
    }

    public static @Nullable ResearchGraph get(ResourceKey<Research> key) {
        return GRAPH_CACHE.get(key);
    }

    public static void clearCache() {
        GRAPH_CACHE.clear();
    }

    public static ResearchGraph computeIfAbsent(Player player, ResourceKey<Research> key) {
        return GRAPH_CACHE.computeIfAbsent(key, k -> computeGraph(player, k));
    }

    private static @NotNull ResearchGraph computeGraph(Player player, ResourceKey<Research> key) {
        return ResearchGraph.formRootResearch(player, ResearchHelperCommon.getInstanceByResearch(ClientResearchCache.GLOBAL_READ_ONLY_RESEARCHES, key));
    }

}
