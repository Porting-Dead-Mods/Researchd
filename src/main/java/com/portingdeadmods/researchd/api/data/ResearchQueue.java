package com.portingdeadmods.researchd.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.ResearchdCommonConfig;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public record ResearchQueue(List<ResourceKey<Research>> entries) {
    public static final ResearchQueue EMPTY = new ResearchQueue(new ArrayList<>());
    public static final Codec<ResearchQueue> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Research.RESOURCE_KEY_CODEC.listOf().fieldOf("entries").forGetter(ResearchQueue::getEntries)
    ).apply(inst, ResearchQueue::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchQueue> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC.apply(ByteBufCodecs.list()),
            ResearchQueue::getEntries,
            ResearchQueue::new
    );
    public static final IntSupplier QUEUE_LENGTH = () -> ResearchdCommonConfig.researchQueueLength;

    public ResearchQueue(List<ResourceKey<Research>> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public ResearchQueue() {
        this(new ArrayList<>(QUEUE_LENGTH.getAsInt()));
    }

    /**
     * @param index of the element of which the priority should be increased
     * @return whether the priority was successfully increased
     */
    public boolean increasePriority(int index) {
        if (index > 0) {
            ResourceKey<Research> research = this.entries.get(index);
            ResourceKey<Research> next = this.entries.get(index - 1);

            if (CommonResearchCache.allChildrenOf(research).stream().map(GlobalResearch::getResearchKey).toList().contains(next)) return false;

            this.entries.set(index, next);
            this.entries.set(index - 1, research);
            return true;
        }
        return false;
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
    public boolean remove(ResourceKey<Research> researchKey) {
        return this.remove(this.entries.indexOf(researchKey));
    }

    /**
     * @param index of the element to be removed
     * @return whether it was possible to remove the element
     */
    public boolean remove(int index) {
        if (this.entries.size() > index && index >= 0) {
            for (ResourceKey<Research> child : CommonResearchCache.allChildrenOf(this.entries.get(index)).stream().map(GlobalResearch::getResearchKey).toList()) {
                this.remove(this.entries.indexOf(child));
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

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public @Nullable ResourceKey<Research> current() {
        return (getEntries().isEmpty() ? null : getEntries().getFirst());
    }

    public List<ResourceKey<Research>> getEntries() {
        return entries;
    }

}
