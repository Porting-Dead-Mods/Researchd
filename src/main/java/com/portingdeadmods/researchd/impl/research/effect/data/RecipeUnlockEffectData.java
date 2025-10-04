package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record RecipeUnlockEffectData(Set<RecipeHolder<?>> blockedRecipes) implements ResearchEffectData<RecipeUnlockEffect> {
    public static final RecipeUnlockEffectData EMPTY = new RecipeUnlockEffectData(Collections.emptySet());

    public static final Codec<RecipeUnlockEffectData> CODEC = CodecUtils.set(CodecUtils.RECIPE_HOLDER_CODEC).xmap(RecipeUnlockEffectData::new, RecipeUnlockEffectData::blockedRecipes);

    public RecipeUnlockEffectData add(RecipeUnlockEffect recipe, Level level) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.addAll(recipe.getRecipes(level));
        return new RecipeUnlockEffectData(recipes);
    }

    public RecipeUnlockEffectData remove(RecipeUnlockEffect recipe, Level level) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.blockedRecipes());
        recipes.removeAll(recipe.getRecipes(level));
        return new RecipeUnlockEffectData(recipes);
    }

    public Set<RecipeHolder<?>> getAll() {
        return this.blockedRecipes;
    }

    /**
     * Returns a new RecipePredicateData with all the blocked recipes from researchPacks.
     *
     * @param level
     */
    @Override
    public RecipeUnlockEffectData getDefault(Level level) {
        Collection<RecipeUnlockEffect> rps =  ResearchHelperCommon.getResearchEffects(RecipeUnlockEffect.class, level);
        Set<RecipeHolder<?>> blockedRecipes = new HashSet<>();

        for (RecipeUnlockEffect rp : rps) {
            blockedRecipes.addAll(rp.getRecipes(level));
        }

        return new RecipeUnlockEffectData(blockedRecipes);
    }
}
