package com.portingdeadmods.researchd.api;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/**
 * Thread-local stack of who called frames pushed around code paths that
 * may invoke recipe lookups. The {@link com.portingdeadmods.researchd.mixins.RecipeManagerMixin}
 * reads the current frame and filters out recipes blocked for that team.
 * <p>
 * Pushers (in core): {@code BoundTickingBlockEntityMixin} for every ticking BE,
 * {@code CraftingMenuMixin} for player crafting. Addons may push their own
 * frames for code paths that don't tick through a BE, which would need compat
 * from rdxcompat
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
}
