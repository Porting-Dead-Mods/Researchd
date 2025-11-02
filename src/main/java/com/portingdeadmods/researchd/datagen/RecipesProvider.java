package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPacks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RecipesProvider extends RecipeProvider {
    public RecipesProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdItems.RESEARCH_LAB, 1)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("III")
                .define('G', Items.GLASS_PANE)
                .define('I', Items.IRON_BLOCK)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_item", has(Items.COPPER_INGOT))
                .save(pRecipeOutput, Researchd.rl("research_lab"));

    }
}
