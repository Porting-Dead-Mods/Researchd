package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.SimpleResearchPack;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchSerializers {
	public static final DeferredRegister<ResearchSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_SERIALIZER, Researchd.MODID);

	static {
		SERIALIZERS.register("simple", () -> SimpleResearch.Serializer.INSTANCE);
	}
}
