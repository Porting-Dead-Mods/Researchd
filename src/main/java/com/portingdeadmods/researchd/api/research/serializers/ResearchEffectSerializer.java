package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ResearchEffectSerializer<T extends ResearchEffect> {
    StreamCodec<RegistryFriendlyByteBuf, ResearchEffectSerializer<?>> STREAM_CODEC =
            ByteBufCodecs.registry(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER_KEY);

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    static <T extends ResearchEffect> ResearchEffectSerializer<T> simple(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new ResearchEffectSerializer<>() {
            @Override
            public MapCodec<T> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }
}
