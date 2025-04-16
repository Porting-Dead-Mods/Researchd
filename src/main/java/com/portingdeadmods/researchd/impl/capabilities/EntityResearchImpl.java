package com.portingdeadmods.researchd.impl.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.ClientResearchCache;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public record EntityResearchImpl(ResearchQueue researchQueue, Set<ResearchInstance> researches) {
    public static final EntityResearchImpl EMPTY = new EntityResearchImpl(ResearchQueue.EMPTY, new LinkedHashSet<>());

    public static final Codec<EntityResearchImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchQueue.CODEC.fieldOf("researchQueue").forGetter(EntityResearchImpl::researchQueue),
            ResearchInstance.CODEC.listOf().fieldOf("research").forGetter(EntityResearchImpl::researchesAsList)
    ).apply(instance, EntityResearchImpl::fromLists));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityResearchImpl> STREAM_CODEC = StreamCodec.composite(
            ResearchQueue.STREAM_CODEC,
            EntityResearchImpl::researchQueue,
            ResearchInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            EntityResearchImpl::researchesAsList,
            EntityResearchImpl::fromLists
    );

    private static EntityResearchImpl fromLists(ResearchQueue researchQueue, List<ResearchInstance> researches) {
        return new EntityResearchImpl(researchQueue, new LinkedHashSet<>(researches));
    }

    private List<ResearchInstance> researchesAsList() {
        return researches().stream().toList();
    }

    private List<ResearchInstance> queueAsList() {
        return researchQueue().getEntries();
    }

    public static void onSync(Player player) {
        if (player.level().isClientSide()) {
            ClientResearchCache.initialize(player);
        }
    }
}
