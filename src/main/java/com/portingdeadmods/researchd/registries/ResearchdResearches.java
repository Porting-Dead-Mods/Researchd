package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ResearchdResearches {
    private final String modid;
    private final Map<ResourceKey<Research>, Research> researches;

    public ResearchdResearches(String modid) {
        this.modid = modid;
        this.researches = new HashMap<>();
    }

    public void build() {
         ResourceKey<Research> cobblestone = simpleResearch("cobblestone", builder -> builder
                .icon(Items.COBBLESTONE)
                .method(consumeItem(Items.COBBLESTONE, 4))
                .effect(
                        and(unlockRecipe(mcLoc("stone_pickaxe")), unlockRecipe(mcLoc("furnace")))
                ));
        ResourceKey<Research> overworldPack = simpleResearch("overworld_pack", builder -> builder
                .icon(ResearchdItems.RESEARCH_LAB.asItem())
                .parents(cobblestone)
                .method(consumeItem(Items.IRON_INGOT, 4))
                .effect(
                        and(unlockRecipe(modLoc("research_lab")), unlockRecipe(modLoc("overworld_pack")))
                ));
        ResourceKey<Research> nether = simpleResearch("nether", builder -> builder
                .icon(Items.NETHERRACK)
                .parents(overworldPack)
                .method(
                        consumePack(25, 100, ResearchdResearchPacks.OVERWORLD)
                )
                .effect(
                        and(
                                unlockRecipe(modLoc("nether_pack")),
                                unlockDimension(mcLoc("the_nether"), DimensionUnlockEffect.NETHER_SPRITE)
                        )
                ));
        ResourceKey<Research> end = simpleResearch("the_end", builder -> builder
                .icon(Items.END_STONE)
                .parents(nether)
                .method(consumePack(100, 200, ResearchdResearchPacks.OVERWORLD, ResearchdResearchPacks.NETHER))
                .effect(
                        and(
                                unlockRecipe(modLoc("end_pack")),
                                unlockDimension(mcLoc("the_end"), DimensionUnlockEffect.END_SPRITE)
                        )
                ));
        simpleResearch("end_crystal", builder -> builder
                .icon(Items.END_CRYSTAL)
                .parents(end)
                .method(consumePack(250, 200, ResearchdResearchPacks.OVERWORLD, ResearchdResearchPacks.NETHER, ResearchdResearchPacks.END))
                .effect(
                        and(unlockRecipe(mcLoc("end_crystal")))
                ));
        simpleResearch("beacon", builder -> builder
                .icon(Items.BEACON)
                .parents(end)
                .method(
                        consumePack(250, 200, ResearchdResearchPacks.OVERWORLD, ResearchdResearchPacks.NETHER, ResearchdResearchPacks.END)
                )
                .effect(
                        and(
                                unlockRecipe(mcLoc("beacon"))
                        )
                ));
    }

    protected @NotNull ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    protected @NotNull ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.modid, path);
    }

    protected @NotNull DimensionUnlockEffect unlockDimension(ResourceLocation location, ResourceLocation sprite) {
        return new DimensionUnlockEffect(location, sprite);
    }

    @SafeVarargs
    protected final @NotNull ConsumePackResearchMethod consumePack(int count, int duration, ResourceKey<SimpleResearchPack>... packs) {
        return new ConsumePackResearchMethod(Arrays.asList(packs), count, duration);
    }

    protected @NotNull RecipeUnlockEffect unlockRecipe(ResourceLocation location) {
        return new RecipeUnlockEffect(location);
    }

    protected @NotNull ConsumeItemResearchMethod consumeItem(ItemLike item, int count) {
        return new ConsumeItemResearchMethod(Ingredient.of(item), count);
    }

    protected ResourceKey<Research> key(String name) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, modLoc(name));
    }

    protected ResearchMethod and(ResearchMethod... methods) {
        return new AndResearchMethod(List.of(methods));
    }

    protected ResearchMethod or(ResearchMethod... methods) {
        return new OrResearchMethod(List.of(methods));
    }

    protected ResearchEffect and(ResearchEffect... methods) {
        return new AndResearchEffect(List.of(methods));
    }

    protected ResourceKey<Research> simpleResearch(String name, UnaryOperator<SimpleResearch.Builder> builder) {
        ResourceKey<Research> key = key(name);
        this.researches.put(key, builder.apply(SimpleResearch.builder())
                .build());
        return key;
    }

    public Map<ResourceKey<Research>, Research> getResearches() {
        return researches;
    }

}
