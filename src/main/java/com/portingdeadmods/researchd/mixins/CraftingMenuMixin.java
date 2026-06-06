package com.portingdeadmods.researchd.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {

    @WrapMethod(method = "slotChangedCraftingGrid")
    private static void researchd$pushOwnerContext(
            AbstractContainerMenu menu, Level level, Player player,
            CraftingContainer craftSlots, ResultContainer resultSlots,
            RecipeHolder<CraftingRecipe> recipe, Operation<Void> original) {

        if (level.isClientSide) {
            original.call(menu, level, player, craftSlots, resultSlots, recipe);
            return;
        }

        ResearchTeamManager mgr = ResearchdApi.getTeamManager(level);
        ResearchTeam team = mgr == null ? null : mgr.getTeamByPlayer(player);
        if (team == null) {
            original.call(menu, level, player, craftSlots, resultSlots, recipe);
            return;
        }

        Researchd.debug("Recipe Filter", "Crafting push for ", player.getName().getString(), " team=", team.getId());
        RecipeFilterContext.push(team.getId(), level);
        try {
            original.call(menu, level, player, craftSlots, resultSlots, recipe);
        } finally {
            RecipeFilterContext.pop();
        }
    }
}
