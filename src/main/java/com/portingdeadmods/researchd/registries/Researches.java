package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.SimpleResearchPack;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Items;

import java.util.Map;

public final class Researches {
    private static final ResourceKey<Research> EXAMPLE = key("example");

    public static void bootstrap(BootstrapContext<Research> context) {
        register(context, EXAMPLE, SimpleResearch.Builder.of()
                .icon(Items.STICK)
                .researchPacks(Map.of(ResearchPacks.OVERWORLD, 3)));
    }

    private static void register(BootstrapContext<Research> context, ResourceKey<Research> key, Research.Builder<?> builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<Research> key(String name) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Researchd.rl(name));
    }
}
