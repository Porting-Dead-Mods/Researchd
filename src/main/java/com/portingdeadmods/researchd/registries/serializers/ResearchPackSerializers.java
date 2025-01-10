package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ResearchPackSerializers {
	public static final DeferredRegister<ResearchPackSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_PACK_SERIALIZER, Researchd.MODID);

	static {
		SERIALIZERS.register("empty", () -> EmptyResearchPack.Serializer.INSTANCE);
		SERIALIZERS.register("overworld", () -> OverworldResearchPack.Serializer.INSTANCE);
		SERIALIZERS.register("nether", () -> NetherResearchPack.Serializer.INSTANCE);
		SERIALIZERS.register("end", () -> EndResearchPack.Serializer.INSTANCE);
	}
}
