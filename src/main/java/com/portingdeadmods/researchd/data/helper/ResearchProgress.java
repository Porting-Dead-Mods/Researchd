package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.impl.research.ResearchCompletionProgress;
import com.portingdeadmods.researchd.utils.UniqueArray;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public record ResearchProgress(
        ResearchQueue researchQueue,
        UniqueArray<ResearchInstance> completedResearches,
        Map<ResearchInstance, ResearchCompletionProgress> progress
) {
    public static final ResearchProgress EMPTY = new ResearchProgress(
            new ResearchQueue(),
            new UniqueArray<>(),
            new HashMap<>()
    );

    public static final Codec<ResearchProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchQueue.CODEC.fieldOf("researchQueue").forGetter(ResearchProgress::researchQueue),
            UniqueArray.CODEC(ResearchInstance.CODEC).fieldOf("completedResearches")
                    .forGetter(ResearchProgress::completedResearches),
            Codec.unboundedMap(ResearchInstance.CODEC, ResearchCompletionProgress.CODEC).fieldOf("completionProgress")
                    .forGetter(ResearchProgress::progress)
    ).apply(instance, ResearchProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchProgress> STREAM_CODEC = StreamCodec.composite(
            ResearchQueue.STREAM_CODEC,
            ResearchProgress::researchQueue,
            UniqueArray.STREAM_CODEC(ResearchInstance.STREAM_CODEC),
            ResearchProgress::completedResearches,
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResearchInstance.STREAM_CODEC, ResearchCompletionProgress.STREAM_CODEC),
            ResearchProgress::progress,
            ResearchProgress::new
    );

    // Helper methods
    public boolean hasCompleted(ResourceKey<Research> research) {
        return completedResearches.stream()
                .anyMatch(r -> r.equals(research));
    }

    public @Nullable ResearchCompletionProgress tryGetProgress(ResourceKey<Research> research) {
        return progress().get(ResearchHelper.getInstanceByResearch(progress.keySet(), research));
    }

    public @Nullable ResearchCompletionProgress tryGetProgress(ResearchInstance researchInstance) {
        return tryGetProgress(researchInstance.getResearch());
    }

    public ResearchCompletionProgress getProgress(ResearchInstance research, HolderLookup.Provider provider) {
        return progress().computeIfAbsent(research, r -> ResearchHelper.getResearch(research.getResearch(), provider).researchMethod().getDefaultProgress());
    }

    public void completeResearch(ResearchInstance research) {
        completedResearches.add(research);
    }
}
