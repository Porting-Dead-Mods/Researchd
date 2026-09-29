package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.api.RecipeFilterContext;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** Recipe lookups as another mod would make them. */
public final class TestRecipes {
    private TestRecipes() {}

    /** Runs {@code lookup} with {@code team} as the Team Context, as an addon's machine would. */
    public static <T> T underTeamContext(ResearchTeam team, Level level, Supplier<T> lookup) {
        RecipeFilterContext.push(team.getId(), level);
        try {
            return lookup.get();
        } finally {
            RecipeFilterContext.pop();
        }
    }

    /** The ids of every crafting recipe, as a mod listing them by type sees them. */
    public static Set<ResourceKey<Recipe<?>>> craftingRecipeIds(ServerLevel level) {
        return level.recipeAccess().recipeMap().byType(RecipeType.CRAFTING).stream()
                .map(RecipeHolder::id)
                .collect(Collectors.toSet());
    }

    /** The ids of every crafting recipe, read from the whole recipe collection rather than a by-type list. */
    public static Set<ResourceKey<Recipe<?>>> allCraftingRecipeIds(ServerLevel level) {
        return level.recipeAccess().getRecipes().stream()
                .filter(holder -> holder.value().getType() == RecipeType.CRAFTING)
                .map(RecipeHolder::id)
                .collect(Collectors.toSet());
    }
}
