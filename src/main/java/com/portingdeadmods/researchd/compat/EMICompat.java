package com.portingdeadmods.researchd.compat;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public final class EMICompat {
    public static void openRecipe(RecipeHolder<?> recipe) {
        EmiRecipe recipe1 = EmiApi.getRecipeManager().getRecipe(recipe.id());
        if (recipe1 != null) {
            EmiApi.displayRecipe(recipe1);
        }
    }

    public static void openRecipesByResult(ItemStack result) {
        EmiApi.displayRecipes(EmiStack.of(result));
    }
}
