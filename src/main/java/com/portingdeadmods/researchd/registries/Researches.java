package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchMethod;
import com.portingdeadmods.researchd.content.predicates.RecipePredicate;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
            .researchMethods(
                    or(new ConsumeItemResearchMethod(Ingredient.of(Items.DIRT), 8), new ConsumeItemResearchMethod(Ingredient.of(Items.WHEAT_SEEDS), 2))
            )
            .researchEffects(
                    new RecipePredicate(ResourceLocation.withDefaultNamespace("anvil"))
            ));
    public static final ResourceKey<Research> STONE = register("stone", builder -> builder
            .icon(Items.STONE)
            .parents(WOOD)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 4)
            ));
    public static final ResourceKey<Research> IRON = register("iron", builder -> builder
            .icon(Items.IRON_INGOT)
            .parents(STONE)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 6)
            ));
    public static final ResourceKey<Research> COPPER = register("copper", builder -> builder
            .icon(Items.COPPER_INGOT)
            .parents(STONE)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 6)
            ));
    public static final ResourceKey<Research> IRON_TOOLS = register("iron_tools", builder -> builder
            .icon(Items.IRON_SWORD)
            .parents(IRON)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 8)
            ));
    public static final ResourceKey<Research> IRON_ARMOR = register("iron_armor", builder -> builder
            .icon(Items.IRON_CHESTPLATE)
            .parents(IRON)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 8)
            ));
    public static final ResourceKey<Research> LIGHTNING_ROD = register("lightning_rod", builder -> builder
            .icon(Items.LIGHTNING_ROD)
            .parents(COPPER)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 8)
            ));
    public static final ResourceKey<Research> COPPER_BLOCK = register("copper_block", builder -> builder
            .icon(Items.COPPER_BLOCK)
            .parents(COPPER)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 8)
            ));
    public static final ResourceKey<Research> CHARGED_CREEPER = register("charged_creeper", builder -> builder
            .icon(Items.CREEPER_HEAD)
            .parents(LIGHTNING_ROD)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> SKELETON_HORSE = register("skeleton_horse", builder -> builder
            .icon(Items.SKELETON_SKULL)
            .parents(LIGHTNING_ROD)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> CHISELED_COPPER = register("chiseled_copper", builder -> builder
            .icon(Items.CHISELED_COPPER)
            .parents(COPPER_BLOCK)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> OXIDIZED_COPPER = register("oxidized_copper", builder -> builder
            .icon(Items.OXIDIZED_COPPER)
            .parents(COPPER_BLOCK)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> DIAMOND_SWORD = register("diamond_sword", builder -> builder
            .icon(Items.DIAMOND_SWORD)
            .parents(IRON_TOOLS)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> TRIDENT = register("trident", builder -> builder
            .icon(Items.TRIDENT)
            .parents(IRON_TOOLS)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> DIAMOND_ARMOR = register("diamond_armor", builder -> builder
            .icon(Items.DIAMOND_CHESTPLATE)
            .parents(IRON_ARMOR)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> DIAMOND_HOE = register("diamond_hoe", builder -> builder
            .icon(Items.DIAMOND_HOE)
            .parents(DIAMOND_ARMOR, TRIDENT)
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));
    public static final ResourceKey<Research> DIAMOND_PICKAXE = register("diamond_pickaxe", builder -> builder
            .icon(Items.DIAMOND_PICKAXE)
            .parents(DIAMOND_ARMOR, TRIDENT)
            .researchEffects(new RecipePredicate(ResourceLocation.withDefaultNamespace("diamond_pickaxe")))
            .researchMethods(
                    new ConsumePackResearchMethod(ResearchPacks.OVERWORLD, 12)
            ));

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

    private static ResearchMethod and(ResearchMethod... methods) {
        return new AndResearchMethod(List.of(methods));
    }

    private static ResearchMethod or(ResearchMethod... methods) {
        return new OrResearchMethod(List.of(methods));
    }
}
