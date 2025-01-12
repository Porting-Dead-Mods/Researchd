package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.networking.TransferOwnershipPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

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

	public static void handleEnterTeam(Player requester, UUID memberOfTeam) {
		Level level = requester.level();
		UUID requesterId = requester.getUUID();

		ResearchdSavedData savedData = ResearchdSavedData.get(level);
		ResearchTeam team = savedData.getTeamForUUID(memberOfTeam);

		if (team != null && team.getReceivedInvites().contains(requesterId)) {
			team.addMember(requesterId);
			team.removeSentInvite(requesterId);
			savedData.setDirty();
		}
	}

	public static void handleLeaveTeam(Player requester, UUID nextToLead) {
		Level level = requester.level();
		UUID requesterId = requester.getUUID();

		ResearchdSavedData savedData = ResearchdSavedData.get(level);

		if (!ResearchTeamUtil.isInATeam(requester)) {
			requester.sendSystemMessage(Component.literal("You're not in a team!").withStyle(ChatFormatting.RED));
			return;
		}
		// Handle the case of transfering ownership
		if (ResearchTeamUtil.isResearchTeamLeader(requester)) {
			PacketDistributor.sendToServer(new TransferOwnershipPayload(nextToLead));
			savedData.setDirty();
			return;
		}

		if (ResearchTeamUtil.getPermissionLevel(requester) == 1) {
			ResearchTeamUtil.removeModFromTeam(requester);
		}
	}

	public static void handleManageMember(Player requester, UUID member, boolean remove) {
		UUID requesterId = requester.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(requester.level());

		if (ResearchTeamUtil.getPermissionLevel(requester) >= 1) {
			if (remove == true) {
				ResearchTeamUtil.getResearchTeam(requester).removeMember(member);
				savedData.setDirty();
			} else {
				ResearchTeamUtil.getResearchTeam(requester).addSentInvite(member);
				savedData.setDirty();
			}
		} else {
			requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
		}
	}

	public static void handleManageModerator(Player requester, UUID moderator, boolean remove) {
		UUID requesterId = requester.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(requester.level());

		if (ResearchTeamUtil.getPermissionLevel(requester) == 2) {
			if (remove) {
				ResearchTeamUtil.getResearchTeam(requester).removeModerator(moderator);
				savedData.setDirty();
			} else {
				ResearchTeamUtil.getResearchTeam(requester).addModerator(moderator);
				savedData.setDirty();
			}
		} else {
			requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
		}
	}

	public static void handleSetName(Player requester, String name) {
		UUID requesterId = requester.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(requester.level());

		if (ResearchTeamUtil.getPermissionLevel(requester) == 2) {
			String oldname = ResearchTeamUtil.getResearchTeam(requester).getName();
			ResearchTeamUtil.getResearchTeam(requester).setName(name);
			requester.sendSystemMessage(Component.literal("Team name changed from " + oldname + " to " + name).withStyle(ChatFormatting.GREEN));
			savedData.setDirty();
		}
	}

	public static void handleTransferOwnership(Player requester, UUID nextToLead) {
		Level level = requester.level();
		UUID requesterId = requester.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(requester.level());

		if (ResearchTeamUtil.getPermissionLevel(requester) == 2) {
			if (ResearchTeamUtil.arePlayersSameTeam(level, requesterId, nextToLead)) {
				// Set the new leader
				ResearchTeamUtil.getResearchTeam(requester).setLeader(nextToLead);

				// If he's moderator remove him from the mod list
				if (ResearchTeamUtil.getPermissionLevel(level, nextToLead) == 1) {
					ResearchTeamUtil.getResearchTeam(requester).removeModerator(nextToLead);
				}

				// Set the old leader as moderator
				ResearchTeamUtil.getResearchTeam(requester).addModerator(requesterId);
				savedData.setDirty();
				requester.sendSystemMessage(Component.literal("Ownership transfered to " + nextToLead).withStyle(ChatFormatting.GREEN));
			} else {
				requester.sendSystemMessage(Component.literal("You can't transfer ownership to someone who's not in your team!").withStyle(ChatFormatting.RED));
			}
		}
	}

	public static void handleCreateTeam(Player requester, String name) {
		UUID requesterId = requester.getUUID();
		ResearchdSavedData savedData = ResearchdSavedData.get(requester.level());

		if (!ResearchTeamUtil.isInATeam(requester)) {
			ResearchTeam team = new ResearchTeam(requesterId, name);
			requester.sendSystemMessage(Component.literal("Team " + name + " created!").withStyle(ChatFormatting.GREEN));
			savedData.getTeams().put(requesterId, team);
			savedData.setDirty();
		}
	}

	public static void handleListMembers(Player requester) {
		ResearchTeam team = ResearchTeamUtil.getResearchTeam(requester);
		if (team != null) {
			requester.sendSystemMessage(team.parseMembers(requester.level()));
		}
	}

	public static void handleSendInviteToPlayer(Player requester, UUID invited, boolean remove) {
		ResearchTeam team = ResearchTeamUtil.getResearchTeam(requester);
		if (team != null) {
			if (remove) {
                team.removeSentInvite(invited);
            } else {
                team.addSentInvite(invited);
            }
            ResearchdSavedData.get(requester.level()).setDirty();
		} else {
			requester.sendSystemMessage(Component.literal("You got to be in a team to do that! Create one with /researchd team create <name>"));
		}
	}

	public static void handleRequestToJoin(Player requester, UUID teamMember, boolean remove) {
		Level level = requester.level();
		Player teamMemberPlayer = level.getPlayerByUUID(teamMember);
		if (teamMemberPlayer != null) {
			ResearchTeam team = ResearchTeamUtil.getResearchTeam(teamMemberPlayer);
			if (team != null) {
				if (remove) {
					team.removeReceivedInvite(requester.getUUID());
				} else {
					team.addSentInvite(requester.getUUID());
				}
				ResearchdSavedData.get(requester.level()).setDirty();
			} else {
				requester.sendSystemMessage(Component.literal("The player you're trying to join is not in a team!").withStyle(ChatFormatting.RED));
			}
		} else {
			requester.sendSystemMessage(Component.literal("The player you're trying to join does not exist!").withStyle(ChatFormatting.RED));
		}
	}
}
