package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchPackSerializers {
    public static final DeferredRegister<ResearchPackSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_PACK_SERIALIZER_KEY, Researchd.MODID);

    static {
        SERIALIZERS.register("simple", () -> ResearchPackImpl.Serializer.INSTANCE);
    }
}
