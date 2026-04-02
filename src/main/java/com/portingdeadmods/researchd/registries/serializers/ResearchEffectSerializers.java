package com.portingdeadmods.researchd.registries.serializers;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.DecreaseValueEffect;
import com.portingdeadmods.researchd.impl.research.effect.DivideValueEffect;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.IncreaseValueEffect;
import com.portingdeadmods.researchd.impl.research.effect.ItemUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.MultiplyValueEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchEffectSerializers {
	public static final DeferredRegister<ResearchEffectSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER_KEY, Researchd.MODID);

	static {
		SERIALIZERS.register("unlock_dimension", () -> DimensionUnlockEffect.SERIALIZER);
		SERIALIZERS.register("unlock_recipe", () -> RecipeUnlockEffect.SERIALIZER);
		SERIALIZERS.register("unlock_item", () -> ItemUnlockEffect.SERIALIZER);
		SERIALIZERS.register("and", () -> AndResearchEffect.SERIALIZER);
		SERIALIZERS.register("empty", () -> EmptyResearchEffect.SERIALIZER);
		SERIALIZERS.register("increase_value", () -> IncreaseValueEffect.SERIALIZER);
		SERIALIZERS.register("decrease_value", () -> DecreaseValueEffect.SERIALIZER);
		SERIALIZERS.register("multiply_value", () -> MultiplyValueEffect.SERIALIZER);
		SERIALIZERS.register("divide_value", () -> DivideValueEffect.SERIALIZER);
	}
}
