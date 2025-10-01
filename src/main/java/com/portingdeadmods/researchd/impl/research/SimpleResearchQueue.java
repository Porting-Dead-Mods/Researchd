package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.ResearchdCommonConfig;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.team.ResearchQueue;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public record SimpleResearchQueue(List<ResourceKey<Research>> entries) implements ResearchQueue {
    public static final SimpleResearchQueue EMPTY = new SimpleResearchQueue(new ArrayList<>());
    public static final Codec<SimpleResearchQueue> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Research.RESOURCE_KEY_CODEC.listOf().fieldOf("entries").forGetter(SimpleResearchQueue::entries)
    ).apply(inst, SimpleResearchQueue::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleResearchQueue> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC.apply(ByteBufCodecs.list()),
            SimpleResearchQueue::entries,
            SimpleResearchQueue::new
    );
    public static final IntSupplier QUEUE_LENGTH = () -> ResearchdCommonConfig.researchQueueLength;

    public SimpleResearchQueue(List<ResourceKey<Research>> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public SimpleResearchQueue() {
        this(new ArrayList<>(QUEUE_LENGTH.getAsInt()));
    }

    /**
     * @param researchInstance the instance that should be added to the queue
     * @return whether it was possible to add the element to the queue
     */
    public boolean add(ResearchInstance researchInstance) {
        if (researchInstance.getResearchStatus() == ResearchStatus.RESEARCHED) return false;

        for (ResourceKey<Research> instance : this.entries) {
            if (instance.equals(researchInstance.getKey())) return false;
        }

        if (this.entries.size() < QUEUE_LENGTH.getAsInt()) {
            this.entries.add(researchInstance.getKey());
            return true;
        }
        return false;
    }

    /**
     * @param researchKey the element to be removed
     * @return whether it was possible to remove the element
     */
    public boolean remove(ResourceKey<Research> researchKey, boolean removeChildren) {
        return this.remove(this.entries.indexOf(researchKey), removeChildren);
    }

    /**
     * @param index of the element to be removed
     * @return whether it was possible to remove the element
     */
    public boolean remove(int index, boolean removeChildren) {
        if (this.entries.size() > index && index >= 0) {
            if (removeChildren)
                for (ResourceKey<Research> child : CommonResearchCache.allChildrenOf(this.entries.get(index)).stream().map(GlobalResearch::getResearchKey).toList()) {
                    this.remove(this.entries.indexOf(child), true);
                }

            for (int i = index; i < this.entries.size(); i++) {
                if (i + 1 < this.entries.size()) {
                    this.entries.set(i, this.entries.get(i + 1));
                }
            }
            this.entries.removeLast();
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(ResourceKey<Research> research) {
        return this.entries.contains(research);
    }

    @Override
    public ResourceKey<Research> get(int index) {
        return this.entries.get(index);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public ResourceKey<Research> getFirst() {
        return this.entries.isEmpty() ? null : this.entries.getFirst();
    }

    public @Nullable ResourceKey<Research> current() {
        return (this.entries.isEmpty() ? null : this.entries.getFirst());
    }

    @Override
    public int size() {
        return this.entries.size();
    }
}
