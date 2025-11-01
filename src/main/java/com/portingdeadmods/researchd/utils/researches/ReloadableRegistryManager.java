package com.portingdeadmods.researchd.utils.researches;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.compat.KubeJSCompat;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class ReloadableRegistryManager<T> extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final HolderLookup.Provider lookup;
    private final ResourceKey<Registry<T>> registry;
    private final Codec<T> codec;
    private Map<ResourceKey<T>, T> byName;
    private boolean failed;

    public ReloadableRegistryManager(HolderLookup.Provider lookup, ResourceKey<Registry<T>> registry, Codec<T> codec) {
        super(GSON, Registries.elementsDirPath(registry));
        this.lookup = lookup;
        this.registry = registry;
        this.codec = codec;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> registryEntries, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceKey<T>, T> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : registryEntries.entrySet()) {
            ResourceLocation location = entry.getKey();
            if (!location.getPath().startsWith("_")) {
                try {
                    DataResult<Pair<T, JsonElement>> result = this.codec.decode(this.makeConditionalOps(), entry.getValue());
                    ResourceKey<T> key = ResourceKey.create(this.registry, location);
                    result.ifSuccess(pair -> {
                        builder.put(key, pair.getFirst());
                    });
                } catch (Exception e) {
                    Researchd.LOGGER.error("Parsing error loading registry entry {}", location, e);
                }
            }
        }

        if (this.registry.equals(ResearchdRegistries.RESEARCH_KEY)) {
            Map<ResourceLocation, Research> kubeJSResearches = KubeJSCompat.getKubeJSResearches();
            for (Map.Entry<ResourceLocation, Research> entry : kubeJSResearches.entrySet()) {
                ResourceKey<T> key = ResourceKey.create(this.registry, entry.getKey());
                builder.put(key, (T) entry.getValue());
            }
            Researchd.LOGGER.info("Loaded {} KubeJS researches", kubeJSResearches.size());
        } else if (this.registry.equals(ResearchdRegistries.RESEARCH_PACK_KEY)) {
            Map<ResourceLocation, ResearchPackImpl> kubeJSPacks = KubeJSCompat.getKubeJSResearchPacks();
            for (Map.Entry<ResourceLocation, ResearchPackImpl> entry : kubeJSPacks.entrySet()) {
                ResourceKey<T> key = ResourceKey.create(this.registry, entry.getKey());
                builder.put(key, (T) entry.getValue());
            }
            Researchd.LOGGER.info("Loaded {} KubeJS research packs", kubeJSPacks.size());
        }

        this.byName = builder.build();
        Researchd.LOGGER.info("Loaded {} entries for registry {}", this.byName.size(), this.registry.location());
    }

    public void replaceContents(Map<ResourceLocation, T> contents) {
        ImmutableMap.Builder<ResourceKey<T>, T> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, T> entry : contents.entrySet()) {
            builder.put(ResourceKey.create(this.registry, entry.getKey()), entry.getValue());
        }
        this.byName = builder.build();
    }

    public Map<ResourceKey<T>, T> getLookup() {
        if (byName == null) {
            return this.lookup.lookupOrThrow(this.registry).listElements().toList().stream()
                    .map(holder -> Pair.of(holder.getKey(), holder.value()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        }
        return this.byName;
    }

    public Map<ResourceLocation, T> getByName() {
        return this.byName.entrySet().stream()
                .map(e -> Pair.of(e.getKey().location(), e.getValue()))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public boolean isFailed() {
        return failed;
    }

    public void fail() {
        this.byName = Collections.emptyMap();
        this.failed = true;
    }

}
