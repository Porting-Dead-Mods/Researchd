package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.portingdeadmods.researchd.content.predicates.CraftingPredicateData;
import com.portingdeadmods.researchd.content.predicates.DimensionPredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {
    @Inject(
            method = "slotChangedCraftingGrid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void checkCraftingPredicate(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci, @Local ItemStack itemstack) {
        CraftingPredicateData data = player.getData(ResearchdAttachments.CRAFTING_PREDICATE);
        RecipeHolder<CraftingRecipe> recipeholder = null;

        // Gotta remake recipeholder since for some reason @Local population fails :(((((((((
        CraftingInput craftinginput = craftSlots.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, level, recipe);

        if (optional.isPresent()) {
            recipeholder = optional.get();
            if (data != null && recipeholder != null) {
                if (data.blockedRecipes().contains(recipeholder)) {
                    itemstack = ItemStack.EMPTY;
                    //System.out.println("Recipe for " + recipeholder + " blocked!");
                }
            }
        }

        if (data == null) System.out.println("CraftingPredicateData: null");
        //else System.out.println("CraftingPredicateData: " + data.blockedRecipes());

        if (recipeholder == null) System.out.println("RecipeHolder: null");
        else {
            //System.out.println("RecipeHolder id: " + recipeholder.id());
            //System.out.println("RecipeHolder value: " + recipeholder.value());
        }
    }
}
