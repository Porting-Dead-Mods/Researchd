package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ResearchEffectSerializer<T extends ResearchEffect> {
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
