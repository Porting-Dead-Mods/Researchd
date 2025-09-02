package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface ResearchMethodSerializer<T extends ResearchMethod> {
    StreamCodec<RegistryFriendlyByteBuf, ResearchMethodSerializer<?>> STREAM_CODEC =
            ByteBufCodecs.registry(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER_KEY);

    @NotNull MapCodec<T> codec();

    @NotNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    static <T extends ResearchMethod> ResearchMethodSerializer<T> simple(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new ResearchMethodSerializer<>() {
            @Override
            public @NotNull MapCodec<T> codec() {
                return codec;
            }

            @Override
            public @NotNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }
}
