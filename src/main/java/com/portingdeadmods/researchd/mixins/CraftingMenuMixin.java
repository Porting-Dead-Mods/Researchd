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
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    public CraftingMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(
            method = "slotChangedCraftingGrid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void checkCraftingPredicate(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci, @Local ItemStack itemstack, @Local RecipeHolder<CraftingRecipe> recipeholder) {
        CraftingPredicateData data = player.getData(ResearchdAttachments.CRAFTING_PREDICATE);
        if (data.blockedRecipes().contains(recipeholder)) {
            itemstack = ItemStack.EMPTY;
        }
        System.out.println("CraftingPredicateData: " + data.blockedRecipes());
    }
}
