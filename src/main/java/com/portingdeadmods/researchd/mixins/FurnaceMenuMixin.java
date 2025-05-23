package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.portingdeadmods.researchd.content.predicates.CraftingPredicateData;
import com.portingdeadmods.researchd.content.predicates.SmeltingPredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public class FurnaceMenuMixin {
    @Inject(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getMaxStackSize()I",
                    shift = At.Shift.AFTER
            )
    )
    private static void onRecipeCheck(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity,
                                      CallbackInfo ci, @Local(ordinal = 0) RecipeHolder<?> recipeholder) {
        if (recipeholder != null) {
            UUID playerUUID = blockEntity.getData(ResearchdAttachments.PLACED_BY_UUID);
            Player player = level.getPlayerByUUID(playerUUID);

            if (player != null) {
                SmeltingPredicateData data = player.getData(ResearchdAttachments.SMELTING_PREDICATE);
                if (data != null) {
                    //System.out.println("SmeltingPredicateData: " + data.blockedRecipes());
                    if (data.blockedRecipes().contains(recipeholder)) {
                        //System.out.println("Recipe for " + recipeholder + " blocked!");
                        recipeholder = null;
                    }
                } else {
                    //System.out.println("SmeltingPredicateData: null - Nothing to predicate");
                }
            } else {
                //System.out.println("Player: null - Nothing to predicate");
            }
        } else {
            //System.out.println("RecipeHolder: null - Nothing to predicate");
        }
    }
}
