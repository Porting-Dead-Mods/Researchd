package com.portingdeadmods.researchd.api.data.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.data.ResearchQueue;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.data.helper.ResearchCompletionProgress;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.function.Function;

public record TeamResearchProgress(
        ResearchQueue researchQueue,
        HashMap<ResourceKey<Research>, ResearchInstance> researches,
        HashMap<ResourceKey<Research>, ResearchCompletionProgress> progress
) {
    public static final TeamResearchProgress EMPTY = new TeamResearchProgress(
            new ResearchQueue(),
            new HashMap<>(),
            new HashMap<>()
    );

    public static final Codec<TeamResearchProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchQueue.CODEC.fieldOf("researchQueue").forGetter(TeamResearchProgress::researchQueue),
            Codec.unboundedMap(Research.RESOURCE_KEY_CODEC, ResearchInstance.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("researches")
                    .forGetter(TeamResearchProgress::researches),
            Codec.unboundedMap(Research.RESOURCE_KEY_CODEC, ResearchCompletionProgress.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("completionProgress")
                    .forGetter(TeamResearchProgress::progress)
    ).apply(instance, TeamResearchProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeamResearchProgress> STREAM_CODEC = StreamCodec.composite(
            ResearchQueue.STREAM_CODEC,
            TeamResearchProgress::researchQueue,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchInstance.STREAM_CODEC),
            TeamResearchProgress::researches,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchCompletionProgress.STREAM_CODEC),
            TeamResearchProgress::progress,
            TeamResearchProgress::new
    );

    // Helper methods
    public boolean hasCompleted(ResourceKey<Research> research) {
        return this.researches.get(research).getResearchStatus() == ResearchStatus.RESEARCHED;
    }

    public ResearchCompletionProgress getProgress(ResourceKey<Research> key, HolderLookup.Provider provider) {
        return progress().computeIfAbsent(key, r -> provider.holderOrThrow(r).value().researchMethod().getDefaultProgress());
    }

    public void completeResearch(ResearchInstance research) {
        this.researches.get(research.getKey()).setResearchStatus(ResearchStatus.RESEARCHED);
    }
}
