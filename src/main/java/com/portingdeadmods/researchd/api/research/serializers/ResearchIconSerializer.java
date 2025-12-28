package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface ResearchIconSerializer<T extends ResearchIcon> {
    @NotNull MapCodec<T> codec();

    static <T extends ResearchIcon> ResearchIconSerializer<T> simple(MapCodec<T> codec) {
        return () -> codec;
    }
}
