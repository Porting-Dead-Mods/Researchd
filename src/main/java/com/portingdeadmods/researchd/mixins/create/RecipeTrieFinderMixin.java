package com.portingdeadmods.researchd.mixins.create;

import com.google.common.cache.Cache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.Callable;

@Mixin(targets = "com.simibubi.create.foundation.recipe.trie.RecipeTrieFinder", remap = false)
public class RecipeTrieFinderMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @WrapOperation(
            method = "get",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/cache/Cache;get(Ljava/lang/Object;Ljava/util/concurrent/Callable;)Ljava/lang/Object;",
                    remap = false
            )
    )
    private static Object researchd$scopeKeyToTeam(Cache cache, Object key, Callable loader, Operation<Object> original) {
        return original.call(cache, RecipeFilterContext.scopedKey(key), loader);
    }
}
