package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.UnlockItemEffectData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
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
        UnlockItemEffectData itemData = player.getData(ResearchdAttachments.ITEM_PREDICATE.get());
        RecipeUnlockEffectData recipeData = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());

        CraftingInput craftinginput = craftSlots.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> recipeHolder = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, level, recipe);

        Researchd.debug("Crafting Mixin", "Blocked items: " + itemData.blockedItems());
        Researchd.debug("Crafting Mixin", "RecipePredicateData: " + recipeData.blockedRecipes());

        if (recipeHolder.isPresent()) {
            RecipeHolder<CraftingRecipe> holder = recipeHolder.get();
            CraftingRecipe craftingRecipe = holder.value();
            Researchd.debug("Crafting Mixin","RecipeHolder id: " + holder.id());
            Researchd.debug("Crafting Mixin","RecipeHolder result: " + craftingRecipe.getResultItem(level.registryAccess()));

            if (isItemBlocked(itemData, craftingRecipe, level)) {
                Researchd.debug("Crafting Mixin", "Recipe uses blocked item, denying craft.");
                return ItemStack.EMPTY;
            }

            if (!recipeData.isEmpty() && recipeData.contains(holder)) {
                Researchd.debug("Crafting Mixin","Recipe for " + craftingRecipe.getResultItem(level.registryAccess()) + " blocked!");
                return ItemStack.EMPTY;
            }
        } else {
            Researchd.debug("Crafting Mixin","Invalid recipes.");
        }

        return itemstack;
    }

    private static boolean isItemBlocked(UnlockItemEffectData itemData, CraftingRecipe craftingRecipe, Level level) {
        if (itemData.blockedItems().isEmpty()) {
            return false;
        }

        ItemStack result = craftingRecipe.getResultItem(level.registryAccess());
        if (!result.isEmpty() && itemData.isBlocked(result)) {
            return true;
        }

        for (Ingredient ingredient : craftingRecipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                continue;
            }

            for (ItemStack ingredientStack : ingredient.getItems()) {
                if (!ingredientStack.isEmpty() && itemData.isBlocked(ingredientStack)) {
                    return true;
                }
            }
        }

        return false;
    }
}
