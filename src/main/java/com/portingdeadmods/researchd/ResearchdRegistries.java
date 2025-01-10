package com.portingdeadmods.researchd;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ResearchdRegistries {
	public static final ResourceKey<Registry<ResearchPack>> RESEARCH_PACK_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "research_pack"));
	public static final ResourceKey<Registry<Research>> RESEARCH_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "research"));
	public static final ResourceKey<Registry<ResearchPredicate>> RESEARCH_PREDICATE_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "research_predicate"));

	public static final Registry<ResearchPack> RESEARCH_PACK = new RegistryBuilder<>(RESEARCH_PACK_KEY).create();
	public static final Registry<Research> RESEARCH = new RegistryBuilder<>(RESEARCH_KEY).create();

	public static final ResourceKey<Registry<ResearchSerializer<?>>> RESEARCH_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "research_serializer"));
	public static final ResourceKey<Registry<ResearchPackSerializer<?>>> RESEARCH_PACK_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "research_pack_serializer"));

	public static final Registry<ResearchSerializer<?>> RESEARCH_SERIALIZER = new RegistryBuilder<>(RESEARCH_SERIALIZER_KEY).create();
	public static final Registry<ResearchPackSerializer<?>> RESEARCH_PACK_SERIALIZER = new RegistryBuilder<>(RESEARCH_PACK_SERIALIZER_KEY).create();

}
