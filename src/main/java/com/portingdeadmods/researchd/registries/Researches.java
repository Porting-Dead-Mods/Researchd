package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.capabilties.Research;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class Researches {
	public static final ResourceKey<Research> EMPTY = key("empty");

	public static void bootstrap(BootstrapContext<Research> context) {
	}

	private static void register(BootstrapContext<Research> context, ResourceKey<Research> key, Research.Builder<?> builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<Research> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Researchd.rl(name));
	}
}
