package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ResearchdRegistries {
	public static final ResourceKey<Registry<ResearchPack>> RESEARCH_PACK_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_pack"));
	public static final ResourceKey<Registry<Research>> RESEARCH_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research"));

	public static final ResourceKey<Registry<ResearchSerializer<?>>> RESEARCH_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_serializer"));
	public static final ResourceKey<Registry<ResearchPackSerializer<?>>> RESEARCH_PACK_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_pack_serializer"));
	public static final ResourceKey<Registry<ResearchPredicateSerializer<?>>> RESEARCH_PREDICATE_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_predicate_serializer"));

	public static final Registry<ResearchSerializer<?>> RESEARCH_SERIALIZER = new RegistryBuilder<>(RESEARCH_SERIALIZER_KEY).sync(true).create();
	public static final Registry<ResearchPackSerializer<?>> RESEARCH_PACK_SERIALIZER = new RegistryBuilder<>(RESEARCH_PACK_SERIALIZER_KEY).create();
	public static final Registry<ResearchPredicateSerializer<?>> RESEARCH_PREDICATE_SERIALIZER = new RegistryBuilder<>(RESEARCH_PREDICATE_SERIALIZER_KEY).create();

}
