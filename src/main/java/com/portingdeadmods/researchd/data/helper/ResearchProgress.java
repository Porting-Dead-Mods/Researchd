package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.utils.UniqueArray;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record ResearchProgress(
        ResearchQueue researchQueue,
        UniqueArray<ResearchInstance> completedResearches
) {
    public static final ResearchProgress EMPTY = new ResearchProgress(
            new ResearchQueue(),
            new UniqueArray<>()
    );

    public static final Codec<ResearchProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchQueue.CODEC.fieldOf("researchQueue").forGetter(ResearchProgress::researchQueue),
            UniqueArray.CODEC(ResearchInstance.CODEC).fieldOf("completedResearches")
                    .forGetter(ResearchProgress::completedResearches)
    ).apply(instance, ResearchProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchProgress> STREAM_CODEC = StreamCodec.composite(
            ResearchQueue.STREAM_CODEC,
            ResearchProgress::researchQueue,
            UniqueArray.STREAM_CODEC(ResearchInstance.STREAM_CODEC),
            ResearchProgress::completedResearches,
            ResearchProgress::new
    );

    // Helper methods
    public boolean hasCompleted(ResourceKey<Research> research) {
        return completedResearches.stream()
                .anyMatch(r -> r.getResearch().equals(research) &&
                        r.getResearchStatus() == ResearchStatus.RESEARCHED);
    }

    public void completeResearch(ResearchInstance research) {
        completedResearches.add(research);
    }
}
