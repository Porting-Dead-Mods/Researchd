package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.impl.research.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumePackResearchMethod;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchMethodSerializers {
    public static final DeferredRegister<ResearchMethodSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER, Researchd.MODID);

    static {
        SERIALIZERS.register("consume_item", () -> ConsumeItemResearchMethod.Serializer.INSTANCE);
        SERIALIZERS.register("consume_research_pack", () -> ConsumePackResearchMethod.Serializer.INSTANCE);
    }
}
