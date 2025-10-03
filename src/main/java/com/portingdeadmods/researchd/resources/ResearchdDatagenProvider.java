package com.portingdeadmods.researchd.resources;

import net.minecraft.resources.ResourceKey;

import java.util.Map;

public interface ResearchdDatagenProvider<T> {
    void build();

    Map<ResourceKey<T>, T> getContents();
}
