package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.impl.research.cache.CachedResearchRelations;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public interface ResearchManager {
    /* Research General */
    List<ResourceKey<Research>> getResearches();

    Research lookupResearch(ResourceKey<Research> key, Level level);

    /* Research Relations */

    CachedResearchRelations getRelationsForResearch(ResourceKey<Research> researchKey);

    /* Research Pages */

    List<ResourceLocation> getPageIds();

    List<ResourceKey<Research>> getRootsForPage(ResourceLocation pageId);

    ResearchPage getPageForId(ResourceLocation pageId);

    default ResearchPage getPageByResearch(ResourceKey<Research> research) {
        Collection<ResourceLocation> pageIds = this.getPageIds();
        for (ResourceLocation pageId : pageIds) {
            ResearchPage page = this.getPageForId(pageId);
            if (page.containsResearch(research)) {
                return page;
            }
        }
        return null;
    }
}
