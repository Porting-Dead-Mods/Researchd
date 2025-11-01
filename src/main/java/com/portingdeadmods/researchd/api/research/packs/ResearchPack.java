package com.portingdeadmods.researchd.api.research.packs;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface ResearchPack {
    Codec<ResourceKey<ResearchPack>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_PACK_KEY);
    StreamCodec<ByteBuf, ResourceKey<ResearchPack>> RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_PACK_KEY);

    Codec<ResearchPack> CODEC = ResearchdRegistries.RESEARCH_PACK_SERIALIZER.byNameCodec().dispatch(ResearchPack::getSerializer, ResearchPackSerializer::codec);

    StreamCodec<RegistryFriendlyByteBuf, ResearchPack> STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC).cast();

    int color();

    int sortingValue();

    Optional<ResourceLocation> customTexture();

    ResearchPackSerializer<?> getSerializer();

    static Component getLangName(ResourceKey<ResearchPack> key) {
        String registryPath = ResearchdRegistries.RESEARCH_PACK_KEY.location().getPath();
        String keyNamespace = key.location().getNamespace();
        String keyPath = key.location().getPath();
        return Component.translatable(String.format("%s.%s.%s_name", registryPath, keyNamespace, keyPath));
    }

    static Component getLangDesc(ResourceKey<ResearchPack> key) {
        String registryPath = ResearchdRegistries.RESEARCH_PACK_KEY.location().getPath();
        String keyNamespace = key.location().getNamespace();
        String keyPath = key.location().getPath();
        return Component.translatable(String.format("%s.%s.%s_desc", registryPath, keyNamespace, keyPath));
    }
}
