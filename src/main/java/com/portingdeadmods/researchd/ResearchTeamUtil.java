package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ResearchTeamUtil {
	/**
	* CALL THIS METHOD ONLY IF YOU ARE SURE YOU'RE ON SERVER SIDE
	*
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

	public static ResearchTeam getResearchTeam(Player player) {
		UUID uuid = player.getUUID();
		return ResearchdSavedData.get(player.level()).getTeamForUUID(uuid);
	}
}
