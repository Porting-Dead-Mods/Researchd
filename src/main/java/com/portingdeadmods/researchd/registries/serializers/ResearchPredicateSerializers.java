package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import com.portingdeadmods.researchd.content.predicates.DimensionPredicate;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchPredicateSerializers {
	public static final DeferredRegister<ResearchPredicateSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_PREDICATE_SERIALIZER, Researchd.MODID);

	static {
		SERIALIZERS.register("dimension_predicate", () -> DimensionPredicate.Serializer.INSTANCE);
	}
}
