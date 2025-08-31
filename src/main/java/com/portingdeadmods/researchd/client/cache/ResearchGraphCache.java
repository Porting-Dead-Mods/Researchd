package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.data.ResearchGraph;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
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

    public static ResearchGraph computeIfAbsent(ResourceKey<Research> key) {
        return GRAPH_CACHE.computeIfAbsent(key, k -> {
            Map<ResourceKey<Research>, ResearchInstance> researches = ClientResearchTeamHelper.getTeam().getResearchProgress().researches();
            return ResearchGraph.formRootResearch(key, researches);
        });
    }

}
