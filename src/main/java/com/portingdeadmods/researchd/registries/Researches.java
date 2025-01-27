package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public final class Researches {
    private static final Map<ResourceKey<Research>, Research.Builder<?>> RESEARCHES = new HashMap<>();

    public static final ResourceKey<Research> WOOD = register("wood", builder -> builder
            .icon(Items.OAK_LOG)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 2)
            )));
    public static final ResourceKey<Research> STICK = register("stick", builder -> builder
            .icon(Items.STICK)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 5)
            ))
            .parents(Researches.WOOD)
            .requiresParent(false));
    public static final ResourceKey<Research> WOODEN_PICKAXE = register("wooden_pickaxe", builder -> builder
            .icon(Items.WOODEN_PICKAXE)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 6),
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 3)
            ))
            .parents(WOOD, STICK)
            .requiresParent(true));
    public static final ResourceKey<Research> STONE = register("stone", builder -> builder
            .icon(Items.STONE)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 3),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(WOOD, STICK)
            .requiresParent(true));
    public static final ResourceKey<Research> COPPER = register("copper", builder -> builder
            .icon(Items.RAW_COPPER)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 5)
            ))
            .parents(WOODEN_PICKAXE)
            .requiresParent(true));
    public static final ResourceKey<Research> COAL = register("coal", builder -> builder
            .icon(Items.COAL)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(WOODEN_PICKAXE)
            .requiresParent(true));
    public static final ResourceKey<Research> DIAMOND = register("diamond", builder -> builder
            .icon(Items.DIAMOND)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(COPPER)
            .requiresParent(true));
    public static final ResourceKey<Research> IRON_INGOT = register("iron_ingot", builder -> builder
            .icon(Items.IRON_INGOT)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(COPPER)
            .requiresParent(true));
    public static final ResourceKey<Research> COPPER_INGOT = register("copper_ingot", builder -> builder
            .icon(Items.COPPER_INGOT)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(COPPER)
            .requiresParent(true));
    public static final ResourceKey<Research> IRON_BLOCK = register("iron_block", builder -> builder
            .icon(Items.IRON_BLOCK)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(COPPER)
            .requiresParent(true));
    public static final ResourceKey<Research> GOLD_HOE = register("gold_hoe", builder -> builder
            .icon(Items.GOLDEN_HOE)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(COPPER)
            .requiresParent(true));
    public static final ResourceKey<Research> COAL_BLOCK = register("coal_block", builder -> builder
            .icon(Items.COAL_BLOCK)
            .researchMethods(List.of(
                    new ConsumePackResearchMethod(ResearchPacks.NETHER, 7),
                    new ConsumePackResearchMethod(ResearchPacks.END, 4)
            ))
            .parents(COAL)
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
