package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class ResearchPredicates {
	public static final ResourceKey<ResearchPredicate> EMPTY = key("empty");

	public static void bootstrap(BootstrapContext<ResearchPredicate> context) {
	}

	private static void register(BootstrapContext<ResearchPredicate> context, ResourceKey<ResearchPredicate> key, ResearchPredicate.Builder<?> builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<ResearchPredicate> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PREDICATE_KEY, Researchd.rl(name));
	}
}
