package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.registries.ResearchdResearchPacks;
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
        ItemStack basePack = ResearchdItems.RESEARCH_PACK.asItem().getDefaultInstance();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPacks.asStack(ResearchdResearchPacks.OVERWORLD))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.COBBLESTONE)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('D', Items.REDSTONE)
                .unlockedBy("has_item", has(Items.GLASS))
                .save(pRecipeOutput, Researchd.rl("overworld_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPacks.asStack(ResearchdResearchPacks.NETHER))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.NETHERRACK)
                .define('B', Items.BASALT)
                .define('C', Items.SOUL_SAND)
                .define('D', Items.GLOWSTONE_DUST)
                .unlockedBy("has_item", has(Items.NETHERRACK))
                .save(pRecipeOutput, Researchd.rl("nether_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPacks.asStack(ResearchdResearchPacks.END))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.END_STONE)
                .define('B', Items.PURPUR_BLOCK)
                .define('C', Items.OBSIDIAN)
                .define('D', Items.ENDER_PEARL)
                .unlockedBy("has_item", has(Items.END_STONE))
                .save(pRecipeOutput, Researchd.rl("end_pack"));

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
