package com.portingdeadmods.researchd.api.research.effects;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.world.level.Level;

public interface ResearchEffectData<T extends ResearchEffect> {
    /**
     * This method should return a 'default' instance of the Data class
     * As a default, it should hold all the effects as if they weren't researched yet.
     */
    ResearchEffectData<T> getDefault(Level level);

    // Storage methods
    ResearchEffectData<T> add(T effect, Level level);

    ResearchEffectData<T> remove(T effect, Level level);

    UniqueArray<?> getAll();

}
