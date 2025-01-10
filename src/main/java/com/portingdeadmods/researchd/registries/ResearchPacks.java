package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Items;

import java.util.List;

public class ResearchPacks {
	public static final ResourceKey<ResearchPack> EMPTY = key("empty");

	public static void bootstrap(BootstrapContext<ResearchPack> context) {
	}

	private static void register(BootstrapContext<ResearchPack> context, ResourceKey<ResearchPack> key, ResearchPack.Builder<?> builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<ResearchPack> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, Researchd.rl(name));
	}
}
