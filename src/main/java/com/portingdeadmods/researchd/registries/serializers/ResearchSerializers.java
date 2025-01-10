package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ResearchSerializers {
	public static final DeferredRegister<ResearchSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_SERIALIZER, Researchd.MODID);

	static {
		SERIALIZERS.register("empty", () -> EmptyResearch.Serializer.INSTANCE);
		SERIALIZERS.register("simple", () -> SimpleResearch.Serializer.INSTANCE);
	}
}
