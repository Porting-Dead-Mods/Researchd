package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
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
        RecipeUnlockEffectData data = player.getData(ResearchdAttachments.RECIPE_PREDICATE);

        CraftingInput craftinginput = craftSlots.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> recipeHolder = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, level, recipe);

        Researchd.debug("Crafting Mixin", "RecipePredicateData: " + data.blockedRecipes());

        if (recipeHolder.isPresent()) {
            Researchd.debug("Crafting Mixin","RecipeHolder id: " + recipeHolder.get().id());
            Researchd.debug("Crafting Mixin","RecipeHolder result: " + recipeHolder.get().value().getResultItem(level.registryAccess()));

            if (!data.blockedRecipes().isEmpty()) {
                if (data.blockedRecipes().contains(recipeHolder.get())) {
                    Researchd.debug("Crafting Mixin","Recipe for " + recipeHolder.get().value().getResultItem(level.registryAccess()) + " blocked!");
                    return ItemStack.EMPTY;
                }
            }
        } else {
            Researchd.debug("Crafting Mixin","Invalid recipes.");
        }

        return itemstack;
    }
}