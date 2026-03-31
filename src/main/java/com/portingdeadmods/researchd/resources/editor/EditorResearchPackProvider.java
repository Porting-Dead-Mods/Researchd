package com.portingdeadmods.researchd.resources.editor;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPackProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class EditorResearchPackProvider implements ResearchdResearchPackProvider {
    private final Map<ResourceKey<ResearchPack>, ResearchPack> researchPacks;

    public EditorResearchPackProvider() {
        this.researchPacks = new HashMap<>();
    }

    public void putResearchPack(ResourceKey<ResearchPack> key, ResearchPack pack) {
        this.researchPacks.put(key, pack);
    }

    @Override
    public String modid() {
        return null;
    }

    @Override
    public ResourceKey<Registry<ResearchPack>> registry() {
        return ResearchdRegistries.RESEARCH_PACK_KEY;
    }

    @Override
    public Codec<ResearchPack> codec() {
        return ResearchPack.CODEC;
    }

    @Override
    public Map<ResourceKey<ResearchPack>, ResearchPack> contents() {
        return this.researchPacks;
    }

    @Override
    public void build() {
    }
}
