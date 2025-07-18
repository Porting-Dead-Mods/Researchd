package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

/**
 * Represents a single research
 */
public interface Research {
    Codec<Research> CODEC = ResearchdRegistries.RESEARCH_SERIALIZER.byNameCodec().dispatch(Research::getSerializer, ResearchSerializer::codec);
    Codec<ResourceKey<Research>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_KEY);
    StreamCodec<ByteBuf, ResourceKey<Research>> RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY);

    Item icon();

    /**
     * @return The research method that is required for this research
     */
    ResearchMethod researchMethod();

    /**
     * @return The research effects that happen after researching this
     */
    List<ResearchEffect> researchEffects();

    /**
     * @return An {@link Optional} {@link ResourceKey} which represents the parent of this research.
     */
    List<ResourceKey<Research>> parents();

    /**
     * @return whether the parent needs to researched to research this research
     */
    boolean requiresParent();

    ResearchSerializer<?> getSerializer();

    interface Builder<T extends Research> {
        T build();
    }
}
