package com.portingdeadmods.researchd.api.research;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Research Wrapper holding the parent-child relationship between itself and other researches
 */
public class GlobalResearch {
    public static final Codec<GlobalResearch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Research.RESOURCE_KEY_CODEC.fieldOf("research").forGetter(GlobalResearch::getResearch)
    ).apply(instance, GlobalResearch::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GlobalResearch> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC,
            GlobalResearch::getResearch,
            GlobalResearch::new
    );
    private final ResourceKey<Research> research;
    private Set<GlobalResearch> children;
    private Set<GlobalResearch> parents;

    public GlobalResearch(ResourceKey<Research> research) {
        this.research = research;
        this.children = new HashSet<>();
        this.parents = new HashSet<>();
    }

    public boolean is(ResourceKey<Research> research) {
        return this.research.compareTo(research) == 0;
    }

    public ResourceKey<Research> getResearch() {
        return this.research;
    }

    public Set<GlobalResearch> getChildren() {
        return this.children;
    }

    public Set<GlobalResearch> getParents() {
        return this.parents;
    }

    public void lock() {
        this.children = new ImmutableSet.Builder<GlobalResearch>().addAll(this.children).build();
        this.parents = new ImmutableSet.Builder<GlobalResearch>().addAll(this.parents).build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlobalResearch that)) return false;
        return Objects.equals(research, that.research);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(research);
    }

    @Override
    public String toString() {
        return "GlobalResearch{" +
                "research=" + research +
                '}';
    }
}
