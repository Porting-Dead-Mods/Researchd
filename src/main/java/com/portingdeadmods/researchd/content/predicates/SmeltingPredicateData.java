package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record SmeltingPredicateData(Set<RecipeHolder<?>> blockedRecipes) {
    public static final SmeltingPredicateData EMPTY = new SmeltingPredicateData(Collections.emptySet());
    public static final Codec<SmeltingPredicateData> CODEC = CodecUtils.set(Codecs.RECIPE_HOLDER_CODEC).xmap(SmeltingPredicateData::new, SmeltingPredicateData::blockedRecipes);

    public SmeltingPredicateData addBlockedRecipe(RecipeHolder<?> recipe) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.add(recipe);
        return new SmeltingPredicateData(recipes);
    }

    public SmeltingPredicateData removeBlockedRecipe(RecipeHolder<?> recipe) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.remove(recipe);
        return new SmeltingPredicateData(recipes);
    }
}
