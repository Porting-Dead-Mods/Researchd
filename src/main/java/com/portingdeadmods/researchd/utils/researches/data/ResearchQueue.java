package com.portingdeadmods.researchd.utils.researches.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ResearchQueue(List<ResearchInstance> entries) {
    public static final ResearchQueue EMPTY = new ResearchQueue(Collections.emptyList());
    public static final Codec<ResearchQueue> CODEC = ResearchInstance.CODEC.listOf().xmap(ResearchQueue::new, ResearchQueue::entries);
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchQueue> STREAM_CODEC = ResearchInstance.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ResearchQueue::new, ResearchQueue::entries);
    // TODO: Make this configurable
    public static final int QUEUE_LENGTH = 7;

    public ResearchQueue() {
        this(new ArrayList<>(QUEUE_LENGTH));
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

}
