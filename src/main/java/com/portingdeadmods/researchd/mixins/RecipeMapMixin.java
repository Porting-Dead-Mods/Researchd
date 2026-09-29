package com.portingdeadmods.researchd.mixins;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import com.portingdeadmods.researchd.impl.FilteredRecipeLists;
import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Drops recipes that are Blocked for the current Team Context from recipe lookups and recipe lists.
 * <p>
 * {@link RecipeMap#byType} lists have no input to go by; they are cached per team, see {@link FilteredRecipeLists}.
 * Every lookup by input goes through {@link RecipeMap#getRecipesFor}, which searches such a list in priority order, so
 * a Blocked match falls through to the next one. Its matches are checked again against the input they would craft
 * from.
 */
@Mixin(RecipeMap.class)
public abstract class RecipeMapMixin {
    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Unique private final FilteredRecipeLists researchd$filteredLists = new FilteredRecipeLists();

    @ModifyReturnValue(method = "byType", at = @At("RETURN"))
    private <T extends Recipe<?>> Collection<RecipeHolder<T>> researchd$filterList(
            Collection<RecipeHolder<T>> recipes, RecipeType<T> type) {
        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null) return recipes;
        return this.researchd$filteredLists.get(frame, type, this.byType, recipes);
    }

    @ModifyReturnValue(method = "getRecipesFor", at = @At("RETURN"))
    private <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> researchd$filterMatches(
            Stream<RecipeHolder<T>> matches, RecipeType<T> type, I input) {
        RecipeFilterContext.Frame frame = RecipeFilterContext.current();
        if (frame == null) return matches;
        return matches.filter(holder -> !RecipeFilterContext.isBlocked(holder, frame, input));
    }
}
