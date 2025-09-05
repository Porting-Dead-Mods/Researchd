package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import net.minecraft.world.level.Level;

public interface ValueEffect {
    ValueEffect DEFAULT = new ValueEffect() {
    };

    default void onUnlock(ResearchTeam team, Level level) {}

}
