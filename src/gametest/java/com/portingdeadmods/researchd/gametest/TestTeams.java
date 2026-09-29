package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperServer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/** Research teams for GameTests. */
public final class TestTeams {
    private TestTeams() {}

    /**
     * A new team owned by {@code player}, with nothing researched and nothing queued, so every research's Research
     * Effect is Blocked for it, as for a player's default team.
     */
    public static ResearchTeam create(GameTestHelper helper, Player player) {
        ResearchTeamManager teams = ResearchdApi.getTeamManager(helper.getLevel());
        ResearchTeam team = teams.createEmptyTeam("GameTest " + player.getUUID());
        team.addMember(player.getUUID(), ResearchTeamRole.OWNER);
        team.init(helper.getLevel());
        teams.addTeam(team);
        ResearchTeamHelperServer.initializeTeamEffects(team, helper.getLevel());
        return team;
    }

    /**
     * Completes {@code research} for {@code team} and applies its Research Effect at once. This is what the admin
     * command that completes a research does, minus the packets it sends each online member: test players have no negotiated connection.
     */
    public static void complete(GameTestHelper helper, ResearchTeam team, ResourceKey<Research> research) {
        ServerLevel level = helper.getLevel();
        team.setResearchCompleted(research, level.getGameTime());
        ResearchdApi.getResearchManager().lookupResearch(research, level).researchEffect().onUnlock(level, team, research);
    }

    /** Queues {@code research}, which becomes the team's current research if the queue was empty. */
    public static void queue(GameTestHelper helper, ResearchTeam team, ResourceKey<Research> research) {
        helper.assertTrue(
                team.getQueue().add(team.getResearches().get(research)), "Could not queue " + research.identifier());
    }
}
