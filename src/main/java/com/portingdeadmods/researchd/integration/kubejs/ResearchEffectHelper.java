package com.portingdeadmods.researchd.integration.kubejs;

import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResearchEffectHelper {
    
    public static ResearchEffect empty() {
        return EmptyResearchEffect.INSTANCE;
    }
    
    public static ResearchEffect unlockRecipe(ResourceLocation recipeId) {
        return new RecipeUnlockEffect(recipeId);
    }
    
    public static ResearchEffect unlockRecipe(String recipeId) {
        return new RecipeUnlockEffect(ResourceLocation.parse(recipeId));
    }
    
    
    public static ResearchEffect unlockRecipes(String... recipeIds) {
        if (recipeIds.length == 0) {
            return EmptyResearchEffect.INSTANCE;
        }
        if (recipeIds.length == 1) {
            return new RecipeUnlockEffect(ResourceLocation.parse(recipeIds[0]));
        }
        List<ResearchEffect> list = new ArrayList<>();
        for (String id : recipeIds) {
            list.add(new RecipeUnlockEffect(ResourceLocation.parse(id)));
        }
        return new AndResearchEffect(list);
    }
    
    public static ResearchEffect unlockDimension(ResourceKey<Level> dimension) {
        return new DimensionUnlockEffect(dimension.location(), DimensionUnlockEffect.DEFAULT_SPRITE);
    }
    
    public static ResearchEffect unlockDimension(ResourceLocation dimension) {
        return new DimensionUnlockEffect(dimension, DimensionUnlockEffect.DEFAULT_SPRITE);
    }
    
    public static ResearchEffect unlockDimension(String dimension) {
        return new DimensionUnlockEffect(ResourceLocation.parse(dimension), DimensionUnlockEffect.DEFAULT_SPRITE);
    }
    
    public static ResearchEffect unlockDimensions(ResourceKey<Level>... dimensions) {
        if (dimensions.length == 0) {
            return EmptyResearchEffect.INSTANCE;
        }
        if (dimensions.length == 1) {
            return unlockDimension(dimensions[0]);
        }
        List<ResearchEffect> list = new ArrayList<>();
        for (ResourceKey<Level> dim : dimensions) {
            list.add(new DimensionUnlockEffect(dim.location(), DimensionUnlockEffect.DEFAULT_SPRITE));
        }
        return new AndResearchEffect(list);
    }
    
    public static ResearchEffect unlockNether() {
        return new DimensionUnlockEffect(Level.NETHER.location(), DimensionUnlockEffect.NETHER_SPRITE);
    }
    
    public static ResearchEffect unlockEnd() {
        return new DimensionUnlockEffect(Level.END.location(), DimensionUnlockEffect.END_SPRITE);
    }
    
    public static ResearchEffect and(ResearchEffect... effects) {
        List<ResearchEffect> list = new ArrayList<>();
        for (ResearchEffect effect : effects) {
            list.add(effect);
        }
        return new AndResearchEffect(list);
    }
    
    public static ResearchEffect combine(List<ResearchEffect> effects) {
        if (effects.isEmpty()) {
            return EmptyResearchEffect.INSTANCE;
        }
        if (effects.size() == 1) {
            return effects.get(0);
        }
        return new AndResearchEffect(new ArrayList<>(effects));
    }
}