package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ResearchGraphCache {
    private static final Map<ResourceKey<Research>, ResearchGraph> GRAPH_CACHE = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ResearchGraph> PAGE_GRAPH_CACHE = new LinkedHashMap<>();

    public static void add(ResourceKey<Research> key, ResearchGraph graph) {
        GRAPH_CACHE.put(key, graph);
    }

    public static @Nullable ResearchGraph get(ResourceKey<Research> key) {
        return GRAPH_CACHE.get(key);
    }

    public static @Nullable ResearchGraph getForPage(ResourceLocation pageId) {
        return PAGE_GRAPH_CACHE.get(pageId);
    }

	public static List<ResearchGraph> getAll() { return new UniqueArray<>(GRAPH_CACHE.values()); }

    public static void clearCache() {
        GRAPH_CACHE.clear();
        PAGE_GRAPH_CACHE.clear();
    }

    public static ResearchGraph computeIfAbsent(ResourceKey<Research> key)  {
        return GRAPH_CACHE.computeIfAbsent(key, k -> ResearchGraph.fromRootResearch(key, ClientResearchTeamHelper.getTeam().getResearches()));
    }

    /**
     * Computes or retrieves a cached ResearchGraph for the given ResearchPage.
     * Currently uses the first root node of the page for graph generation.
     */
    public static ResearchGraph computeIfAbsentForPage(ResearchPage page) {
        return PAGE_GRAPH_CACHE.computeIfAbsent(page.id(), pageId -> {
            List<GlobalResearch> roots = CommonResearchCache.pageRoots.get(pageId);
            if (roots != null && !roots.isEmpty()) {
                // TODO: Parse
                return ResearchGraph.fromResearchPage(page, roots.getFirst(), ClientResearchTeamHelper.getTeam().getResearches());
            }
            return null;
        });
    }

}
