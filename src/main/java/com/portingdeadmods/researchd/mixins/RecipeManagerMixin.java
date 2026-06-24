package com.portingdeadmods.researchd.mixins;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;",
            at = @At("RETURN"),
            cancellable = true)
    private void researchd$filterSingle(CallbackInfoReturnable<Optional<RecipeHolder<?>>> cir) {
        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null) return;

        Optional<RecipeHolder<?>> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) return;

        RecipeHolder<?> holder = result.get();
        if (researchd$isBlocked(holder, frame)) {
            Researchd.debug("Recipe Filter", "getRecipeFor blocked ", holder.id(), " for team ", frame.teamId());
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(
            method = "getAllRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true)
    private void researchd$filterAll(CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null) return;

        List<RecipeHolder<?>> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) return;

        List<RecipeHolder<?>> filtered = null;
        int blocked = 0;
        for (int i = 0; i < result.size(); i++) {
            RecipeHolder<?> holder = result.get(i);
            if (researchd$isBlocked(holder, frame)) {
                blocked++;
                if (filtered == null) {
                    filtered = new ArrayList<>(result.size());
                    for (int j = 0; j < i; j++) filtered.add(result.get(j));
                }
            } else if (filtered != null) {
                filtered.add(holder);
            }
        }
        if (filtered != null) {
            Researchd.debug("Recipe Filter", "getAllRecipesFor dropped ", blocked, "/", result.size(), " recipes for team ", frame.teamId());
            cir.setReturnValue(filtered);
        }
    }

    @Unique
    private static boolean researchd$isBlocked(RecipeHolder<?> holder, RecipeFilterContext.Frame frame) {
        return RecipeFilterContext.isBlocked(holder, frame);
    }
}
