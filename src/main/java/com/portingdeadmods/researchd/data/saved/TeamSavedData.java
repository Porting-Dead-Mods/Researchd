package com.portingdeadmods.researchd.data.saved;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class TeamSavedData extends SavedData {
    private static final Codec<TeamSavedData> CODEC = ResearchTeamMap.CODEC
            .fieldOf("map")
            .xmap(TeamSavedData::new, data -> data.map)
            .codec();
    private static final SavedDataType<TeamSavedData> TYPE =
            new SavedDataType<>(Researchd.rl("team_research"), TeamSavedData::new, CODEC);

    private final ResearchTeamMap map;

    public TeamSavedData(ResearchTeamMap map) {
        this.map = map;
        this.map.setOnChangedFunction(this::setDirty);
    }

    public TeamSavedData() {
        this(new ResearchTeamMap());
    }

    public static ResearchTeamMap getData(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(TYPE).map;
    }
}
