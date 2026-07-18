package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.impl.team.TeamIterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface ResearchTeamManager {
    /**
     * @return the team with the given team id, or null if no such team exists
     */
    @Nullable ResearchTeam getTeamById(UUID uuid);

    /**
     * @return the team with the given display name, or null if no such team exists
     */
    @Nullable ResearchTeam getTeamByName(String name);

    /**
     * @return the team the given player is a member of, or null if the player has no team
     */
    @Nullable ResearchTeam getTeamByPlayerId(UUID playerId);

    /**
     * @return the team the given player is a member of, or null if the player has no team
     */
    default @Nullable ResearchTeam getTeamByPlayer(@NotNull Player player) {
        return this.getTeamByPlayerId(player.getUUID());
    }

    @NotNull Collection<UUID> getTeamIds();

    default @NotNull Iterable<ResearchTeam> getTeams() {
        return new TeamIterator.Iterable(this, this.getTeamIds().iterator());
    }

    @NotNull ResearchTeam createEmptyTeam(String name);

    @NotNull ResearchTeam createDefaultTeam(UUID playerId, Level level);

    default @NotNull ResearchTeam createDefaultTeam(@NotNull Player player) {
        return this.createDefaultTeam(player.getUUID(), player.level());
    }

    void addTeam(ResearchTeam team);

    void removeTeam(UUID teamId);

}
