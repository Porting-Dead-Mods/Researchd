package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.content.predicates.RecipePredicate;
import com.portingdeadmods.researchd.content.predicates.DimensionPredicate;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchEffectSerializers {
	public static final DeferredRegister<ResearchEffectSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER_KEY, Researchd.MODID);

	static {
		SERIALIZERS.register("unlock_dimension", () -> DimensionPredicate.Serializer.INSTANCE);
		SERIALIZERS.register("unlock_recipe", () -> RecipePredicate.Serializer.INSTANCE);
	}
}
