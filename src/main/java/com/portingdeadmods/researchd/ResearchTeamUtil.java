package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.data.TeamSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.networking.TransferOwnershipPayload;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResearchTeamUtil {
	// CALL THESE METHOD ONLY IF YOU ARE SURE YOU'RE ON SERVER SIDE

	/**
    * Checks if the Player is the leader of his research team.
	* @param player
	 */
	public static boolean isResearchTeamLeader(Player player) {
		UUID uuid = player.getUUID();
		ResearchTeam team = TeamSavedData.get(player.level()).getTeamForUUID(uuid);

		return team.isLeader(uuid);
	}

	public static int getPermissionLevel(Player player) {
		UUID uuid = player.getUUID();
		ResearchTeam team = TeamSavedData.get(player.level()).getTeamForUUID(uuid);

		return team.getPermissionLevel(uuid);
	}

	public static int getPermissionLevel(Level level, UUID uuid) {
		ResearchTeam team = TeamSavedData.get(level).getTeamForUUID(uuid);

		return team.getPermissionLevel(uuid);
	}


	public static ResearchTeam getResearchTeam(Player player) {
		UUID uuid = player.getUUID();
		return TeamSavedData.get(player.level()).getTeamForUUID(uuid);
	}

	public static boolean isInATeam(Player player) {
		UUID uuid = player.getUUID();
		return TeamSavedData.get(player.level()).getTeamForUUID(uuid) != null;
	}

	public static boolean arePlayersSameTeam(Player player1, Player player2) {
		UUID uuid1 = player1.getUUID();
		UUID uuid2 = player2.getUUID();
		ResearchTeam team1 = TeamSavedData.get(player1.level()).getTeamForUUID(uuid1);
		ResearchTeam team2 = TeamSavedData.get(player2.level()).getTeamForUUID(uuid2);

		return team1.equals(team2);
	}

	public static boolean arePlayersSameTeam(Player player1, UUID uuid2) {
		UUID uuid1 = player1.getUUID();
		TeamSavedData savedData = TeamSavedData.get(player1.level());

		ResearchTeam team1 = savedData.getTeamForUUID(uuid1);
		ResearchTeam team2 = savedData.getTeamForUUID(uuid2);

		return team1.equals(team2);
	}

	public static boolean arePlayersSameTeam(Level level, UUID uuid1, UUID uuid2) {
		TeamSavedData savedData = TeamSavedData.get(level);

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
		TeamSavedData savedData = TeamSavedData.get(player.level());
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

		TeamSavedData savedData = TeamSavedData.get(level);
		ResearchTeam team = savedData.getTeamForUUID(memberOfTeam);

		if (isInATeam(requester)) {
			requester.sendSystemMessage(Component.literal("You're already in a team!").withStyle(ChatFormatting.RED));
			return;
		}
		if (team != null && (team.getReceivedInvites().contains(requesterId) || team.isFreeToJoin())) {
			requester.sendSystemMessage(Component.literal("You successfully joined: " + team.getName() + "!").withStyle(ChatFormatting.GREEN));
			team.addMember(requesterId);
			team.removeSentInvite(requesterId);
			savedData.setDirty();
		}
	}

	public static void handleLeaveTeam(Player requester, UUID nextToLead) {
		Level level = requester.level();
		UUID requesterId = requester.getUUID();

		TeamSavedData savedData = TeamSavedData.get(level);

		if (!ResearchTeamUtil.isInATeam(requester)) {
			requester.sendSystemMessage(Component.literal("You're not in a team!").withStyle(ChatFormatting.RED));
			return;
		}
		// Handle the case of transfering ownership
		if (ResearchTeamUtil.isResearchTeamLeader(requester)) {
			if (ResearchTeamUtil.getResearchTeam(requester).getMembers().size() <= 1) {
				savedData.getTeams().remove(requesterId);
				savedData.setDirty();
				requester.sendSystemMessage(Component.literal("You successfully abandoned your team!").withStyle(ChatFormatting.GREEN));
				return;
			} else {
				if (nextToLead == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
					requester.sendSystemMessage(Component.literal("You need to specify the next leader!").withStyle(ChatFormatting.RED));
					return;
				}
				PacketDistributor.sendToServer(new TransferOwnershipPayload(nextToLead));
				savedData.setDirty();
				return;
			}
		}

		if (ResearchTeamUtil.getPermissionLevel(requester) == 1) {
			ResearchTeamUtil.removeModFromTeam(requester);
		}
	}

	public static void handleManageMember(Player requester, UUID member, boolean remove) {
		UUID requesterId = requester.getUUID();
		TeamSavedData savedData = TeamSavedData.get(requester.level());

		if (requester.getUUID() == member) {
			requester.sendSystemMessage(getIllegalMessage());
			return;
		}

		if (ResearchTeamUtil.getPermissionLevel(requester) >= 1) {
			if (remove) {
				ResearchTeamUtil.getResearchTeam(requester).removeMember(member);
				requester.sendSystemMessage(Component.literal("Member " + PlayerUtils.getPlayerNameFromUUID(requester.level(), member) + " removed!").withStyle(ChatFormatting.GREEN));
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
		TeamSavedData savedData = TeamSavedData.get(requester.level());

		if (requester.getUUID() == moderator) {
			requester.sendSystemMessage(getIllegalMessage());
			return;
		}

		if (ResearchTeamUtil.getPermissionLevel(requester) == 2) {
			if (arePlayersSameTeam(requester, moderator)) {
				if (remove) {
					ResearchTeamUtil.getResearchTeam(requester).removeModerator(moderator);
					requester.sendSystemMessage(Component.literal("Moderator " + PlayerUtils.getPlayerNameFromUUID(requester.level(), moderator) + " removed!").withStyle(ChatFormatting.GREEN));
					savedData.setDirty();
				} else {
					ResearchTeamUtil.getResearchTeam(requester).addModerator(moderator);
					requester.sendSystemMessage(Component.literal("Moderator " + PlayerUtils.getPlayerNameFromUUID(requester.level(), moderator) + " added!").withStyle(ChatFormatting.GREEN));
					savedData.setDirty();
				}
			} else {
				requester.sendSystemMessage(Component.literal("Player is not in the same team with you!").withStyle(ChatFormatting.RED));
			}
		} else {
			requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
		}
	}

	public static void handleSetName(Player requester, String name) {
		UUID requesterId = requester.getUUID();
		TeamSavedData savedData = TeamSavedData.get(requester.level());

		if (ResearchTeamUtil.getPermissionLevel(requester) == 2) {
			String oldname = ResearchTeamUtil.getResearchTeam(requester).getName();
			ResearchTeamUtil.getResearchTeam(requester).setName(name);
			requester.sendSystemMessage(Component.literal("Team name changed from " + oldname + " to " + name).withStyle(ChatFormatting.GREEN));
			savedData.setDirty();
		} else {
			requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
		}
	}

	public static void handleTransferOwnership(Player requester, UUID nextToLead) {
		Level level = requester.level();
		UUID requesterId = requester.getUUID();
		TeamSavedData savedData = TeamSavedData.get(requester.level());

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
		TeamSavedData savedData = TeamSavedData.get(requester.level());

		if (!ResearchTeamUtil.isInATeam(requester)) {
			ResearchTeam team = new ResearchTeam(requesterId, name);
			requester.sendSystemMessage(Component.literal("Team " + name + " created!").withStyle(ChatFormatting.GREEN));
			savedData.getTeams().put(requesterId, team);
			savedData.setDirty();
		} else {
			requester.sendSystemMessage(Component.literal("You're already in a team!").withStyle(ChatFormatting.RED));
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
				requester.sendSystemMessage(Component.literal("Invite to " + PlayerUtils.getPlayerNameFromUUID(requester.level(), invited) + " removed").withStyle(ChatFormatting.GREEN));
            } else {
                team.addSentInvite(invited);
				requester.sendSystemMessage(Component.literal("Invite sent to " + PlayerUtils.getPlayerNameFromUUID(requester.level(), invited)).withStyle(ChatFormatting.GREEN));
            }
            TeamSavedData.get(requester.level()).setDirty();
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
				TeamSavedData.get(requester.level()).setDirty();
			} else {
				requester.sendSystemMessage(Component.literal("The player you're trying to join is not in a team!").withStyle(ChatFormatting.RED));
			}
		} else {
			requester.sendSystemMessage(Component.literal("The player you're trying to join does not exist!").withStyle(ChatFormatting.RED));
		}
	}

	public static ArrayList<String> getTeamMemberNames(Level level, Player player) {
		return new ArrayList<String>(ResearchTeamUtil.getResearchTeam(player).getMembers().stream().map(
				member -> level.getPlayerByUUID(member).getName().getString()
		).toList());
	}

	public static ArrayList<ResearchTeam> getTeams(Level level) {
		return new ArrayList<ResearchTeam>(TeamSavedData.get(level).getTeams().values());
	}

	public static ArrayList<String> getTeamNames(Level level) {
		return new ArrayList<String>(TeamSavedData.get(level).getTeams().values().stream().map(
				team -> team.getName()
		).toList());
	}

	public static MutableComponent getFormattedDump(Level level) {
		ArrayList<MutableComponent> dump = new ArrayList<MutableComponent>();
		dump.add(Component.literal("---- RESEARCH'D DUMP - TEAMS ----").withStyle(ChatFormatting.AQUA));
		for (Map.Entry<UUID, ResearchTeam> entry : TeamSavedData.get(level).getTeams().entrySet()) {
			dump.add(
				Component.literal(PlayerUtils.getPlayerNameFromUUID(level, entry.getKey())).withStyle(ChatFormatting.WHITE)
				.append(Component.literal(" -> ").withStyle(ChatFormatting.AQUA))
				.append(Component.literal(entry.getValue().getName()).withStyle(ChatFormatting.WHITE))
			);
		}
		for (ResearchTeam team : TeamSavedData.get(level).getTeams().values()) {
			dump.add(team.parseMembers(level));
		}

		MutableComponent ret = Component.empty();
		for (MutableComponent component : dump) {
			ret.append(component);
			ret.append("\n");
		}

		ret.append(Component.literal("--------- END OF DUMP ----------").withStyle(ChatFormatting.AQUA));
		return ret;
	}

	public static MutableComponent paramDescription(String param, String description) {
		MutableComponent paramComp = Component.literal("<" + param + ">").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC);
		return paramComp.append(Component.literal(" - " + description).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}

	public static MutableComponent description(String description) {
		return Component.literal(" - " + description).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
	}

	public static MutableComponent helpMessage(String categ, String command, Component... description) {
		MutableComponent ret = Component.literal("/researchd " + categ + " ").withStyle(ChatFormatting.AQUA);
		ret.append(Component.literal(command).withStyle(ChatFormatting.WHITE));

		for (Component desc : description) {
			ret.append("\n");
			ret.append(desc);
		}

		return ret;
	}

	public static void handleHelpMessage(Player player, String page) {
		if (page.equals("team")) {
			player.sendSystemMessage(Component.literal("> Researchd Teams").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
			player.sendSystemMessage(helpMessage("team", "create <name>", description("Create a new team with the specified name.")));
			player.sendSystemMessage(helpMessage("team", "list", description("List all teams.")));
			player.sendSystemMessage(helpMessage("team", "members", description("List all members of your team.")));
			player.sendSystemMessage(helpMessage("team", "invite <player>", description("Invite a player to your team.")));
			player.sendSystemMessage(helpMessage("team", "join <player>", description("Join a team that you have been invited to.")));
			player.sendSystemMessage(helpMessage("team", "leave <next_to_lead>", paramDescription("next_to_lead", "Put 'none' if there's no-one to lead or you're not the leader."), description("Leave your current team.")));
			player.sendSystemMessage(helpMessage("team", "promote <player>", description("Promote a player to moderator. You got to be the leader to do this.")));
			player.sendSystemMessage(helpMessage("team", "demote <player>", description("Demote a player from moderator. You got to be the leader to do this.")));
			player.sendSystemMessage(helpMessage("team", "kick <player>", description("Kick a player from your team. You got to be a moderator or the leader to do this.")));
			player.sendSystemMessage(helpMessage("team", "transfer-ownership <player>", description("Transfer ownership of the team to another player.")));
			player.sendSystemMessage(helpMessage("team", "set-name <name>", description("Set a new name for your team.")));
		}
	}

	public static MutableComponent illegalMessage(String message) {
		return Component.literal(message).withStyle(ChatFormatting.RED);
	}
	public static MutableComponent getIllegalMessage() {
		List<MutableComponent> msgs = new ArrayList<>();

		msgs.add(illegalMessage("No."));
		msgs.add(illegalMessage("Nope."));
		msgs.add(illegalMessage("Not happening."));
		msgs.add(illegalMessage("Nuh uh."));
		msgs.add(illegalMessage("No chance."));
		msgs.add(illegalMessage("Stop."));
		msgs.add(illegalMessage("You gonna keep doin that?"));
		msgs.add(illegalMessage("You're just spamming the console at this point."));
		msgs.add(illegalMessage("You're not getting anywhere with this."));

		return msgs.get((int) Math.floor(Math.random() * msgs.size()));
	}
}
