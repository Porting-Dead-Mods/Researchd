package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Taking a crafting output looks the recipe up again to find what stays in the grid. Under the crafting player's
 * Team Context it finds the recipe that made the output, not a Blocked one it fell through.
 */
@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {

    @WrapMethod(method = "onTake")
    private void researchd$pushOwnerContext(Player player, ItemStack carried, Operation<Void> original) {
        RecipeFilterContext.runAs(player, player.level(), () -> original.call(player, carried));
    }
}
