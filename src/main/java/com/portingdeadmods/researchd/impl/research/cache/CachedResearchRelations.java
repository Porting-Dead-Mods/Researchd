package com.portingdeadmods.researchd.impl.research.cache;

import com.google.common.collect.ImmutableSet;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.Research;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Research Wrapper holding the parent-child relationship between itself and other researches
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

    public ResourceKey<Research> getResearchKey() {
        return this.research;
    }

    public Set<CachedResearchRelations> getChildren() {
        return this.children;
    }

    public Set<CachedResearchRelations> getParents() {
        return this.parents;
    }

    public void lock() {
        this.children = ImmutableSet.copyOf(this.children);
        this.parents = ImmutableSet.copyOf(this.parents);
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
