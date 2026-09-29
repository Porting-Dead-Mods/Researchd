package com.portingdeadmods.researchd.impl;

import com.google.common.collect.Multimap;
import com.portingdeadmods.researchd.api.RecipeFilterContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * One recipe map's by-type recipe lists with the recipes Blocked for a team dropped, kept per (team, recipe type) so a
 * machine that lists recipes every tick doesn't check every recipe every tick.
 * <p>
 * Every list is dropped whenever any team's Research Effect data changes ({@link #effectDataChanged}) and whenever the
 * recipe map's lists are replaced (NeoForge reorders them by recipe priority). A datapack reload builds a new recipe map, and with it a new instance of this. A
 * list doesn't depend on the level it was first built for: only the level's registries go into the check.
 */
public final class FilteredRecipeLists {
    private static final AtomicInteger EFFECT_DATA_REVISION = new AtomicInteger();

    private final Map<Key, List<? extends RecipeHolder<?>>> lists = new HashMap<>();
    private int revision = EFFECT_DATA_REVISION.get();
    private Multimap<?, ?> source;

    /** Drops every team's lists, in every recipe map. Call it after changing any team's Research Effect data. */
    public static void effectDataChanged() {
        EFFECT_DATA_REVISION.incrementAndGet();
    }

    /**
     * {@code unfiltered} without the recipes Blocked for {@code frame}'s team.
     *
     * @param source the recipe map's by-type lists that {@code unfiltered} comes from; when it is replaced, every list
     *               is rebuilt
     */
    @SuppressWarnings("unchecked")
    public synchronized <T extends Recipe<?>> List<RecipeHolder<T>> get(
            RecipeFilterContext.Frame frame,
            RecipeType<T> type,
            Multimap<?, ?> source,
            Collection<RecipeHolder<T>> unfiltered) {
        int current = EFFECT_DATA_REVISION.get();
        if (current != this.revision || source != this.source) {
            this.lists.clear();
            this.revision = current;
            this.source = source;
        }

        Key key = new Key(frame.teamId(), type);
        List<RecipeHolder<T>> list = (List<RecipeHolder<T>>) this.lists.get(key);
        if (list == null) {
            list = unfiltered.stream()
                    .filter(holder -> !RecipeFilterContext.isBlocked(holder, frame))
                    .toList();
            this.lists.put(key, list);
        }
        return list;
    }

    private record Key(UUID teamId, RecipeType<?> type) {}
}
