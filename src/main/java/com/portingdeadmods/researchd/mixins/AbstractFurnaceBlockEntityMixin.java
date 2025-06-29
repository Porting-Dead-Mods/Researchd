package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.predicates.RecipePredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
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
                RecipePredicateData data = player.getData(ResearchdAttachments.RECIPE_PREDICATE);
                if (!data.getAll().isEmpty()) {
                    Researchd.debug("Furnace Mixin", "RecipePredicateData: " + data.blockedRecipes());
                    Researchd.debug("Furnace Mixin","RecipeHolder id: " + recipeholder.id());
                    if (data.blockedRecipes().contains(recipeholder)) {
                        Researchd.debug("Furnace Mixin","Recipe for " + recipeholder.value().getResultItem(level.registryAccess()) + " blocked!");
                        return null;
                    }
                }
            }
        }
        return recipeholder;
    }
}