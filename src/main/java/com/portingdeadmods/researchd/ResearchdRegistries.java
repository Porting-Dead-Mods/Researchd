package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.pdl.data.PDLSavedData;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
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
	public static final ResourceKey<Registry<ResearchEffectSerializer<?>>> RESEARCH_EFFECT_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_effect_serializer"));

	public static final Registry<ResearchSerializer<?>> RESEARCH_SERIALIZER = new RegistryBuilder<>(RESEARCH_SERIALIZER_KEY).sync(true).create();
	public static final Registry<ResearchMethodSerializer<?>> RESEARCH_METHOD_SERIALIZER = new RegistryBuilder<>(RESEARCH_METHOD_SERIALIZER_KEY).create();
	public static final Registry<ResearchEffectSerializer<?>> RESEARCH_EFFECT_SERIALIZER = new RegistryBuilder<>(RESEARCH_EFFECT_SERIALIZER_KEY).create();

	public static final ResourceKey<Registry<PDLSavedData<?>>> SAVED_DATA_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("saved_data"));
	public static final Registry<PDLSavedData<?>> SAVED_DATA = new RegistryBuilder<>(SAVED_DATA_KEY).create();
}
