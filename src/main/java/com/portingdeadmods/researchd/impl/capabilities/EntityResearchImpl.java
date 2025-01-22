package com.portingdeadmods.researchd.impl.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;
import java.util.stream.Collectors;

public record EntityResearchImpl(ResearchQueue researchQueue, Set<ResearchInstance> researches) {
    public static final EntityResearchImpl EMPTY = new EntityResearchImpl(ResearchQueue.EMPTY, Collections.emptySet());

    public static final Codec<EntityResearchImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchQueue.CODEC.fieldOf("researchQueue").forGetter(EntityResearchImpl::researchQueue),
            CodecUtils.set(ResearchInstance.CODEC).fieldOf("research").forGetter(EntityResearchImpl::researches)
    ).apply(instance, EntityResearchImpl::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityResearchImpl> STREAM_CODEC = StreamCodec.composite(
            ResearchInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            EntityResearchImpl::queueAsList,
            ResearchInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            EntityResearchImpl::researchesAsList,
            EntityResearchImpl::fromLists
    );

    private static EntityResearchImpl fromLists(List<ResearchInstance> researchQueue, List<ResearchInstance> researches) {
        return new EntityResearchImpl(new ResearchQueue(researchQueue), new HashSet<>(researches));
    }

    private List<ResearchInstance> researchesAsList() {
        return researches().stream().toList();
    }

    private List<ResearchInstance> queueAsList() {
        return researchQueue().entries();
    }
}
