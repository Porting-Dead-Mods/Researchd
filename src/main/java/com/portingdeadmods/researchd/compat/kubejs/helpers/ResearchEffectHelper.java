package com.portingdeadmods.researchd.compat.kubejs.helpers;

import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ResearchEffectHelper {
    
    public static ResearchEffect empty() {
        return EmptyResearchEffect.INSTANCE;
    }
    
    public static ResearchEffect unlockRecipe(String recipeId) {
        return new RecipeUnlockEffect(ResourceLocation.parse(recipeId));
    }

    public static ResearchEffect unlockRecipes(String... recipeIds) {
        if (recipeIds.length == 0) {
            return EmptyResearchEffect.INSTANCE;
        }
        if (recipeIds.length == 1) {
            return unlockRecipe(recipeIds[0]);
        }
        List<ResearchEffect> list = new ArrayList<>();
        for (String id : recipeIds) {
            list.add(unlockRecipe(id));
        }
        return new AndResearchEffect(list);
    }

    public static ResearchEffect unlockItem(String itemId) {
        return new ItemUnlockEffect(ResourceLocation.parse(itemId));
    }

    public static ResearchEffect unlockItems(String... itemIds) {
        if (itemIds.length == 0) {
            return EmptyResearchEffect.INSTANCE;
        }
        if (itemIds.length == 1) {
            return unlockItem(itemIds[0]);
        }
        List<ResearchEffect> list = new ArrayList<>();
        for (String itemId : itemIds) {
            list.add(unlockItem(itemId));
        }
        return new AndResearchEffect(list);
    }
    
    public static ResearchEffect unlockDimension(String dimension) {
        return new DimensionUnlockEffect(ResourceLocation.parse(dimension), DimensionUnlockEffect.DEFAULT_SPRITE);
    }
    
    public static ResearchEffect unlockDimensions(String... dimensions) {
        if (dimensions.length == 0) {
            return EmptyResearchEffect.INSTANCE;
        }
        if (dimensions.length == 1) {
            return unlockDimension(dimensions[0]);
        }
        List<ResearchEffect> list = new ArrayList<>();
        for (String dim : dimensions) {
            list.add(unlockDimension(dim));
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
