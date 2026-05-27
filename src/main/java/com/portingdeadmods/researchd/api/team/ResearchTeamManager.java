package com.portingdeadmods.researchd.api.team;

import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.UUID;

public interface ResearchTeamManager {
    ResearchTeam getTeamById(UUID uuid);

    ResearchTeam getTeamByName(String name);

    ResearchTeam getTeamByPlayerId(UUID playerId);

    default ResearchTeam getTeamByPlayer(Player player) {
        return this.getTeamByPlayerId(player.getUUID());
    }

    Collection<ResearchTeam> getTeams();

}
