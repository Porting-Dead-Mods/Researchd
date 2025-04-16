package com.portingdeadmods.researchd.utils.researches.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ResearchQueue implements Iterable<ResearchInstance> {
    public static final ResearchQueue EMPTY = new ResearchQueue(Collections.emptyList(), 0);
    public static final Codec<ResearchQueue> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResearchInstance.CODEC.listOf().fieldOf("entries").forGetter(ResearchQueue::getEntries),
            Codec.INT.fieldOf("researchProgress").forGetter(ResearchQueue::getResearchProgress)
    ).apply(inst, ResearchQueue::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchQueue> STREAM_CODEC = StreamCodec.composite(
            ResearchInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ResearchQueue::getEntries,
            ByteBufCodecs.INT,
            ResearchQueue::getResearchProgress,
            ResearchQueue::new
    );
    // TODO: Make this configurable
    public static final int QUEUE_LENGTH = 7;

    private final List<ResearchInstance> entries;
    private int researchProgress;

    public ResearchQueue(List<ResearchInstance> entries, int researchProgress) {
        this.entries = entries;
        this.researchProgress = researchProgress;
    }

    public ResearchQueue() {
        this(new ArrayList<>(QUEUE_LENGTH), 0);
    }

    /**
     * @param index of the element of which the priority should be increased
     * @return whether the priority was successfully increased
     */
    public boolean increasePriority(int index) {
        if (index > 0) {
            ResearchInstance instance = this.entries.get(index);
            ResearchInstance next = this.entries.get(index - 1);
            this.entries.set(index, next);
            this.entries.set(index - 1, instance);
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

        for (ResearchInstance instance : this.entries) {
            if (instance.getResearch().equals(researchInstance.getResearch())) return false;
        }

        if (this.entries.size() < QUEUE_LENGTH) {
            this.entries.add(researchInstance);
            return true;
        }
        return false;
    }

    /**
     * @param index of the element to be removed
     * @return whether it was possible to remove the element
     */
    public boolean remove(int index) {
        if (this.entries.size() > index) {
            this.entries.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Iterator<ResearchInstance> iterator() {
        return entries.iterator();
    }

    public void setResearchProgress(int researchProgress) {
        this.researchProgress = researchProgress;
    }

    public List<ResearchInstance> getEntries() {
        return entries;
    }

    public int getResearchProgress() {
        return researchProgress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ResearchQueue) obj;
        return Objects.equals(this.entries, that.entries) &&
                this.researchProgress == that.researchProgress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries, researchProgress);
    }

    @Override
    public String toString() {
        return "ResearchQueue[" +
                "entries=" + entries + ", " +
                "researchProgress=" + researchProgress + ']';
    }

}
