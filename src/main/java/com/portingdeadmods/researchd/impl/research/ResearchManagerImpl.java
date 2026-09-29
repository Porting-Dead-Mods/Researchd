package com.portingdeadmods.researchd.impl.research;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.api.research.ResearchRelations;
import com.portingdeadmods.researchd.utils.registries.ResearchdManagers;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ResearchManagerImpl implements ResearchManager {
    private static ResearchManagerImpl instance;

    private List<ResourceKey<Research>> researches = ImmutableList.of();
    private List<Identifier> pageIds = ImmutableList.of();
    private Map<ResourceKey<Research>, ResearchRelations> researchRelations = ImmutableMap.of();
    private Map<Identifier, ResearchPage> researchPages = ImmutableMap.of();

    /**
     * Map of ResearchPage id to list of root nodes (GlobalResearches with no parents within that page)
     */
    private Map<Identifier, List<ResourceKey<Research>>> pageRoots;

    @Deprecated
    private @Nullable ResearchRelations rootResearch;

    private ResearchManagerImpl() {}

    public static void setNewInstance(Level level) {
        instance = new ResearchManagerImpl();
        instance.initialize(level);
    }

    public static void reset() {
        instance = null;
    }

    // TODO: Use Hashmaps?
    private void initialize(Level level) {
        Map<ResourceKey<Research>, Research> researchLookup =
                ResearchdManagers.getResearchesManager(level).getLookup();
        Map<ResourceKey<Research>, ResearchRelations> globalResearchMap = new LinkedHashMap<>(researchLookup.size());

        this.researches = ImmutableList.copyOf(researchLookup.keySet());

        // Add the researchPacks to GLOBAL_RESEARCHES
        for (ResourceKey<Research> key : this.researches) {
            globalResearchMap.put(key, new ResearchRelations(key));
        }

        // CHILDREN
        for (ResearchRelations research : globalResearchMap.values()) {
            Research research1 = researchLookup.get(research.getResearchKey());
            List<ResourceKey<Research>> parents = research1.parents();
            for (ResourceKey<Research> parent : parents) {
                ResearchRelations parentResearchRelations = globalResearchMap.get(parent);

                if (parentResearchRelations == null) {
                    Researchd.error(
                            "Research Manager",
                            "Research %s has a parent %s that does not exist. It will be treated as if it had no such parent.",
                            research.getResearchKey().identifier(),
                            parent.identifier());
                    continue;
                }

                parentResearchRelations.getChildren().add(research);
            }
        }

        // PARENTS
        for (ResearchRelations research : globalResearchMap.values()) {
            Research research1 = researchLookup.get(research.getResearchKey());
            List<ResourceKey<Research>> parents = research1.parents();

            // Track first root researchPack for backwards compatibility
            if (parents.isEmpty() && rootResearch == null) {
                rootResearch = research;
            }

            for (ResourceKey<Research> parent : parents) {
                ResearchRelations parentRelations = globalResearchMap.get(parent);
                if (parentRelations == null) continue;

                research.getParents().add(parentRelations);
            }
        }

        // Lock global researchLookup
        for (ResearchRelations research : globalResearchMap.values()) {
            research.lock();
        }

        researchRelations = ImmutableMap.copyOf(globalResearchMap);

        // Build research pages
        Map<Identifier, UniqueArray<ResearchRelations>> pageGroups = new LinkedHashMap<>();
        for (ResearchRelations research : globalResearchMap.values()) {
            Identifier pageId = resolvePage(research, researchLookup);
            pageGroups.computeIfAbsent(pageId, k -> new UniqueArray<>()).add(research);
        }

        // Build page roots map and convert page groups to ResearchPage objects
        Map<Identifier, ResearchPage> pagesMap = new LinkedHashMap<>();
        Map<Identifier, List<ResourceKey<Research>>> pageRootsMap = new LinkedHashMap<>();

        for (Map.Entry<Identifier, UniqueArray<ResearchRelations>> entry : pageGroups.entrySet()) {
            Identifier pageId = entry.getKey();
            UniqueArray<ResearchRelations> researches = entry.getValue();

            // Find all root nodes for this page (researches with no parents within this page)
            List<ResourceKey<Research>> roots = researches.stream()
                    .filter(r -> r.getParents().isEmpty() || !researches.containsAll(r.getParents()))
                    .map(ResearchRelations::getResearchKey)
                    .toList();
            pageRootsMap.put(pageId, roots);

            // Use the first root's icon as the page icon
            ResourceKey<Research> firstRoot =
                    roots.isEmpty() ? researches.getFirst().getResearchKey() : roots.getFirst();
            Research firstResearchData = researchLookup.get(firstRoot);
            UniqueArray<ResourceKey<Research>> researchKeys = new UniqueArray<>(
                    researches.stream().map(ResearchRelations::getResearchKey).toList());
            ResearchPage page = new ResearchPage(pageId, firstResearchData.researchIcon(), firstRoot, researchKeys);
            pagesMap.put(pageId, page);
        }

        this.researchPages = ImmutableMap.copyOf(pagesMap);
        this.pageIds = ImmutableList.copyOf(researchPages.keySet());
        this.pageRoots = ImmutableMap.copyOf(pageRootsMap);
    }

    private static Identifier resolvePage(ResearchRelations research, Map<ResourceKey<Research>, Research> lookup) {
        Research r = lookup.get(research.getResearchKey());
        if (r == null) return ResearchPage.DEFAULT_PAGE_ID;

        Identifier pageId = r.researchPage();

        // If this is not root and has default page, inherit from parent
        if (!research.getParents().isEmpty() && pageId.equals(ResearchPage.DEFAULT_PAGE_ID)) {
            ResearchRelations firstParent =
                    research.getParents().stream().findFirst().get();
            Identifier page = resolvePage(firstParent, lookup);

            for (ResearchRelations parent : research.getParents()) {
                Identifier parentPage = resolvePage(parent, lookup);
                if (!parentPage.equals(page)) {
                    Researchd.error(
                            "Research Manager",
                            "Research %s has parents on different pages (%s and %s), using %s.",
                            research.getResearchKey().identifier(),
                            page,
                            parentPage,
                            page);
                }
            }

            return page;
        }
        return pageId;
    }

    /* Research General */

    @Override
    public List<ResourceKey<Research>> getResearches() {
        return researches;
    }

    @Override
    public Research lookupResearch(ResourceKey<Research> key, Level level) {
        return ResearchdManagers.getResearchesManager(level).getLookup().get(key);
    }

    /* Research Relations */

    @Override
    public ResearchRelations getRelationsForResearch(ResourceKey<Research> researchKey) {
        return this.researchRelations.get(researchKey);
    }

    /* Research Pages */

    @Override
    public List<Identifier> getPageIds() {
        return pageIds;
    }

    @Override
    public List<ResourceKey<Research>> getRootsForPage(Identifier pageId) {
        return this.pageRoots.get(pageId);
    }

    @Override
    public ResearchPage getPageForId(Identifier pageId) {
        return this.researchPages.get(pageId);
    }

    public static ResearchManagerImpl getInstance() {
        return instance;
    }
}
