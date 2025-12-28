package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.ItemUnlockEffect;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.CheckItemPresenceResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public interface ResearchdResearchProvider {
    String getModid();
    Map<ResourceKey<Research>, Research> getResearches();

    default @NotNull ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    default @NotNull ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(getModid(), path);
    }

    default @NotNull ResourceLocation loc(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    default @NotNull DimensionUnlockEffect unlockDimension(ResourceLocation location, ResourceLocation sprite) {
        return new DimensionUnlockEffect(location, sprite);
    }

    default @NotNull ItemUnlockEffect unlockItem(ItemLike item) {
        return new ItemUnlockEffect(item.asItem());
    }

    default @NotNull ConsumePackResearchMethod consumePack(int count, int duration, ResourceKey<ResearchPack>... packs) {
        return new ConsumePackResearchMethod(Arrays.asList(packs), count, duration);
    }

    default @NotNull RecipeUnlockEffect unlockRecipe(ResourceLocation location) {
        return new RecipeUnlockEffect(location);
    }

    default @NotNull ConsumeItemResearchMethod consumeItem(ItemLike item, int count) {
        return new ConsumeItemResearchMethod(Ingredient.of(item), count);
    }

    default @NotNull CheckItemPresenceResearchMethod hasItem(ItemLike item, int count) {
        return new CheckItemPresenceResearchMethod(Ingredient.of(item), count);
    }

    default ResourceKey<Research> key(String name) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, modLoc(name));
    }

    default ResourceKey<ResearchPack> pack(ResourceLocation location) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, location);
    }

    default ResearchMethod and(ResearchMethod... methods) {
        return new AndResearchMethod(List.of(methods));
    }

    default ResearchMethod or(ResearchMethod... methods) {
        return new OrResearchMethod(List.of(methods));
    }

    default ResearchEffect and(ResearchEffect... effects) {
        return new AndResearchEffect(List.of(effects));
    }

    default ResourceKey<Research> simpleResearch(String name, UnaryOperator<SimpleResearch.Builder> builder) {
        ResourceKey<Research> key = key(name);
        getResearches().put(key, builder.apply(SimpleResearch.builder()).build());
        return key;
    }
}
