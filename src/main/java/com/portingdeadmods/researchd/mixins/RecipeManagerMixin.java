package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.api.RecipeFilterContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * A lookup returns its recipe hint without searching when the hint matches, which would skip
 * {@link RecipeMapMixin}'s filter. A hint that is Blocked for the current Team Context is dropped instead, so the
 * lookup searches as if it had none.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @ModifyVariable(
            method =
                    "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;",
            at = @At("HEAD"),
            argsOnly = true)
    private <I extends RecipeInput, T extends Recipe<I>> @Nullable RecipeHolder<T> researchd$dropBlockedHint(
            @Nullable RecipeHolder<T> recipeHint, RecipeType<T> type, I input, Level level) {
        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null || recipeHint == null) return recipeHint;
        return RecipeFilterContext.isBlocked(recipeHint, frame, input) ? null : recipeHint;
    }
}
