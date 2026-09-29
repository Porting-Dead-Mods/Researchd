package com.portingdeadmods.researchd.api.research;

import java.util.Collection;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ResearchManager {
    /* Research General */
    List<ResourceKey<Research>> getResearches();

    Research lookupResearch(ResourceKey<Research> key, Level level);

    /* Research Relations */

    ResearchRelations getRelationsForResearch(ResourceKey<Research> researchKey);

    /* Research Pages */

    List<Identifier> getPageIds();

    List<ResourceKey<Research>> getRootsForPage(Identifier pageId);

    ResearchPage getPageForId(Identifier pageId);

    default ResearchPage getPageByResearch(ResourceKey<Research> research) {
        Collection<Identifier> pageIds = this.getPageIds();
        for (Identifier pageId : pageIds) {
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
