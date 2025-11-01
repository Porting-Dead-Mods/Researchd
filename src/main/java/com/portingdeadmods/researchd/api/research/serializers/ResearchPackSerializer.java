package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ResearchPackSerializer<P extends ResearchPack> {
    MapCodec<P> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, P> streamCodec();
}
