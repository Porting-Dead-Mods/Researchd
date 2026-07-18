package com.portingdeadmods.researchd.compat.ftbteams;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperServer;
import dev.ftb.mods.ftbteams.api.event.PlayerChangedTeamEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.api.event.TeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class FTBTeamsCompat {
    // Change Team also Handles Leave Team
    public static void changeTeamHandler(PlayerChangedTeamEvent event) {
        if (!ResearchdCompatHandler.isFTBTeamsEnabled()) return;
        Researchd.debug("FTBTeamsCompat", "changeTeamHandler called");
        ServerPlayer player = event.getPlayer();
        if (player == null) {
            Researchd.LOGGER.error("PlayerChangedTeamEvent posted with null ServerPlayer argument. Data Errors may occur.");
            return;
        }

        ResearchTeamHelperServer.handleLeaveTeam(player);
        ResearchTeamHelperServer.handleSetName(player, event.getTeam().getName().getString());

        UUID newTeamOwner = event.getTeam().getOwner();
        if (!newTeamOwner.equals(player.getUUID())) /* If it's an actually different team, in rest it's just a leave team*/ {
            ResearchTeamHelperServer.handleEnterTeamSynced(player, (ResearchTeamImpl) ResearchdApi.getTeamManager(player.level()).getTeamByPlayerId(newTeamOwner));
        }
    }

    public static void changeTeamNameHandler(TeamPropertiesChangedEvent event) {
        if (!ResearchdCompatHandler.isFTBTeamsEnabled()) return;
        Researchd.debug("FTBTeamsCompat", "changeTeamNameHandler called");
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        if (!event.getPreviousProperties().get(TeamProperties.DISPLAY_NAME).equals(event.getTeam().getName().getString())) {
            ResearchTeamMap teams = TeamSavedData.getData(server.overworld());
            ResearchTeam team = teams.getTeamByPlayerId(event.getTeam().getOwner());
            if (team == null) return;

            team.setName(event.getTeam().getName().getString());
            teams.setChanged();
        }
    }
//
//    public static void joinedTeamHandler(PlayerJoinedPartyTeamEvent event) {
//        System.out.println("FTBTeamsCompat: joinedTeamHandler called");
//        ServerPlayer player = event.getPlayer();
//        if (player == null) {
//            Researchd.LOGGER.error("PlayerJoinedPartyTeamEvent posted with null ServerPlayer argument. Data Errors may occur.");
//            return;
//        }
//
//        UUID newTeamOwner = event.getTeam().getOwner();
//        ResearchTeamHelper.handleLeaveTeam(player);
//        ResearchTeamHelper.handleEnterTeam(player, newTeamOwner);
//    }

    static {
        TeamEvent.PLAYER_CHANGED.register(FTBTeamsCompat::changeTeamHandler);
        //TeamEvent.PLAYER_JOINED_PARTY.register(FTBTeamsCompat::joinedTeamHandler);
        TeamEvent.PROPERTIES_CHANGED.register(FTBTeamsCompat::changeTeamNameHandler);
    }

    public static void init() {
    }
}
