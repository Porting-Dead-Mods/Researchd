package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public final class ResearchdResearchPacks {
	public static final ResourceKey<SimpleResearchPack> OVERWORLD = key("overworld");
	public static final ResourceKey<SimpleResearchPack> NETHER = key("nether");
	public static final ResourceKey<SimpleResearchPack> END = key("end");

	public static void bootstrap(BootstrapContext<SimpleResearchPack> context) {
		register(context, OVERWORLD, SimpleResearchPack.builder()
				.color(222, 0, 0)
		);
		register(context, NETHER, SimpleResearchPack.builder()
				.color(0, 0, 222)
		);
		register(context, END, SimpleResearchPack.builder()
				.color(0, 222, 0)
		);
	}

	private static void register(BootstrapContext<SimpleResearchPack> context, ResourceKey<SimpleResearchPack> key, SimpleResearchPack.Builder builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<SimpleResearchPack> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, Researchd.rl(name));
	}
}
