package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ResearchdRegistries {
	public static final ResourceKey<Registry<Research>> RESEARCH_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research"));
	public static final ResourceKey<Registry<ResearchPack>> RESEARCH_PACK_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_pack"));

	public static final ResourceKey<Registry<ResearchSerializer<?>>> RESEARCH_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_serializer"));
	public static final ResourceKey<Registry<ResearchMethodSerializer<?>>> RESEARCH_METHOD_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_method_serializer"));
	public static final ResourceKey<Registry<ResearchPredicateSerializer<?>>> RESEARCH_PREDICATE_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_predicate_serializer"));

	public static final Registry<ResearchSerializer<?>> RESEARCH_SERIALIZER = new RegistryBuilder<>(RESEARCH_SERIALIZER_KEY).sync(true).create();
	public static final Registry<ResearchMethodSerializer<?>> RESEARCH_METHOD_SERIALIZER = new RegistryBuilder<>(RESEARCH_METHOD_SERIALIZER_KEY).create();
	public static final Registry<ResearchPredicateSerializer<?>> RESEARCH_PREDICATE_SERIALIZER = new RegistryBuilder<>(RESEARCH_PREDICATE_SERIALIZER_KEY).create();

}
