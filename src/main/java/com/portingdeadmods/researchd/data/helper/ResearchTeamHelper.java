package com.portingdeadmods.researchd.data.helper;

import com.portingdeadmods.researchd.data.ResearchdSavedData;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ResearchTeamHelper {
    public static @NotNull ResearchTeam getOrCreateTeamForUUID(ServerLevel serverLevel, UUID uuid) {
        ResearchTeamMap map = ResearchdSavedData.TEAM_RESEARCH.get().getData(serverLevel);
        ResearchTeam researchTeam = map.getResearchTeams().get(uuid);
        if (researchTeam == null) {
            researchTeam = new ResearchTeam(uuid);
            map.getResearchTeams().put(uuid, researchTeam);
            ResearchdSavedData.TEAM_RESEARCH.get().setData(serverLevel, map);
        }
        return researchTeam;
    }
}
