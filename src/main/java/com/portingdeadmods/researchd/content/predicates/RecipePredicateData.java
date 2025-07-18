package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.ResearchEffectData;
import com.portingdeadmods.researchd.utils.Codecs;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record RecipePredicateData(Set<RecipeHolder<?>> blockedRecipes) implements ResearchEffectData<RecipePredicate> {
    public static final RecipePredicateData EMPTY = new RecipePredicateData(Collections.emptySet());

    public static final Codec<RecipePredicateData> CODEC = CodecUtils.set(Codecs.RECIPE_HOLDER_CODEC).xmap(RecipePredicateData::new, RecipePredicateData::blockedRecipes);

    public RecipePredicateData add(RecipePredicate recipe, Level level) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.add(recipe.getRecipe(level));
        return new RecipePredicateData(recipes);
    }

    public RecipePredicateData remove(RecipePredicate recipe, Level level) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.remove(recipe.getRecipe(level));
        return new RecipePredicateData(recipes);
    }

    public Set<RecipeHolder<?>> getAll() {
        return this.blockedRecipes;
    }

    /**
     * Returns a new RecipePredicateData with all the blocked recipes from researches.
     *
     * @param level
     */
    public RecipePredicateData getDefault(Level level) {
        Collection<RecipePredicate> rps =  ResearchHelperCommon.getResearchEffects(RecipePredicate.class, level);
        Set<RecipeHolder<?>> blockedRecipes = new HashSet<>();

        for (RecipePredicate rp : rps) {
            blockedRecipes.add(rp.getRecipe(level));
        }

        return new RecipePredicateData(blockedRecipes);
    }
}
