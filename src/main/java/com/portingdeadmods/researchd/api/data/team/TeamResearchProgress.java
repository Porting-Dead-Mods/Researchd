package com.portingdeadmods.researchd.api.data.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.data.ResearchQueue;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public record TeamResearchProgress(
        ResearchQueue researchQueue,
        HashMap<ResourceKey<Research>, ResearchInstance> researches,
        HashMap<ResourceKey<Research>, List<ResearchMethodProgress>> progress
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
            Codec.unboundedMap(Research.RESOURCE_KEY_CODEC, ResearchMethodProgress.CODEC.listOf()).xmap(HashMap::new, Function.identity()).fieldOf("completionProgress")
                    .forGetter(TeamResearchProgress::progress)
    ).apply(instance, TeamResearchProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeamResearchProgress> STREAM_CODEC = StreamCodec.composite(
            ResearchQueue.STREAM_CODEC,
            TeamResearchProgress::researchQueue,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchInstance.STREAM_CODEC),
            TeamResearchProgress::researches,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchMethodProgress.STREAM_CODEC.apply(ByteBufCodecs.list())),
            TeamResearchProgress::progress,
            TeamResearchProgress::new
    );

    // Helper methods
    public boolean hasCompleted(ResourceKey<Research> research) {
        return this.researches.get(research).getResearchStatus() == ResearchStatus.RESEARCHED;
    }

    /**
     * Gets the root progress of a research a.k.a. the one that dictates if the research is complete or not.
     */
    public @Nullable ResearchMethodProgress getRootProgress(ResourceKey<Research> research) {
        if (!this.progress.containsKey(research)) return null;
        List<ResearchMethodProgress> progressList = this.progress.get(research);
        ResearchMethodProgress rmp = progressList.getFirst();

        while (rmp.getParent() != null) {
            rmp = rmp.getParent();
        }

        return rmp;
    }

    /**
     * Filters out all the complete and And/Or methods.
     *
     * @return A list of all the valid (non Or/And) methods available for the current research. Null if no current research.
     */
    public @Nullable List<ResearchMethodProgress> getAllValidMethodProgress() {
        if (currentResearch() == null) return null;
        List<ResearchMethodProgress> progressList = this.progress().get(this.currentResearch());

        progressList = new ArrayList<>(progressList.parallelStream().filter(rmp -> {
            if (rmp.isComplete()) return false;
            if (rmp.getMethod() instanceof OrResearchMethod || rmp.getMethod() instanceof AndResearchMethod) return false;

            ResearchMethodProgress parent = rmp.getParent();

            while (parent != null) {
                if (parent.isComplete()) {
                    return false;
                }
                parent = parent.getParent();
            }
            return true;
        }).toList());

        return progressList;
    }

    /**
     * ! Filtering by {@link com.portingdeadmods.researchd.impl.research.method.AndResearchMethod} or {@link com.portingdeadmods.researchd.impl.research.method.OrResearchMethod} return an empty list !
     *
     * @param clazz The class of ResearchMethod to filter by
     * @return {@link #getAllValidMethodProgress()} filtered by the given class. Null if no current research.
     */
    public @Nullable <T extends ResearchMethod> List<ResearchMethodProgress> getAllValidMethodProgress(Class<T> clazz) {
        List<ResearchMethodProgress> progressList = this.getAllValidMethodProgress();
        if (progressList == null) return null;

        progressList.removeIf(p -> !clazz.isInstance(p.getMethod()));
        return progressList;
    }

    public @Nullable ResourceKey<Research> currentResearch() {
        return this.researchQueue.current();
    }

    public void refreshResearchStatus(Level level) {
        for (ResearchInstance instance : this.researches().values()) {
            if (instance.getResearchStatus() == ResearchStatus.RESEARCHED) continue;

            if (instance.getParents().stream().allMatch(parent -> this.hasCompleted(parent.getResearchKey()))) {
                instance.setResearchStatus(ResearchStatus.RESEARCHABLE);
                continue;
                }

            if (instance.getParents().stream().allMatch(parent -> {
                if (this.hasCompleted(parent.getResearchKey())) return true;
                if (this.researchQueue.getEntries().contains(parent.getResearchKey())) return true;
                return false;
            })) {
                instance.setResearchStatus(ResearchStatus.RESEARCHABLE_AFTER_QUEUE);
                continue;
            }

            instance.setResearchStatus(ResearchStatus.LOCKED);
        }

        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
    }

    public void completeResearch(ResourceKey<Research> research, long completionTime, Level level) {
        this.researches.get(research).setResearchStatus(ResearchStatus.RESEARCHED).setResearchedTime(completionTime);

        refreshResearchStatus(level);
    }
}