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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class Researches {
    private static final Map<ResourceKey<Research>, Research.Builder<?>> RESEARCHES = new HashMap<>();

    public static final ResourceKey<Research> WOOD = register("wood", builder -> builder
            .icon(Items.OAK_LOG)
            .researchPacks(Map.of(
                    ResearchPacks.OVERWORLD, 3
            )));
    private static final ResourceKey<Research> STICK = register("stick", builder -> builder
            .icon(Items.STICK)
            .researchPacks(Map.of(
                    ResearchPacks.OVERWORLD, 5
            ))
            .parents(Researches.WOOD)
            .requiresParent(false));
    private static final ResourceKey<Research> WOODEN_PICKAXE = register("wooden_pickaxe", builder -> builder
            .icon(Items.WOODEN_PICKAXE)
            .researchPacks(Map.of(
                    ResearchPacks.OVERWORLD, 6,
                    ResearchPacks.NETHER, 3
            ))
            .parents(WOOD, STICK)
            .requiresParent(true));
    public static final ResourceKey<Research> STONE = register("stone", builder -> builder
            .icon(Items.STONE)
            .researchPacks(Map.of(
                    ResearchPacks.OVERWORLD, 3,
                    ResearchPacks.END, 4
            ))
            .parents(WOODEN_PICKAXE)
            .requiresParent(true));
    private static final ResourceKey<Research> COPPER = register("copper", builder -> builder
            .icon(Items.RAW_COPPER)
            .researchPacks(Map.of(
                    ResearchPacks.NETHER, 5
            ))
            .parents(WOODEN_PICKAXE)
            .requiresParent(true));
    private static final ResourceKey<Research> COAL = register("coal", builder -> builder
            .icon(Items.COAL)
            .researchPacks(Map.of(
                    ResearchPacks.NETHER, 7,
                    ResearchPacks.END, 4
            ))
            .parents(WOODEN_PICKAXE)
            .requiresParent(true));

    public static void bootstrap(BootstrapContext<Research> context) {
        for (Map.Entry<ResourceKey<Research>, Research.Builder<?>> research : RESEARCHES.entrySet()) {
            register(context, research.getKey(), research.getValue());
        }
    }

    private static void register(BootstrapContext<Research> context, ResourceKey<Research> key, Research.Builder<?> builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<Research> key(String name) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Researchd.rl(name));
    }

    private static ResourceKey<Research> register(String name, UnaryOperator<SimpleResearch.Builder> builder) {
        ResourceKey<Research> key = key(name);
        RESEARCHES.put(key, builder.apply(SimpleResearch.Builder.of()));
        return key;
    }
}
