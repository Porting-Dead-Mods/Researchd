package com.portingdeadmods.researchd.impl.research.cache;

import com.google.common.collect.ImmutableSet;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Research Wrapper holding the parent-child relationship between itself and other researchPacks
 */
public final class CachedResearchRelations {
    private final ResourceKey<Research> research;
    private Set<CachedResearchRelations> children;
    private Set<CachedResearchRelations> parents;

    public CachedResearchRelations(ResourceKey<Research> research) {
        this.research = research;
        this.children = new HashSet<>();
        this.parents = new HashSet<>();
    }

    public boolean is(ResourceKey<Research> research) {
        return this.research.compareTo(research) == 0;
    }

    public ResourceKey<Research> getResearchKey() {
        return this.research;
    }

    public Research getResearch(Level level) {
        return ResearchHelperCommon.getResearch(this.getResearchKey(), level);
    }

    public Set<CachedResearchRelations> getChildren() {
        return this.children;
    }

    public Set<CachedResearchRelations> getParents() {
        return this.parents;
    }

    public void lock() {
        this.children = new ImmutableSet.Builder<CachedResearchRelations>().addAll(this.children).build();
        this.parents = new ImmutableSet.Builder<CachedResearchRelations>().addAll(this.parents).build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CachedResearchRelations that)) return false;
        return Objects.equals(research, that.research);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(research);
    }
}
