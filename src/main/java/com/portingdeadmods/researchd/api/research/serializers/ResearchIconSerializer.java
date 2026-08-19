package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import org.jetbrains.annotations.NotNull;

public interface ResearchIconSerializer<T extends ResearchIcon> {
    @NotNull MapCodec<T> codec();

    static <T extends ResearchIcon> ResearchIconSerializer<T> simple(MapCodec<T> codec) {
        return () -> codec;
    }
}
