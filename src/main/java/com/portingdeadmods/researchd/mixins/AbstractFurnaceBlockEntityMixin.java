package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.UnlockItemEffectData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @ModifyVariable(
            method = "serverTick",
            at = @At(value = "LOAD", target = "recipeholder"),
            name = "recipeholder"
    )
    private static RecipeHolder<?> modifyRecipeHolder(
            RecipeHolder<?> recipeholder,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) AbstractFurnaceBlockEntity blockEntity
    ) {
        if (recipeholder != null) {
            UUID playerUUID = blockEntity.getData(ResearchdAttachments.PLACED_BY_UUID);
            Player player = level.getPlayerByUUID(playerUUID);

            if (player != null) {
                UnlockItemEffectData itemData = player.getData(ResearchdAttachments.ITEM_PREDICATE.get());
                RecipeUnlockEffectData recipeData = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());

                Recipe<?> recipe = recipeholder.value();
                if (isItemBlocked(itemData, recipe, level)) {
                    Researchd.debug("Furnace Mixin", "Blocked due to item predicate.");
                    return null;
                }

                if (!recipeData.getAll().isEmpty()) {
                    Researchd.debug("Furnace Mixin", "RecipePredicateData: " + recipeData.blockedRecipes());
                    Researchd.debug("Furnace Mixin","RecipeHolder id: " + recipeholder.id());
                    if (recipeData.blockedRecipes().contains(recipeholder)) {
                        Researchd.debug("Furnace Mixin","Recipe for " + recipeholder.value().getResultItem(level.registryAccess()) + " blocked!");
                        return null;
                    }
                }
            }
        }
        return recipeholder;
    }

    private static boolean isItemBlocked(UnlockItemEffectData itemData, Recipe<?> recipe, Level level) {
        if (itemData.blockedItems().isEmpty()) {
            return false;
        }

        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (!result.isEmpty() && itemData.isBlocked(result)) {
            return true;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                continue;
            }

            for (ItemStack stack : ingredient.getItems()) {
                if (!stack.isEmpty() && itemData.isBlocked(stack)) {
                    return true;
                }
            }
        }

        return false;
    }
}