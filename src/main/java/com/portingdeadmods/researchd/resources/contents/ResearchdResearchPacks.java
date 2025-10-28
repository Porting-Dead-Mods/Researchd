package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ResearchdResearchPacks implements ResearchdDatagenProvider<ResearchPackImpl> {
    public static final ResourceLocation OVERWORLD_PACK_LOC = Researchd.rl("overworld");
    public static final ResourceLocation NETHER_PACK_LOC = Researchd.rl("nether");
    public static final ResourceLocation END_PACK_LOC = Researchd.rl("end");

    private final String modid;
    private final Map<ResourceKey<ResearchPackImpl>, ResearchPackImpl> researchPacks;

    public ResearchdResearchPacks(String modid) {
        this.modid = modid;
        this.researchPacks = new HashMap<>();
    }

    @Override
    public void build() {
        researchPack("overworld", builder -> builder
                .sortingValue(1)
                .color(20, 240, 20));
        researchPack("nether", builder -> builder
                .sortingValue(2)
                .color(160, 20, 20));
        researchPack("end", builder -> builder
                .sortingValue(3)
                .color(63, 36, 83));
    }

    public void buildExampleDatapack() {
        researchPack("test_pack", builder -> builder
                .sortingValue(1)
                .color(120, 150, 90));
    }

	protected ResourceKey<ResearchPackImpl> researchPack(String name, UnaryOperator<ResearchPackImpl.Builder> builder) {
		ResourceKey<ResearchPackImpl> key = key(name);
		researchPacks.put(key, builder.apply(ResearchPackImpl.builder()).build());

		return key;
	}

	protected ResourceKey<ResearchPackImpl> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.fromNamespaceAndPath(this.modid, name));
	}

	public static ItemStack asStack(ResourceKey<ResearchPackImpl> key, int count) {
		ItemStack pack = ResearchdItems.RESEARCH_PACK.toStack();
		pack.set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(Optional.of(key)));
		pack.setCount(count);
		return pack;
	}

	public static ItemStack asStack(ResourceKey<ResearchPackImpl> key) {
		return asStack(key, 1);
	}

    public static ItemStack asStack(ResourceLocation key) {
        return asStack(ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, key), 1);
    }

    @Override
    public Map<ResourceKey<ResearchPackImpl>, ResearchPackImpl> getContents() {
        return this.researchPacks;
    }
}
