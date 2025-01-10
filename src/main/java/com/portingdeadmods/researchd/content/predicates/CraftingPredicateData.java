package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record CraftingPredicateData (Set<RecipeHolder<?>> blockedRecipes) {
    public static final CraftingPredicateData EMPTY = new CraftingPredicateData(Collections.emptySet());
    public static final Codec<CraftingPredicateData> CODEC = CodecUtils.set(Codecs.RECIPE_HOLDER_CODEC).xmap(CraftingPredicateData::new, CraftingPredicateData::blockedRecipes);

    public CraftingPredicateData addBlockedRecipe(RecipeHolder<?> recipe) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.add(recipe);
        return new CraftingPredicateData(recipes);
    }

    public CraftingPredicateData removeBlockedRecipe(RecipeHolder<?> recipe) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.remove(recipe);
        return new CraftingPredicateData(recipes);
    }
}
