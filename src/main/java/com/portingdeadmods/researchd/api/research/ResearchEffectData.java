package com.portingdeadmods.researchd.api.research;

import net.minecraft.world.level.Level;

public interface ResearchEffectData<T extends ResearchEffect> {
    /**
     * This method should return a 'default' instance of the Data class
     * As a default, it should hold all the effects as if they weren't researched yet.
     */
    ResearchEffectData<T> getDefault(Level level);
}
