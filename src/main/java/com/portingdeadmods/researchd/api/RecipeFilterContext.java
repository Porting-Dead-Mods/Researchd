package com.portingdeadmods.researchd.api;

import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.impl.research.effect.data.ItemUnlockEffectData;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Thread-local stack of who called frames pushed around code paths that
 * may invoke recipe lookups. {@code RecipeMapMixin} and {@code RecipeManagerMixin}
 * read the current frame and filter out recipes Blocked for that team.
 * <p>
 * Pushers (in core): {@code CraftingMenuMixin} and {@code ResultSlotMixin} for player crafting, and
 * {@code BoundTickingBlockEntityMixin}, which pushes the placer's team for every ticking BE. Addons may push their own
 * frames for code paths that don't tick through a BE.
 * <p>
 * Addons that need to filter recipe lookups which bypass the vanilla
 * {@link net.minecraft.world.item.crafting.RecipeManager} (e.g. mods with their own cached recipe
 * finders) should not re-implement the blocking rules. Instead they mixin into the foreign finder
 * and call {@link #isBlocked(RecipeHolder)} per result (or, when they have the input, {@link
 * #isBlocked(RecipeHolder, Frame, RecipeInput)} with the current frame), and, if that finder caches a prebuilt
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

    /**
     * Runs {@code action} with {@code player}'s team as the Team Context, or with no frame when the player has no
     * team or {@code level} is client-side.
     */
    public static void runAs(Player player, Level level, Runnable action) {
        ResearchTeamManager teams = level.isClientSide() ? null : ResearchdApi.getTeamManager(level);
        ResearchTeam team = teams == null ? null : teams.getTeamByPlayer(player);
        if (team == null) {
            action.run();
            return;
        }

        push(team.getId(), level);
        try {
            action.run();
        } finally {
            pop();
        }
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
     * Whether {@code holder} is Blocked for {@code frame}'s team when it would craft from {@code input}: its id is
     * Blocked, the result it makes is a Blocked item, or an item in {@code input} is a Blocked item. The result is
     * what the recipe assembles from {@code input} as well as what its display shows, since some recipes (fireworks,
     * for one) show none.
     */
    public static <I extends RecipeInput> boolean isBlocked(
            RecipeHolder<? extends Recipe<I>> holder, Frame frame, I input) {
        Level level = frame.level();
        if (ResearchdApi.isRecipeBlocked(level, frame.teamId(), holder.id())) return true;

        ItemUnlockEffectData items = blockedItems(frame);
        if (items == null) return false;
        if (consumesBlockedItem(input, items) || displaysBlockedResult(holder.value(), level, items)) return true;

        ItemStack assembled = holder.value().assemble(input);
        return !assembled.isEmpty() && items.isBlocked(assembled);
    }

    /**
     * Whether {@code holder} is Blocked for {@code frame}'s team, with no input to go by: its id is Blocked, the
     * result its display shows is a Blocked item, or one of its ingredients accepts only Blocked items, so that no
     * input could satisfy it.
     */
    public static boolean isBlocked(RecipeHolder<?> holder, Frame frame) {
        Level level = frame.level();
        if (ResearchdApi.isRecipeBlocked(level, frame.teamId(), holder.id())) return true;

        ItemUnlockEffectData items = blockedItems(frame);
        if (items == null) return false;
        return displaysBlockedResult(holder.value(), level, items) || needsBlockedItem(holder.value(), items);
    }

    /** The team's Blocked items, or {@code null} when none are Blocked. */
    private static @Nullable ItemUnlockEffectData blockedItems(Frame frame) {
        ItemUnlockEffectData items =
                ResearchdApi.getEffectDataForTeam(frame.level(), frame.teamId(), ResearchdEffectDataTypes.ITEM_UNLOCK);
        return items == null || items.blockedItems().isEmpty() ? null : items;
    }

    private static boolean needsBlockedItem(Recipe<?> recipe, ItemUnlockEffectData items) {
        for (Ingredient ingredient : recipe.placementInfo().ingredients()) {
            List<Holder<Item>> accepted = ingredient.items().toList();
            if (!accepted.isEmpty() && accepted.stream().allMatch(item -> items.isBlocked(item.value()))) return true;
        }
        return false;
    }

    private static boolean consumesBlockedItem(RecipeInput input, ItemUnlockEffectData items) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && items.isBlocked(stack)) return true;
        }
        return false;
    }

    private static boolean displaysBlockedResult(Recipe<?> recipe, Level level, ItemUnlockEffectData items) {
        ContextMap context = SlotDisplayContext.fromLevel(level);
        for (RecipeDisplay display : recipe.display()) {
            for (ItemStack result : display.result().resolveForStacks(context)) {
                if (!result.isEmpty() && items.isBlocked(result)) return true;
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
