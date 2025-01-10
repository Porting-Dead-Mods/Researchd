package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import com.portingdeadmods.researchd.impl.research.SimpleResearchPack;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchPackSerializers {
	public static final DeferredRegister<ResearchPackSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_PACK_SERIALIZER, Researchd.MODID);

	static {
		SERIALIZERS.register("simple", () -> SimpleResearchPack.Serializer.INSTANCE);
	}
}
