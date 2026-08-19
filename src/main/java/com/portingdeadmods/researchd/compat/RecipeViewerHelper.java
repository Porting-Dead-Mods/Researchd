package com.portingdeadmods.researchd.compat;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeViewerHelper {
    public static void openRecipe(RecipeHolder<?> recipe) {
        if (ResearchdCompatHandler.isEmiLoaded()) {
            EMICompat.openRecipe(recipe);
        } else if (ResearchdCompatHandler.isJeiLoaded()) {
            JEICompat.openRecipes(List.of(recipe));
        }
    }

    public static void openRecipesByResult(ItemStack result) {
        if (ResearchdCompatHandler.isEmiLoaded()) {
            EMICompat.openRecipesByResult(result);
        } else if (ResearchdCompatHandler.isJeiLoaded()) {
            JEICompat.openRecipesFor(List.of(result));
        }
    }
}
