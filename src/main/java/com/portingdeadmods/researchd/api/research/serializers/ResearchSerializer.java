package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.Research;

public interface ResearchSerializer<T extends Research> {
    MapCodec<T> codec();
}
