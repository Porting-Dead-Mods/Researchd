package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface ResearchPack {
    Codec<ResearchPack> CODEC =
            ResearchdRegistries.RESEARCH_PACK_SERIALIZER.byNameCodec().dispatch(ResearchPack::getSerializer, ResearchPackSerializer::codec);
    Codec<ResourceKey<ResearchPack>> RESOURCE_KEY_CODEC =
            ResourceKey.codec(ResearchdRegistries.RESEARCH_PACK_KEY);

    int color();

    Optional<ResourceLocation> customTexture();

    ResearchPackSerializer<?> getSerializer();

    interface Builder<T extends ResearchPack> {
        T build();
    }
}
