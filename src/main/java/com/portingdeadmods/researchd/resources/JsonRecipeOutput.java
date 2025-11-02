package com.portingdeadmods.researchd.resources;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record JsonRecipeOutput(Map<ResourceLocation, Recipe<?>> recipes) implements RecipeOutput {
    public JsonRecipeOutput() {
        this(new HashMap<>());
    }

    @Override
    public Advancement.Builder advancement() {
        return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
    }

    @Override
    public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
        this.recipes.put(id, recipe);
    }

}
