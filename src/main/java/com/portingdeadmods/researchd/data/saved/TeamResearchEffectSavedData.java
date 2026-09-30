package com.portingdeadmods.researchd.data.saved;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class TeamResearchEffectSavedData extends SavedData {
    private static final Codec<TeamResearchEffectSavedData> CODEC = TeamResearchEffectDataMap.CODEC
            .fieldOf("map")
            .xmap(TeamResearchEffectSavedData::new, data -> data.map)
            .codec();
    private static final SavedDataType<TeamResearchEffectSavedData> TYPE =
            new SavedDataType<>(Researchd.rl("team_research_effect_data"), TeamResearchEffectSavedData::new, CODEC);

    private final TeamResearchEffectDataMap map;

    public TeamResearchEffectSavedData() {
        this(new TeamResearchEffectDataMap());
    }

    public TeamResearchEffectSavedData(TeamResearchEffectDataMap map) {
        this.map = map;
        this.map.setOnChangedFunction(this::setDirty);
    }

    public static TeamResearchEffectDataMap getData(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(TYPE).map;
    }
}
