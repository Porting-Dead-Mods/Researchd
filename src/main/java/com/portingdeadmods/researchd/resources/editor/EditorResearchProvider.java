package com.portingdeadmods.researchd.resources.editor;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class EditorResearchProvider implements ResearchdResearchProvider {
    private final Map<ResourceKey<Research>, Research> researches;

    public EditorResearchProvider() {
        this.researches = new HashMap<>();
    }

    public void putResearch(ResourceKey<Research> key, Research research) {
        this.researches.put(key, research);
    }

    @Override
    public void build() {
    }

    @Override
    public String modid() {
        return null;
    }

    @Override
    public ResourceKey<Registry<Research>> registry() {
        return ResearchdRegistries.RESEARCH_KEY;
    }

    @Override
    public Codec<Research> codec() {
        return Research.CODEC;
    }

    @Override
    public Map<ResourceKey<Research>, Research> contents() {
        return this.researches;
    }

}
