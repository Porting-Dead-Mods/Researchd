package com.portingdeadmods.researchd.api.data.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.data.ResearchProgressPacket;
import com.portingdeadmods.researchd.api.data.ResearchQueue;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public record TeamResearchProgress(
        ResearchQueue researchQueue,
        HashMap<ResourceKey<Research>, ResearchInstance> researches,
        HashMap<ResourceKey<Research>, ResearchMethodProgress> progress
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
            Codec.unboundedMap(Research.RESOURCE_KEY_CODEC, ResearchMethodProgress.CODEC).xmap(HashMap::new, Function.identity()).fieldOf("completionProgress")
                    .forGetter(TeamResearchProgress::progress)
    ).apply(instance, TeamResearchProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeamResearchProgress> STREAM_CODEC = StreamCodec.composite(
            ResearchQueue.STREAM_CODEC,
            TeamResearchProgress::researchQueue,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchInstance.STREAM_CODEC),
            TeamResearchProgress::researches,
            ByteBufCodecs.map(HashMap::new, Research.RESOURCE_KEY_STREAM_CODEC, ResearchMethodProgress.STREAM_CODEC),
            TeamResearchProgress::progress,
            TeamResearchProgress::new
    );

    // Helper methods
    public boolean hasCompleted(ResourceKey<Research> research) {
        return this.researches.get(research).getResearchStatus() == ResearchStatus.RESEARCHED;
    }

    public ResearchMethodProgress getProgress(ResourceKey<Research> key, HolderLookup.Provider provider) {
        return progress().computeIfAbsent(key, r -> provider.holderOrThrow(r).value().researchMethod().getDefaultProgress());
    }

    private boolean _check_or_and_method(ResearchMethodProgress prog, ResearchProgressPacket pkt) {
        if (prog.hasChildren) {
            for (ResearchMethodProgress child : prog.children) {
                if (_check_or_and_method(child, pkt)) {
                    return true;
                }
            }
        }
        return pkt.methodId().equals(prog.getMethod()) && !prog.isComplete();
    }

    /**
     * Check if the given progress packet would be accepted for processing
     * @param pkt the packet to check
     * @param provider registry access
     * @return true if the packet would fit any of the current research methods, false otherwise
     */
    public boolean wouldAcceptProgressPacket(ResearchProgressPacket pkt, HolderLookup.Provider provider) {
        ResearchInstance current = this.researchQueue.current();
        if (current == null) return false;

        ResearchMethodProgress prog = this.getProgress(current.getResearch().getResearch(), provider);
        return _check_or_and_method(prog, pkt);
    }

    private ResearchMethodProgress _findFirstMatchingProgress(ResearchMethodProgress prog, ResearchProgressPacket pkt) {
        if (prog.hasChildren) {
            for (ResearchMethodProgress child : prog.children) {
                ResearchMethodProgress res = _findFirstMatchingProgress(child, pkt);
                if (res != null) return res;
            }
        }
        if (pkt.methodId().equals(prog.getMethod()) && !prog.isComplete()) {
            return prog;
        }
        return null;
    }

    /**
     * Shouldn't be called without checking {@link TeamResearchProgress#wouldAcceptProgressPacket}  first!
     *
     * @param pkt the packet to process
     */
    public void pushProgress(ResearchProgressPacket pkt) {
        ResearchInstance current = this.researchQueue.current();
        if (current == null) return;
        ResearchMethodProgress prog = this.progress().get(current.getResearch().getResearch());

        ResearchMethodProgress target = _findFirstMatchingProgress(prog, pkt);
        target.progress(pkt.progress());
    }

    public @Nullable ResearchInstance current() {
        return this.researchQueue.current();
    }

    public void completeResearch(ResearchInstance research) {
        this.researches.get(research.getKey()).setResearchStatus(ResearchStatus.RESEARCHED);
    }
}