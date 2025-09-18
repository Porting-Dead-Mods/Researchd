package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class ResearchdResearchPacks {
	public static final Map<ResourceKey<SimpleResearchPack>, SimpleResearchPack> RESEARCH_PACKS = new HashMap<>();

	public static final ResourceKey<SimpleResearchPack> OVERWORLD = register("overworld", builder -> builder.sortingValue(1).color(20, 240, 20));
	public static final ResourceKey<SimpleResearchPack> NETHER = register("nether", builder -> builder.sortingValue(2).color(160, 20, 20));
	public static final ResourceKey<SimpleResearchPack> END = register("end", builder -> builder.sortingValue(3).color(23, 6, 33));

	public static void bootstrap(BootstrapContext<SimpleResearchPack> context) {
		for (Map.Entry<ResourceKey<SimpleResearchPack>, SimpleResearchPack> research_pack : RESEARCH_PACKS.entrySet()) {
			context.register(research_pack.getKey(), research_pack.getValue());
		}
	}

	private static ResourceKey<SimpleResearchPack> register(String name, UnaryOperator<SimpleResearchPack.Builder> builder) {
		ResourceKey<SimpleResearchPack> key = key(name);
		RESEARCH_PACKS.put(key, builder.apply(SimpleResearchPack.builder()).build());

		return key;
	}

	private static ResourceKey<SimpleResearchPack> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, Researchd.rl(name));
	}

	public static ItemStack asStack(ResourceKey<SimpleResearchPack> key, int count) {
		ItemStack pack = ResearchdItems.RESEARCH_PACK.toStack();
		pack.set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(Optional.of(key)));
		pack.setCount(count);
		return pack;
	}

	public static ItemStack asStack(ResourceKey<SimpleResearchPack> key) {
		return asStack(key, 1);
	}
}
