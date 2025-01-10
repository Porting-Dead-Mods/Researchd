package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class ResearchTeamUtil {
	// CALL THESE METHOD ONLY IF YOU ARE SURE YOU'RE ON SERVER SIDE

	/**
    * Checks if the Player is the leader of his research team.
	* @param player
	 */
	public static boolean isResearchTeamLeader(Player player) {
		UUID uuid = player.getUUID();
		ResearchTeam team = ResearchdSavedData.get(player.level()).getTeamForUUID(uuid);

		return team.isLeader(uuid);
	}

	public static int getPermissionLevel(Player player) {
		UUID uuid = player.getUUID();
		ResearchTeam team = ResearchdSavedData.get(player.level()).getTeamForUUID(uuid);

		return team.getPermissionLevel(uuid);
	}

	public static int getPermissionLevel(Level level, UUID uuid) {
		ResearchTeam team = ResearchdSavedData.get(level).getTeamForUUID(uuid);

		return team.getPermissionLevel(uuid);
	}


	public static ResearchTeam getResearchTeam(Player player) {
		UUID uuid = player.getUUID();
		return ResearchdSavedData.get(player.level()).getTeamForUUID(uuid);
	}

	public static boolean isInATeam(Player player) {
		UUID uuid = player.getUUID();
		return ResearchdSavedData.get(player.level()).getTeamForUUID(uuid) != null;
	}

	public static boolean arePlayersSameTeam(Player player1, Player player2) {
		UUID uuid1 = player1.getUUID();
		UUID uuid2 = player2.getUUID();
		ResearchTeam team1 = ResearchdSavedData.get(player1.level()).getTeamForUUID(uuid1);
		ResearchTeam team2 = ResearchdSavedData.get(player2.level()).getTeamForUUID(uuid2);

		return team1.equals(team2);
	}

	public static boolean arePlayersSameTeam(Player player1, UUID uuid2) {
		UUID uuid1 = player1.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(player1.level());

		ResearchTeam team1 = savedData.getTeamForUUID(uuid1);
		ResearchTeam team2 = savedData.getTeamForUUID(uuid2);

		return team1.equals(team2);
	}

	public static boolean arePlayersSameTeam(Level level, UUID uuid1, UUID uuid2) {
		ResearchdSavedData savedData = ResearchdSavedData.get(level);

		ResearchTeam team1 = savedData.getTeamForUUID(uuid1);
		ResearchTeam team2 = savedData.getTeamForUUID(uuid2);

		return team1.equals(team2);
	}

	/**
	 * Removes the player from his research team.
	 * <br>
	 * Sets savedData dirty
	 * @param player
	 */
	public static void removeModFromTeam(Player player) {
		UUID uuid = player.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(player.level());
		ResearchTeam team = savedData.getTeamForUUID(uuid);

		if (team != null) {
			team.removeModerator(uuid);
			team.removeMember(uuid);
		}

		savedData.setDirty();
	}
}
