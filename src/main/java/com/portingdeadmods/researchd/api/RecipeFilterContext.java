package com.portingdeadmods.researchd.api;

import com.portingdeadmods.researchd.impl.research.effect.data.ItemUnlockEffectData;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.UUID;

/**
 * Thread-local stack of who called frames pushed around code paths that
 * may invoke recipe lookups. The {@link com.portingdeadmods.researchd.mixins.RecipeManagerMixin}
 * reads the current frame and filters out recipes blocked for that team.
 * <p>
 * Pushers (in core): {@code BoundTickingBlockEntityMixin} for every ticking BE,
 * {@code CraftingMenuMixin} for player crafting. Addons may push their own
 * frames for code paths that don't tick through a BE.
 * <p>
 * Addons that need to filter recipe lookups which bypass the vanilla
 * {@link net.minecraft.world.item.crafting.RecipeManager} (e.g. mods with their own cached recipe
 * finders) should not re-implement the blocking rules. Instead they mixin into the foreign finder
 * and call {@link #isBlocked(RecipeHolder)} per result, and, if that finder caches a prebuilt
 * structure, scope its cache key with {@link #scopedKey(Object)} so each team keeps its own entry.
 */
public final class RecipeFilterContext {
    public record Frame(UUID teamId, Level level) {}

    private static final ThreadLocal<Deque<Frame>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private RecipeFilterContext() {}

    public static void push(UUID teamId, Level level) {
        STACK.get().push(new Frame(teamId, level));
    }

    public static void pop() {
        Deque<Frame> stack = STACK.get();
        if (!stack.isEmpty()) stack.pop();
    }

    public static @Nullable Frame current() {
        Deque<Frame> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    /**
     * @return {@code true} if {@code holder} is blocked for the team of the current frame.
     * Returns {@code false} when there is no active frame (no filtering in progress).
     */
    public static boolean isBlocked(RecipeHolder<?> holder) {
        Frame frame = current();
        return frame != null && isBlocked(holder, frame);
    }

    /**
     * Single source of truth for whether a recipe is blocked for a given frame: the recipe id is
     * blocked, or its result / any of its ingredients is a blocked item.
     */
    public static boolean isBlocked(RecipeHolder<?> holder, Frame frame) {
        Level level = frame.level();
        if (ResearchdApi.isRecipeBlocked(level, frame.teamId(), holder)) return true;

        ItemUnlockEffectData itemData = ResearchdApi.getEffectDataForTeam(
                level, frame.teamId(), ResearchdEffectDataTypes.ITEM_UNLOCK);
        if (itemData == null || itemData.blockedItems().isEmpty()) return false;

        Recipe<?> recipe = holder.value();
        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (!result.isEmpty() && itemData.isBlocked(result)) return true;

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) continue;
            for (ItemStack stack : ingredient.getItems()) {
                if (!stack.isEmpty() && itemData.isBlocked(stack)) return true;
            }
        }
        return false;
    }

    /**
     * Scopes a foreign cache key to the current frame's team, so addon finders that cache a
     * prebuilt structure (filtered recipe lists, tries, ...) keep a separate entry per team instead
     * of letting whichever team searched first decide the result for everyone.
     *
     * @return a team-scoped key while a frame is active, otherwise {@code delegate} unchanged.
     */
    public static Object scopedKey(Object delegate) {
        Frame frame = current();
        return frame == null ? delegate : new TeamScopedKey(delegate, frame.teamId());
    }

    private record TeamScopedKey(Object delegate, UUID teamId) {
        private TeamScopedKey {
            Objects.requireNonNull(delegate, "delegate");
            Objects.requireNonNull(teamId, "teamId");
        }
    }
}
