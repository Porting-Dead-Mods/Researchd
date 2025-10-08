package com.portingdeadmods.researchd.compat;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public class JEICompat {
    public static IJeiRuntime RUNTIME;

    public static void openRecipes(Collection<RecipeHolder<?>> recipes) {
        if (RUNTIME != null) {
            IFocusFactory focusFactory = RUNTIME.getJeiHelpers().getFocusFactory();
            IIngredientManager ingredientManager = RUNTIME.getIngredientManager();
            Map<Item, IFocus<?>> focuses = new HashMap<>();
            for (RecipeHolder<?> recipe : recipes) {
                ItemStack stack = recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess());
                Optional<ITypedIngredient<ItemStack>> ingredient = ingredientManager.createTypedIngredient(stack, false);
                //noinspection OptionalIsPresent
                if (ingredient.isPresent()) {
                    focuses.put(stack.getItem(), focusFactory.createFocus(RecipeIngredientRole.OUTPUT, ingredient.get()));
                }
            }
            IRecipesGui recipesGui = RUNTIME.getRecipesGui();
            recipesGui.show(List.copyOf(focuses.values()));
        }
    }

    public static void openRecipes(List<ItemStack> results) {
        if (RUNTIME != null) {
            IFocusFactory focusFactory = RUNTIME.getJeiHelpers().getFocusFactory();
            IIngredientManager ingredientManager = RUNTIME.getIngredientManager();
            Map<Item, IFocus<?>> focuses = new HashMap<>();
            for (ItemStack result : results) {
                Optional<ITypedIngredient<ItemStack>> ingredient = ingredientManager.createTypedIngredient(result, false);
                //noinspection OptionalIsPresent
                if (ingredient.isPresent()) {
                    focuses.put(result.getItem(), focusFactory.createFocus(RecipeIngredientRole.OUTPUT, ingredient.get()));
                }
            }
            IRecipesGui recipesGui = RUNTIME.getRecipesGui();
            recipesGui.show(List.copyOf(focuses.values()));
        }
    }
}
