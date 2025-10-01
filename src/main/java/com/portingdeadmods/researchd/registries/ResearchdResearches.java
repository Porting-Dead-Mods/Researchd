package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
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

public final class ResearchdResearches {
    private static final Map<ResourceKey<Research>, SimpleResearch.Builder> RESEARCHES = new HashMap<>();

    public static final ResourceKey<Research> COBBLESTONE = register("cobblestone", builder -> builder
            .icon(Items.COBBLESTONE)
            .researchMethod(
                    new ConsumeItemResearchMethod(Ingredient.of(Items.COBBLESTONE), 4)
            )
            .researchEffect(
                    and(
                            new RecipeUnlockEffect(ResourceLocation.withDefaultNamespace("stone_pickaxe")),
                            new RecipeUnlockEffect(ResourceLocation.withDefaultNamespace("furnace"))
                    )
            ));

    public static final ResourceKey<Research> OVERWORLD_PACK = register("overworld_pack", builder -> builder
            .icon(ResearchdItems.RESEARCH_LAB.asItem())
            .parents(COBBLESTONE)
            .researchMethod(
                    and(
                            new ConsumeItemResearchMethod(Ingredient.of(Items.IRON_INGOT), 4)
                    )
            )
            .researchEffect(
                    new AndResearchEffect(List.of(
                            new RecipeUnlockEffect(Researchd.rl("research_lab")),
                            new RecipeUnlockEffect(Researchd.rl("overworld_pack"))
                    ))
            ));

    public static final ResourceKey<Research> NETHER = register("nether", builder -> builder
            .icon(Items.NETHERRACK)
            .parents(OVERWORLD_PACK)
            .researchMethod(
                    new ConsumePackResearchMethod(List.of(ResearchdResearchPacks.OVERWORLD), 25, 100)
            )
            .researchEffect(
                    and(
                        new RecipeUnlockEffect(Researchd.rl("nether_pack")),
                        new DimensionUnlockEffect(ResourceLocation.withDefaultNamespace("the_nether"), DimensionUnlockEffect.NETHER_SPRITE)
                    )
            ));

    public static final ResourceKey<Research> THE_END = register("the_end", builder -> builder
            .icon(Items.END_STONE)
            .parents(NETHER)
            .researchMethod(
                    new ConsumePackResearchMethod(List.of(ResearchdResearchPacks.OVERWORLD, ResearchdResearchPacks.NETHER), 100, 200)
            )
            .researchEffect(
                    and(
                            new RecipeUnlockEffect(Researchd.rl("end_pack")),
                            new DimensionUnlockEffect(ResourceLocation.withDefaultNamespace("the_end"), DimensionUnlockEffect.END_SPRITE)
                    )
            ));

    public static final ResourceKey<Research> BEACON = register("beacon", builder -> builder
            .icon(Items.BEACON)
            .parents(THE_END)
            .researchMethod(
                    new ConsumePackResearchMethod(List.of(ResearchdResearchPacks.OVERWORLD, ResearchdResearchPacks.NETHER, ResearchdResearchPacks.END), 250, 200)
            )
            .researchEffect(
                    and(
                            new RecipeUnlockEffect(ResourceLocation.withDefaultNamespace("beacon"))
                    )
            ));

    public static final ResourceKey<Research> END_CRYSTAL = register("end_crystal", builder -> builder
            .icon(Items.END_CRYSTAL)
            .parents(THE_END)
            .researchMethod(
                    new ConsumePackResearchMethod(List.of(ResearchdResearchPacks.OVERWORLD, ResearchdResearchPacks.NETHER, ResearchdResearchPacks.END), 250, 200)
            )
            .researchEffect(
                    and(
                            new RecipeUnlockEffect(ResourceLocation.withDefaultNamespace("end_crystal"))
                    )
            ));

    public static void bootstrap(BootstrapContext<Research> context) {
        for (Map.Entry<ResourceKey<Research>, SimpleResearch.Builder> research : RESEARCHES.entrySet()) {
            register(context, research.getKey(), research.getValue());
        }
    }

    private static void register(BootstrapContext<Research> context, ResourceKey<Research> key, SimpleResearch.Builder builder) {
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

    private static ResearchEffect and(ResearchEffect... methods) {
        return new AndResearchEffect(List.of(methods));
    }

}
