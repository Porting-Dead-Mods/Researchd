package com.portingdeadmods.researchd.utils.registries;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.compat.KubeJSCompat;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

public class ReloadableRegistryManager<T> extends SimpleJsonResourceReloadListener<T> {
    public static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final HolderLookup.Provider lookup;
    private final ResourceKey<Registry<T>> registry;
    private @Nullable Map<ResourceKey<T>, T> byName;
    private boolean failed;

    public ReloadableRegistryManager(HolderLookup.Provider lookup, ResourceKey<Registry<T>> registry, Codec<T> codec) {
        super(codec, FileToIdConverter.registry(registry));
        this.lookup = lookup;
        this.registry = registry;
    }

    // TODO: Replace with linked hashmap and sort it
    @SuppressWarnings("unchecked")
    @Override
    protected void apply(
            Map<Identifier, T> registryEntries, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceKey<T>, T> builder = ImmutableMap.builder();

        for (Map.Entry<Identifier, T> entry : registryEntries.entrySet()) {
            Identifier location = entry.getKey();
            if (!location.getPath().startsWith("_")) {
                builder.put(ResourceKey.create(this.registry, location), entry.getValue());
            }
        }

        if (this.registry.equals(ResearchdRegistries.RESEARCH_KEY)) {
            Map<Identifier, Research> kubeJSResearches = KubeJSCompat.getKubeJSResearches();
            for (Map.Entry<Identifier, Research> entry : kubeJSResearches.entrySet()) {
                builder.put(ResourceKey.create(this.registry, entry.getKey()), (T) entry.getValue());
            }
            Researchd.LOGGER.info("Loaded {} KubeJS researches", kubeJSResearches.size());
        } else if (this.registry.equals(ResearchdRegistries.RESEARCH_PACK_KEY)) {
            Map<Identifier, ResearchPackImpl> kubeJSPacks = KubeJSCompat.getKubeJSResearchPacks();
            for (Map.Entry<Identifier, ResearchPackImpl> entry : kubeJSPacks.entrySet()) {
                builder.put(ResourceKey.create(this.registry, entry.getKey()), (T) entry.getValue());
            }
            Researchd.LOGGER.info("Loaded {} KubeJS Research Packs", kubeJSPacks.size());
        }

        this.byName = builder.build();
        Researchd.LOGGER.info("Loaded {} entries for registry {}", this.byName.size(), this.registry.identifier());
    }

    public void replaceContents(Map<Identifier, T> contents) {
        ImmutableMap.Builder<ResourceKey<T>, T> builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, T> entry : contents.entrySet()) {
            builder.put(ResourceKey.create(this.registry, entry.getKey()), entry.getValue());
        }
        this.byName = builder.build();
    }

    // TODO: Fire an event when this happens
    public void mergeContents(Map<Identifier, T> contents) {
        Map<ResourceKey<T>, T> newByName = new HashMap<>();
        if (this.byName != null) {
            newByName.putAll(this.byName);
        }
        for (Map.Entry<Identifier, T> entry : contents.entrySet()) {
            newByName.put(ResourceKey.create(this.registry, entry.getKey()), entry.getValue());
        }
        this.byName = ImmutableMap.copyOf(newByName);
    }

    public Map<ResourceKey<T>, T> getLookup() {
        if (byName == null) {
            return this.lookup.lookupOrThrow(this.registry).listElements().toList().stream()
                    .map(holder -> Pair.of(holder.getKey(), holder.value()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        }
        return this.byName;
    }

    public Map<Identifier, T> getByName() {
        return this.getLookup().entrySet().stream()
                .map(e -> Pair.of(e.getKey().identifier(), e.getValue()))
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
