package com.portingdeadmods.researchd.impl.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public record EntityResearchImpl(ResearchQueue researchQueue, Set<ResearchInstance> completedResearches) implements EntityResearch {
    public static final EntityResearchImpl EMPTY = new EntityResearchImpl(ResearchQueue.EMPTY, new LinkedHashSet<>());

    public static final Codec<EntityResearchImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchQueue.CODEC.fieldOf("researchQueue").forGetter(EntityResearchImpl::researchQueue),
            ResearchInstance.CODEC.listOf().fieldOf("completedResearches").forGetter(EntityResearchImpl::researchesAsList)
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
        return this.completedResearches().stream().toList();
    }

    private List<ResearchInstance> queueAsList() {
        return researchQueue().getEntries();
    }

    public static void onSync(Player player) {
        if (player.level().isClientSide()) {
            ClientResearchCache.initialize(player);
        }
    }

    @Override
    public void completeResearch(ResearchInstance researchInstance) {
        this.completedResearches.add(new ResearchInstance(researchInstance.getResearch(), ResearchStatus.RESEARCHED));
    }

    public boolean isCompleted(ResourceKey<Research> resourceKey) {
        for (ResearchInstance researchInstance : this.completedResearches) {
            if (researchInstance.getResearch().compareTo(resourceKey) == 0) {
                return true;
            }
        }
        return false;
    }

}
