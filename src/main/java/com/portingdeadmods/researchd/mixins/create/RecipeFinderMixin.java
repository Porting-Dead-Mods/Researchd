package com.portingdeadmods.researchd.mixins.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// get recipes -> filter all -> return list
@Mixin(targets = "com.simibubi.create.foundation.recipe.RecipeFinder", remap = false)
public class RecipeFinderMixin {
    @WrapMethod(method = "get")
    private static List<RecipeHolder<? extends Recipe<?>>> researchd$filterBlocked(
            Object cacheKey, Level level, Predicate<RecipeHolder<? extends Recipe<?>>> conditions,
            Operation<List<RecipeHolder<? extends Recipe<?>>>> original) {
        List<RecipeHolder<? extends Recipe<?>>> result = original.call(cacheKey, level, conditions);

        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null || result.isEmpty()) {
            return result;
        }

        List<RecipeHolder<? extends Recipe<?>>> filtered = null;
        for (int i = 0; i < result.size(); i++) {
            RecipeHolder<? extends Recipe<?>> holder = result.get(i);
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
