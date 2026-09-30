package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;

public class RecipesProvider extends RecipeProvider {
    protected RecipesProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ResearchdItems.RESEARCH_LAB, 1)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("III")
                .define('G', Items.GLASS_PANE)
                .define('I', Items.IRON_BLOCK)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_item", has(Items.COPPER_INGOT))
                .save(this.output, ResourceKey.create(Registries.RECIPE, Researchd.rl("research_lab")));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipesProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Researchd Recipes";
        }
    }
}
