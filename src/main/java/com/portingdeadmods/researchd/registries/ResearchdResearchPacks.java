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
	public static final ResourceKey<SimpleResearchPack> GRAY_1 = key("gray_1");
	public static final ResourceKey<SimpleResearchPack> GRAY_2 = key("gray_2");
	public static final ResourceKey<SimpleResearchPack> GRAY_3 = key("gray_3");
	public static final ResourceKey<SimpleResearchPack> GRAY_4 = key("gray_4");
	public static final ResourceKey<SimpleResearchPack> GRAY_5 = key("gray_5");
	public static final ResourceKey<SimpleResearchPack> GRAY_6 = key("gray_6");
	public static final ResourceKey<SimpleResearchPack> GRAY_7 = key("gray_7");
	public static final ResourceKey<SimpleResearchPack> GRAY_8 = key("gray_8");
	public static final ResourceKey<SimpleResearchPack> GRAY_9 = key("gray_9");
	public static final ResourceKey<SimpleResearchPack> GRAY_10 = key("gray_10");

	public static void bootstrap(BootstrapContext<SimpleResearchPack> context) {
		register(context, OVERWORLD, SimpleResearchPack.builder()
				.sortingValue(1)
				.color(222, 0, 0)
		);
		register(context, NETHER, SimpleResearchPack.builder()
				.sortingValue(2)
				.color(0, 0, 222)
		);
		register(context, END, SimpleResearchPack.builder()
				.sortingValue(3)
				.color(0, 222, 0)
		);
		register(context, GRAY_1, SimpleResearchPack.builder()
				.sortingValue(4)
				.color(25, 25, 25)
		);
		register(context, GRAY_2, SimpleResearchPack.builder()
				.sortingValue(5)
				.color(50, 50, 50)
		);
		register(context, GRAY_3, SimpleResearchPack.builder()
				.sortingValue(6)
				.color(75, 75, 75)
		);
		register(context, GRAY_4, SimpleResearchPack.builder()
				.sortingValue(7)
				.color(100, 100, 100)
		);
		register(context, GRAY_5, SimpleResearchPack.builder()
				.sortingValue(8)
				.color(125, 125, 125)
		);
		register(context, GRAY_6, SimpleResearchPack.builder()
				.sortingValue(9)
				.color(150, 150, 150)
		);
		register(context, GRAY_7, SimpleResearchPack.builder()
				.sortingValue(10)
				.color(175, 175, 175)
		);
		register(context, GRAY_8, SimpleResearchPack.builder()
				.sortingValue(11)
				.color(200, 200, 200)
		);
		register(context, GRAY_9, SimpleResearchPack.builder()
				.sortingValue(12)
				.color(225, 225, 225)
		);
		register(context, GRAY_10, SimpleResearchPack.builder()
				.sortingValue(13)
				.color(250, 250, 250)
		);
	}

	private static void register(BootstrapContext<SimpleResearchPack> context, ResourceKey<SimpleResearchPack> key, SimpleResearchPack.Builder builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<SimpleResearchPack> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, Researchd.rl(name));
	}
}
