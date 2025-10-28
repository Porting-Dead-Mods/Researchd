package com.portingdeadmods.researchd.api.research.packs;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface ResearchPack {
    int color();

    int sortingValue();

    Optional<ResourceLocation> customTexture();

    static Component getLangName(ResourceKey<ResearchPack> key) {
        String registryPath = ResearchdRegistries.RESEARCH_PACK_KEY.location().getPath();
        String keyNamespace = key.location().getNamespace();
        String keyPath = key.location().getPath();
        return Component.translatable(String.format("%s.%s.%s_name", registryPath, keyNamespace, keyPath));
    }

    static Component getLangDesc(ResourceKey<ResearchPack> key) {
        String registryPath = ResearchdRegistries.RESEARCH_PACK_KEY.location().getPath();
        String keyNamespace = key.location().getNamespace();
        String keyPath = key.location().getPath();
        return Component.translatable(String.format("%s.%s.%s_desc", registryPath, keyNamespace, keyPath));
    }
}
