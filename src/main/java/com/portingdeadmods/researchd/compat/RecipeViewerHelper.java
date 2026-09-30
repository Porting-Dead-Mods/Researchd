package com.portingdeadmods.researchd.compat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

// TODO(26.1 port, JEI): open recipes (by id and by result) in JEI again (JEICompat) once the JEI integration
// is ported. TODO(26.1 port, EMI): the same through EMICompat once EMI has a 26.1.2 build.
public final class RecipeViewerHelper {
    public static void openRecipe(ResourceKey<Recipe<?>> recipe) {}

    public static void openRecipesByResult(ItemStack result) {}
}
