package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodProgress;
import com.portingdeadmods.researchd.impl.research.SimpleResearchQueue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public record TeamResearches(SimpleResearchQueue researchQueue,
                             HashMap<ResourceKey<Research>, ResearchInstance> researches,
                             HashMap<ResourceKey<Research>, ResearchMethodProgress<?>> progress) {
    public static final TeamResearches EMPTY = new TeamResearches(
            new SimpleResearchQueue(),
            new HashMap<>(),
            new HashMap<>()
    );
    public static final Codec<TeamResearches> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SimpleResearchQueue.CODEC.fieldOf("researchQueue").forGetter(TeamResearches::researchQueue),
            Codec.unboundedMap(Research.RESOURCE_KEY_CODEC, ResearchInstance.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("researches")
                    .forGetter(TeamResearches::researches),
            Codec.unboundedMap(Research.RESOURCE_KEY_CODEC, ResearchMethodProgress.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("completionProgress")
                    .forGetter(TeamResearches::progress)
    ).apply(instance, TeamResearches::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TeamResearches> STREAM_CODEC = StreamCodec.composite(
            SimpleResearchQueue.STREAM_CODEC,
            TeamResearches::researchQueue,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchInstance.STREAM_CODEC),
            TeamResearches::researches,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchMethodProgress.STREAM_CODEC),
            TeamResearches::progress,
            TeamResearches::new
    );

    // Helper methods
    public boolean hasCompleted(ResourceKey<Research> research) {
        return this.researches.get(research).getResearchStatus() == ResearchStatus.RESEARCHED;
    }

    /**
     * Gets the root progress of a research
     */
    public <T extends ResearchMethod> ResearchMethodProgress<T> getProgress(ResourceKey<Research> research) {
        return (ResearchMethodProgress<T>) this.progress().get(research);
    }

    public @Nullable ResourceKey<Research> currentResearch() {
        return this.researchQueue.current();
    }

    public void refreshResearchStatus() {
        for (ResearchInstance instance : this.researches.values()) {
            if (instance.getResearchStatus() == ResearchStatus.RESEARCHED) continue;

            if (instance.getParents().stream().allMatch(parent -> this.hasCompleted(parent.getResearchKey()))) {
                instance.setResearchStatus(ResearchStatus.RESEARCHABLE);
                continue;
            }

            if (instance.getParents().stream().allMatch(parent -> {
                if (this.hasCompleted(parent.getResearchKey())) return true;
                return this.researchQueue.entries().contains(parent.getResearchKey());
            })) {
                instance.setResearchStatus(ResearchStatus.RESEARCHABLE_AFTER_QUEUE);
                continue;
            }

            instance.setResearchStatus(ResearchStatus.LOCKED);
        }

        //ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
    }

    public void completeResearch(ResourceKey<Research> research, long completionTime, Level level) {
        this.researches.get(research).setResearchStatus(ResearchStatus.RESEARCHED).setResearchedTime(completionTime);

        this.refreshResearchStatus();
    }

}