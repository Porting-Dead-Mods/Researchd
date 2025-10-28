package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class ResearchdRegistries {
	public static final ResourceKey<Registry<Research>> RESEARCH_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research"));
	public static final ResourceKey<Registry<ResearchPackImpl>> RESEARCH_PACK_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_pack"));

    public static final ResourceKey<Registry<ValueEffect>> VALUE_EFFECT_KEY =
            ResourceKey.createRegistryKey(Researchd.rl("value_effect"));


    public static final ResourceKey<Registry<ResearchSerializer<?>>> RESEARCH_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_serializer"));
	public static final ResourceKey<Registry<ResearchMethodSerializer<?>>> RESEARCH_METHOD_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_method_serializer"));
	public static final ResourceKey<Registry<ResearchEffectSerializer<?>>> RESEARCH_EFFECT_SERIALIZER_KEY =
			ResourceKey.createRegistryKey(Researchd.rl("research_effect_serializer"));

	public static final Registry<ResearchSerializer<?>> RESEARCH_SERIALIZER = new RegistryBuilder<>(RESEARCH_SERIALIZER_KEY).sync(true).create();
	public static final Registry<ResearchMethodSerializer<?>> RESEARCH_METHOD_SERIALIZER = new RegistryBuilder<>(RESEARCH_METHOD_SERIALIZER_KEY).sync(true).create();
	public static final Registry<ResearchEffectSerializer<?>> RESEARCH_EFFECT_SERIALIZER = new RegistryBuilder<>(RESEARCH_EFFECT_SERIALIZER_KEY).sync(true).create();

	public static final Registry<ValueEffect> VALUE_EFFECT = new RegistryBuilder<>(VALUE_EFFECT_KEY).sync(true).create();
}
