package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.resources.JsonRecipeOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Map;

public class ResearchdRecipes implements ResearchdRecipeProvider {
    private final JsonRecipeOutput output;
    private final String modid;

    public ResearchdRecipes(String modid) {
        this.output = new JsonRecipeOutput();
        this.modid = modid;
    }

    public void build() {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPackProvider.asStack(ResearchdResearchPacks.OVERWORLD_PACK_LOC))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.COBBLESTONE)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('D', Items.REDSTONE)
                .unlockedBy("has_item", ResearchdRecipeProvider.has(Items.GLASS))
                .save(this.output, Researchd.rl("overworld_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPackProvider.asStack(ResearchdResearchPacks.NETHER_PACK_LOC))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.NETHERRACK)
                .define('B', Items.BASALT)
                .define('C', Items.SOUL_SAND)
                .define('D', Items.GLOWSTONE_DUST)
                .unlockedBy("has_item", ResearchdRecipeProvider.has(Items.NETHERRACK))
                .save(this.output, Researchd.rl("nether_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPackProvider.asStack(ResearchdResearchPacks.END_PACK_LOC))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.END_STONE)
                .define('B', Items.PURPUR_BLOCK)
                .define('C', Items.OBSIDIAN)
                .define('D', Items.ENDER_PEARL)
                .unlockedBy("has_item", ResearchdRecipeProvider.has(Items.END_STONE))
                .save(this.output, Researchd.rl("end_pack"));
    }

    public Map<ResourceLocation, Recipe<?>> getContents() {
        return this.output.recipes();
    }
}
