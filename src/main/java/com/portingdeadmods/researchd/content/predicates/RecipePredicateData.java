package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record RecipePredicateData(Set<RecipeHolder<?>> blockedRecipes) {
    public static final RecipePredicateData EMPTY = new RecipePredicateData(Collections.emptySet());
    public static final Codec<RecipePredicateData> CODEC = CodecUtils.set(Codecs.RECIPE_HOLDER_CODEC).xmap(RecipePredicateData::new, RecipePredicateData::blockedRecipes);

    public RecipePredicateData addBlockedRecipe(RecipeHolder<?> recipe) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.add(recipe);
        return new RecipePredicateData(recipes);
    }

    public RecipePredicateData removeBlockedRecipe(RecipeHolder<?> recipe) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.remove(recipe);
        return new RecipePredicateData(recipes);
    }
}
