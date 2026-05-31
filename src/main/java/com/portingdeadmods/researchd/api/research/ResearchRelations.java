package com.portingdeadmods.researchd.api.research;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceKey;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Research Wrapper holding the parent-child relationship between itself and other researches
 */
public final class ResearchRelations {
    private final ResourceKey<Research> research;
    private Set<ResearchRelations> children;
    private Set<ResearchRelations> parents;

    public ResearchRelations(ResourceKey<Research> research) {
        this.research = research;
        this.children = new HashSet<>();
        this.parents = new HashSet<>();
    }

    public ResourceKey<Research> getResearchKey() {
        return this.research;
    }

    public Set<ResearchRelations> getChildren() {
        return this.children;
    }

    public Set<ResearchRelations> getParents() {
        return this.parents;
    }

    public void lock() {
        this.children = ImmutableSet.copyOf(this.children);
        this.parents = ImmutableSet.copyOf(this.parents);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResearchRelations that)) return false;
        return Objects.equals(research, that.research);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(research);
    }
}
