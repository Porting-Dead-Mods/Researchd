package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Makes the crafting player's team the Team Context while the server works out a crafting grid's output. Covers the
 * crafting table and the player's inventory grid, which both go through here.
 */
@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {

    @WrapMethod(method = "slotChangedCraftingGrid")
    private static void researchd$pushOwnerContext(
            AbstractContainerMenu menu,
            ServerLevel level,
            Player player,
            CraftingContainer craftSlots,
            ResultContainer resultSlots,
            @Nullable RecipeHolder<CraftingRecipe> recipeHint,
            Operation<Void> original) {
        RecipeFilterContext.runAs(
                player, level, () -> original.call(menu, level, player, craftSlots, resultSlots, recipeHint));
    }
}
