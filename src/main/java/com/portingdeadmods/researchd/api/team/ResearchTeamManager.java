package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.utils.TeamIterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.UUID;

public interface ResearchTeamManager {
    ResearchTeam getTeamById(UUID uuid);

    ResearchTeam getTeamByName(String name);

    ResearchTeam getTeamByPlayerId(UUID playerId);

    default ResearchTeam getTeamByPlayer(Player player) {
        return this.getTeamByPlayerId(player.getUUID());
    }

    Collection<UUID> getTeamIds();

    default Iterable<ResearchTeam> getTeams() {
        return new TeamIterator.Iterable(this, this.getTeamIds().iterator());
    }

    ResearchTeam createEmptyTeam(String name);

    ResearchTeam createDefaultTeam(UUID playerId, Level level);

    default ResearchTeam createDefaultTeam(Player player) {
        return this.createDefaultTeam(player.getUUID(), player.level());
    }

    void addTeam(ResearchTeam team);

    void removeTeam(UUID teamId);

}
