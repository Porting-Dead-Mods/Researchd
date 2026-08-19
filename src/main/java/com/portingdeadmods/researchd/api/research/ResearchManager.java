package com.portingdeadmods.researchd.api.research;

import java.util.Collection;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface ResearchManager {
    /* Research General */
    List<ResourceKey<Research>> getResearches();

    Research lookupResearch(ResourceKey<Research> key, Level level);

    /* Research Relations */

    ResearchRelations getRelationsForResearch(ResourceKey<Research> researchKey);

    /* Research Pages */

    List<ResourceLocation> getPageIds();

    List<ResourceKey<Research>> getRootsForPage(ResourceLocation pageId);

    ResearchPage getPageForId(ResourceLocation pageId);

    default ResearchPage getPageByResearch(ResourceKey<Research> research) {
        Collection<ResourceLocation> pageIds = this.getPageIds();
        for (ResourceLocation pageId : pageIds) {
            ResearchPage page = this.getPageForId(pageId);
            if (page != null && page.containsResearch(research)) {
                return page;
            }
        }
        return null;
    }

    default boolean isPageRoot(ResourceKey<Research> research) {
        ResearchPage page = this.getPageByResearch(research);
        if (page == null) return false;

        List<ResourceKey<Research>> roots = this.getRootsForPage(page.id());
        return roots != null && roots.contains(research);
    }
}
