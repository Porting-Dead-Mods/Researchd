package com.portingdeadmods.researchd.mixins.immersiveengineering;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

// get recipes -> filter all -> return list
@Mixin(targets = "blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList", remap = false)
public class CachedRecipeListMixin {
    @WrapMethod(method = "getRecipes")
    private List<RecipeHolder<?>> researchd$filterBlocked(Level level, Operation<List<RecipeHolder<?>>> original) {
        List<RecipeHolder<?>> result = original.call(level);

        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null || result.isEmpty()) {
            return result;
        }

        List<RecipeHolder<?>> filtered = null;
        for (int i = 0; i < result.size(); i++) {
            RecipeHolder<?> holder = result.get(i);
            if (RecipeFilterContext.isBlocked(holder, frame)) {
                if (filtered == null) {
                    filtered = new ArrayList<>(result.size());
                    for (int j = 0; j < i; j++) filtered.add(result.get(j));
                }
            } else if (filtered != null) {
                filtered.add(holder);
            }
        }
        return filtered != null ? filtered : result;
    }
}
