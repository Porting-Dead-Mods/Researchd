package com.portingdeadmods.researchd.cache;

import com.google.common.collect.ImmutableMap;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CommonResearchCache {
    public static Map<ResourceKey<Research>, GlobalResearch> globalResearches;
    public static Map<ResourceLocation, ResearchPage> researchPages;

    /**
     * Map of ResearchPage id to list of root nodes (GlobalResearches with no parents within that page)
     */
    public static Map<ResourceLocation, List<GlobalResearch>> pageRoots;
    @Deprecated
    public static @Nullable GlobalResearch rootResearch;

    public static void initialize(Level level) {
        CommonResearchCache.reset();

        Map<ResourceKey<Research>, Research> researchLookup = ResearchHelperCommon.getLevelResearches(level);
        Map<ResourceKey<Research>, GlobalResearch> globalResearchMap = new LinkedHashMap<>(researchLookup.size());
        // Add the researchPacks to GLOBAL_RESEARCHES
        for (ResourceKey<Research> key : researchLookup.keySet()) {
            globalResearchMap.put(key, new GlobalResearch(key));
        }

        // CHILDREN
        for (GlobalResearch research : globalResearchMap.values()) {
            Research research1 = researchLookup.get(research.getResearchKey());
            List<ResourceKey<Research>> parents = research1.parents();
            for (ResourceKey<Research> parent : parents) {
                GlobalResearch parentGlobalResearch = globalResearchMap.get(parent);
                parentGlobalResearch.getChildren().add(research);
            }
        }

        // PARENTS
        for (GlobalResearch research : globalResearchMap.values()) {
            Research research1 = researchLookup.get(research.getResearchKey());
            List<ResourceKey<Research>> parents = research1.parents();

            // Track first root research for backwards compatibility
            if (parents.isEmpty() && rootResearch == null) {
                rootResearch = research;
            }

            for (ResourceKey<Research> parent : parents) {
                research.getParents().add(globalResearchMap.get(parent));
            }
        }

        // Lock global researchLookup
        for (GlobalResearch research : globalResearchMap.values()) {
            research.lock();
        }

        globalResearches = ImmutableMap.copyOf(globalResearchMap);

        // Build research pages
        Map<ResourceLocation, UniqueArray<GlobalResearch>> pageGroups = new LinkedHashMap<>();
        for (GlobalResearch research : globalResearchMap.values()) {
            ResourceLocation pageId = resolvePage(research, researchLookup);
            pageGroups.computeIfAbsent(pageId, k -> new UniqueArray<>()).add(research);
        }

        // Build page roots map and convert page groups to ResearchPage objects
        Map<ResourceLocation, ResearchPage> pagesMap = new LinkedHashMap<>();
        Map<ResourceLocation, List<GlobalResearch>> pageRootsMap = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, UniqueArray<GlobalResearch>> entry : pageGroups.entrySet()) {
            ResourceLocation pageId = entry.getKey();
            UniqueArray<GlobalResearch> researches = entry.getValue();

            // Find all root nodes for this page (researches with no parents within this page)
            List<GlobalResearch> roots = researches.stream()
                    .filter(r -> r.getParents().isEmpty() || !researches.containsAll(r.getParents()))
                    .toList();
            pageRootsMap.put(pageId, roots);

            // Use the first root's icon as the page icon
            GlobalResearch firstRoot = roots.isEmpty() ? researches.getFirst() : roots.getFirst();
            Research firstResearchData = researchLookup.get(firstRoot.getResearchKey());
            ResearchPage page = new ResearchPage(pageId, firstResearchData.researchIcon(), firstRoot.getResearchKey(), researches);
            pagesMap.put(pageId, page);
        }

        researchPages = ImmutableMap.copyOf(pagesMap);
        pageRoots = ImmutableMap.copyOf(pageRootsMap);
    }

    private static ResourceLocation resolvePage(GlobalResearch research, Map<ResourceKey<Research>, Research> lookup) {
        Research r = lookup.get(research.getResearchKey());
        ResourceLocation pageId = r.researchPage();

        // If this is not root and has default page, inherit from parent
        if (!research.getParents().isEmpty() && pageId.equals(ResearchPage.DEFAULT_PAGE_ID)) {
	        GlobalResearch firstParent = research.getParents().stream().findFirst().get();
			ResourceLocation page = resolvePage(firstParent, lookup);

			for (GlobalResearch parent : research.getParents()) {
				if (resolvePage(parent, lookup) != page) throw new RuntimeException("Research Parent is on a different page than child");
			}

            return resolvePage(firstParent, lookup);
        }
        return pageId;
    }

	/**
	 * @return ResourceLocation of the page that contains the research, null if no page contains it
	 */
	public static @Nullable ResourceLocation rlPageOf(GlobalResearch res) {
		for (Map.Entry<ResourceLocation, ResearchPage> entry : researchPages.entrySet()) {
			if (entry.getValue().containsResearch(res)) return entry.getKey();
		}

		return null;
	}

	/**
	 * @return ResearchPage that contains the research, null if no page contains it
	 */
	public static @Nullable ResearchPage pageOf(GlobalResearch res) {
		return researchPages.get(rlPageOf(res));
	}

    private static void _collectChildren(GlobalResearch research, List<GlobalResearch> list) {
        for (GlobalResearch child : research.getChildren()) {
            list.add(child);
            if (!child.getChildren().isEmpty()) {
                _collectChildren(child, list);
            }
        }
    }

    public static List<GlobalResearch> allChildrenOf(ResourceKey<Research> key) {
        List<GlobalResearch> list = new UniqueArray<>();
        _collectChildren(globalResearches.get(key), list);

        return list;
    }

    private static void _collectParents(GlobalResearch research, List<GlobalResearch> list) {
        for (GlobalResearch parent : research.getParents()) {
            list.add(parent);
            if (!parent.getParents().isEmpty()) {
                _collectChildren(parent, list);
            }
        }
    }

    public static List<GlobalResearch> allParentsOf(ResourceKey<Research> key) {
        List<GlobalResearch> list = new UniqueArray<>();
        _collectParents(globalResearches.get(key), list);

        return list;
    }

    public static void reset() {
        if (globalResearches != null) {
            rootResearch = null;
            globalResearches = null;
            researchPages = null;
            pageRoots = null;
        }
    }
}
