package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ResearchSerializer<T extends Research> {
    StreamCodec<RegistryFriendlyByteBuf, ResearchSerializer<?>> STREAM_CODEC =
            ByteBufCodecs.registry(ResearchdRegistries.RESEARCH_SERIALIZER_KEY);

    MapCodec<T> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

}
