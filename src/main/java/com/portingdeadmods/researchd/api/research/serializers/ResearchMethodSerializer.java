package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ResearchMethodSerializer<T extends ResearchMethod> {
    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    static <T extends ResearchMethod> ResearchMethodSerializer<T> simple(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new ResearchMethodSerializer<>() {
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
