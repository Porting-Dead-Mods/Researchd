package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.predicates.RecipePredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {

    @ModifyVariable(
            method = "slotChangedCraftingGrid",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
                    shift = At.Shift.BEFORE),
            ordinal = 0
    )
    private static ItemStack checkCraftingPredicate(ItemStack itemstack, AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe) {
        RecipePredicateData data = player.getData(ResearchdAttachments.RECIPE_PREDICATE);

        CraftingInput craftinginput = craftSlots.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> recipeHolder = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, level, recipe);

        Researchd.debug("(Crafting Menu)");
        Researchd.debug("RecipePredicateData: " + data.blockedRecipes());

        if (recipeHolder.isPresent()) {
            Researchd.debug("RecipeHolder id: " + recipeHolder.get().id());
            Researchd.debug("RecipeHolder result: " + recipeHolder.get().value().getResultItem(level.registryAccess()));

            if (!data.blockedRecipes().isEmpty()) {
                if (data.blockedRecipes().contains(recipeHolder.get())) {
                    Researchd.debug("Recipe for " + recipeHolder.get().value().getResultItem(level.registryAccess()) + " blocked!");
                    return ItemStack.EMPTY;
                }
            }
        } else {
            Researchd.debug("Invalid recipe.");
        }

        return itemstack;
    }
}