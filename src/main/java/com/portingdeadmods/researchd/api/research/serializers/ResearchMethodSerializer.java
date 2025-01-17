package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ResearchMethodSerializer<T extends ResearchMethod> {
    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
