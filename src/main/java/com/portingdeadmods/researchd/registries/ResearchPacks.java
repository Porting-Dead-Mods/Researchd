package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.impl.research.SimpleResearchPack;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;

public final class ResearchPacks {
	public static final ResourceKey<ResearchPack> OVERWORLD = key("overworld");
	public static final ResourceKey<ResearchPack> NETHER = key("nether");
	public static final ResourceKey<ResearchPack> END = key("end");

	public static void bootstrap(BootstrapContext<ResearchPack> context) {
		register(context, OVERWORLD, SimpleResearchPack.Builder.of()
				.color(FastColor.ARGB32.color(255, 0, 0)));
		register(context, NETHER, SimpleResearchPack.Builder.of()
				.color(FastColor.ARGB32.color(0, 0, 255)));
		register(context, END, SimpleResearchPack.Builder.of()
				.color(FastColor.ARGB32.color(0, 255, 0)));
	}

	private static void register(BootstrapContext<ResearchPack> context, ResourceKey<ResearchPack> key, ResearchPack.Builder<?> builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<ResearchPack> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, Researchd.rl(name));
	}
}
