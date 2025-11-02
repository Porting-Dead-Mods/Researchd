package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.resources.JsonRecipeOutput;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import java.util.*;

public class ResearchdRecipes {
    private final JsonRecipeOutput output;
    private final String modid;

    public ResearchdRecipes(String modid) {
        this.output = new JsonRecipeOutput();
        this.modid = modid;
    }

    public void build() {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPacks.asStack(ResearchdResearchPacks.OVERWORLD_PACK_LOC))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.COBBLESTONE)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('D', Items.REDSTONE)
                .unlockedBy("has_item", has(Items.GLASS))
                .save(this.output, Researchd.rl("overworld_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPacks.asStack(ResearchdResearchPacks.NETHER_PACK_LOC))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.NETHERRACK)
                .define('B', Items.BASALT)
                .define('C', Items.SOUL_SAND)
                .define('D', Items.GLOWSTONE_DUST)
                .unlockedBy("has_item", has(Items.NETHERRACK))
                .save(this.output, Researchd.rl("nether_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ResearchdResearchPacks.asStack(ResearchdResearchPacks.END_PACK_LOC))
                .pattern(" A ")
                .pattern("BGC")
                .pattern(" D ")
                .define('G', Items.GLASS_BOTTLE)
                .define('A', Items.END_STONE)
                .define('B', Items.PURPUR_BLOCK)
                .define('C', Items.OBSIDIAN)
                .define('D', Items.ENDER_PEARL)
                .unlockedBy("has_item", has(Items.END_STONE))
                .save(this.output, Researchd.rl("end_pack"));

    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
        return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
    }

    public Map<ResourceLocation, Recipe<?>> getContents() {
        return this.output.recipes();
    }
}
