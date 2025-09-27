package com.portingdeadmods.researchd.data.helper;

import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.researchd.api.data.team.TeamMember;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.networking.team.RefreshResearchesPayload;
import com.portingdeadmods.researchd.networking.team.TransferOwnershipPayload;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public final class ResearchTeamHelper {
    public static boolean isResearchTeamLeader(Player player) {
        UUID uuid = player.getUUID();
        ResearchTeam team = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(uuid);

        return team.isOwner(uuid);
    }

    public static int getPermissionLevel(Player player) {
        UUID uuid = player.getUUID();
        ResearchTeam team = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(uuid);

        return team.getPermissionLevel(uuid);
    }

    public static int getPermissionLevel(Level level, UUID uuid) {
        ResearchTeam team = ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getTeamByMember(uuid);

        return team.getPermissionLevel(uuid);
    }

    public @Nullable static ResearchTeam getResearchTeam(Player player) {
        UUID uuid = player.getUUID();
        return ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(uuid);
    }

    public @Nullable static ResearchTeam getResearchTeamByUUID(Level level, UUID uuid) {
        ResearchTeamMap map = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        if (map == null) return null;
        return map.getTeamByMember(uuid);
    }

    public static boolean arePlayersSameTeam(Player player1, Player player2) {
        UUID uuid1 = player1.getUUID();
        UUID uuid2 = player2.getUUID();
        ResearchTeam team1 = ResearchdSavedData.TEAM_RESEARCH.get().getData(player1.level()).getTeamByMember(uuid1);
        ResearchTeam team2 = ResearchdSavedData.TEAM_RESEARCH.get().getData(player2.level()).getTeamByMember(uuid2);

        return team1.equals(team2);
    }

    public static boolean arePlayersSameTeam(Player player1, UUID uuid2) {
        UUID uuid1 = player1.getUUID();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(player1.level());

        ResearchTeam team1 = savedData.getTeamByMember(uuid1);
        ResearchTeam team2 = savedData.getTeamByMember(uuid2);

        return team1.equals(team2);
    }

    public static boolean arePlayersSameTeam(Level level, UUID uuid1, UUID uuid2) {
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team1 = savedData.getTeamByMember(uuid1);
        ResearchTeam team2 = savedData.getTeamByMember(uuid2);

        return team1.equals(team2);
    }

    /**
     * Removes the player from his research team.
     * <br>
     * Sets savedData dirty
     *
     * @param player
     */
    public static void removeModFromTeam(ServerPlayer player) {
        UUID uuid = player.getUUID();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level());
        ResearchTeam team = savedData.getTeamByMember(uuid);

        if (team != null) {
            team.removeModerator(uuid);
            team.removeMember(uuid);
        }

        ResearchdSavedData.TEAM_RESEARCH.get().setData(player.level(), savedData);
    }

    public static void handleEnterTeam(ServerPlayer requester, UUID memberOfTeam) {
        Level level = requester.level();
        UUID requesterId = requester.getUUID();

        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        ResearchTeam team = savedData.getTeamByMember(memberOfTeam);

        if (getResearchTeam(requester).getMembers().size() > 1) {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.ALREADY_IN_TEAM));
            return;
        }
        if (team != null && (team.getSentInvites().contains(requesterId))) {
			ResearchTeamHelper.handleLeaveTeam(requester, PlayerUtils.EMPTY_UUID);

			savedData.getResearchTeams().put(requesterId, team);
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.YOU_JOINED_TEAM, team.getName()));

			for (TeamMember member : team.getMembers()) {
				Player memberPlayer = level.getPlayerByUUID(member.player());
				if (memberPlayer != null) {
					memberPlayer.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.PLAYER_JOINED_TEAM, PlayerUtils.getPlayerNameFromUUID(level, requesterId)));
				}
			}

            team.addMember(requesterId);
            team.removeSentInvite(requesterId);
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
	        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);

            PacketDistributor.sendToPlayer(requester, new RefreshResearchesPayload());
        }
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param memberOfTeam
     */
    public static void handleEnterTeam(ServerPlayer requester, Player memberOfTeam) {
        handleEnterTeam(requester, memberOfTeam.getUUID());
    }

	public static void handleIgnoreTeam(ServerPlayer requester, UUID memberOfTeam) {
		Level level = requester.level();
		UUID requesterId = requester.getUUID();

		ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
		ResearchTeam team = savedData.getTeamByMember(memberOfTeam);

		if (team != null) {
			team.addIgnore(requester.getUUID());
			requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.IGNORE, team.getName()));
		}
	}

	/**
	 * 2 player param wrapper
	 *
	 * @param requester
	 * @param memberOfTeam
	 */
	public static void handleIgnoreTeam(ServerPlayer requester, Player memberOfTeam) {
		handleIgnoreTeam(requester, memberOfTeam.getUUID());
	}

    public static void handleLeaveTeam(ServerPlayer requester, @Nullable UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();

        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        // Handle the case of transfering ownership
        if (isResearchTeamLeader(requester)) {
            if (getResearchTeam(requester).getMembers().size() <= 1) {
                savedData.getResearchTeams().remove(requesterId);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.LEFT_TEAM));

                savedData.getResearchTeams().put(requesterId, ResearchTeam.createDefaultTeam(requester));
            } else {
                if (nextToLead == PlayerUtils.EMPTY_UUID || nextToLead == null) {
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_NEXT_LEADER));
                    return;
                }
                handleTransferOwnership(requester, nextToLead);
            }

            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
	        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            PacketDistributor.sendToPlayer(requester, new RefreshResearchesPayload());
            return;
        }

        if (getPermissionLevel(requester) == 1) {
            removeModFromTeam(requester);
        }
	    ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param nextToLead
     */
    public static void handleLeaveTeam(ServerPlayer requester, Player nextToLead) {
        handleLeaveTeam(requester, nextToLead.getUUID());
    }

    public static void handleManageMember(ServerPlayer requester, UUID member, boolean remove) {
        UUID requesterId = requester.getUUID();
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (requester.getUUID() == member) {
            requester.sendSystemMessage(getIllegalMessage());
            return;
        }

        if (getPermissionLevel(requester) >= 1) {
            if (remove) {
                getResearchTeam(requester).removeMember(member);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.REMOVED, PlayerUtils.getPlayerNameFromUUID(level, member)));
            } else {
                getResearchTeam(requester).addSentInvite(member);
            }

	        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
	        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
        } else {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
	    ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param member
     * @param remove
     */
    public static void handleManageMember(ServerPlayer requester, Player member, boolean remove) {
        handleManageMember(requester, member.getUUID(), remove);
    }

    public static void handleManageModerator(ServerPlayer requester, UUID moderator, boolean remove) {
        UUID requesterId = requester.getUUID();
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (requester.getUUID() == moderator) {
            requester.sendSystemMessage(getIllegalMessage());
            return;
        }

        if (getPermissionLevel(requester) == 2) {
            if (arePlayersSameTeam(requester, moderator)) {
                if (remove) {
                    getResearchTeam(requester).removeModerator(moderator);
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.DEMOTED, PlayerUtils.getPlayerNameFromUUID(level, moderator)));
                } else {
                    getResearchTeam(requester).addModerator(moderator);
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.PROMOTED, PlayerUtils.getPlayerNameFromUUID(level, moderator)));
                }

	            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
	            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            } else {
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.BAD_INPUT));
            }
        } else {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param moderator
     * @param remove
     */
    public static void handleManageModerator(ServerPlayer requester, Player moderator, boolean remove) {
        handleManageModerator(requester, moderator.getUUID(), remove);
    }

    public static void handleSetName(ServerPlayer requester, String name) {
        UUID requesterId = requester.getUUID();
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (getPermissionLevel(requester) == 2) {
            String oldname = getResearchTeam(requester).getName();
            getResearchTeam(requester).setName(name);
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NEW_TEAM_NAME, oldname, name));
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
	        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
        } else {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    public static void handleTransferOwnership(ServerPlayer requester, UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(requester.level());

        if (getPermissionLevel(requester) == 2) {
            if (arePlayersSameTeam(level, requesterId, nextToLead)) {
                // Set the new leader
                getResearchTeam(requester).setOwner(nextToLead);

                // If he's moderator remove him from the mod list
                if (getPermissionLevel(level, nextToLead) == 1) {
                    getResearchTeam(requester).removeModerator(nextToLead);
                }

                // Set the old leader as moderator
                getResearchTeam(requester).addModerator(requesterId);
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
	            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.TRANSFERRED_OWNERSHIP, PlayerUtils.getPlayerNameFromUUID(level, nextToLead)));
            } else {
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.BAD_INPUT));
            }
        }
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param nextToLead
     */
    public static void handleTransferOwnership(ServerPlayer requester, Player nextToLead) {
        handleTransferOwnership(requester, nextToLead.getUUID());
    }

    public static void handleListMembers(ServerPlayer requester) {
        ResearchTeam team = getResearchTeam(requester);
        if (team != null) {
            requester.sendSystemMessage(team.parseMembers(requester.level()));
        }
    }

    public static void handleSendInviteToPlayer(ServerPlayer requester, UUID invited, boolean remove) {
        ResearchTeam team = getResearchTeam(requester);
        if (team != null) {
            if (remove) {
                team.removeSentInvite(invited);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.REMOVED_INVITE, PlayerUtils.getPlayerNameFromUUID(requester.level(), invited)));
            } else {
                team.addSentInvite(invited);
				ServerPlayer invitedPlayer = (ServerPlayer) requester.serverLevel().getPlayerByUUID(invited);
				if (invitedPlayer != null) {
					invitedPlayer.sendSystemMessage(
							ResearchdTranslations.component(ResearchdTranslations.Team.RECEIVED_INVITE, team.getName())
									.append(Component.literal("\n"))
									.append("     ")
									.append(ResearchdTranslations.component(ResearchdTranslations.Team.ACCEPT).withStyle(style -> style.withClickEvent(
											new net.minecraft.network.chat.ClickEvent(ClickEvent.Action.RUN_COMMAND, "/researchd team join " + PlayerUtils.getPlayerNameFromUUID(requester.level(), requester.getUUID()))
									)))
									.append("     ")
									.append(ResearchdTranslations.component(ResearchdTranslations.Team.DECLINE).withStyle(style -> style.withClickEvent(
											new net.minecraft.network.chat.ClickEvent(ClickEvent.Action.RUN_COMMAND, "/researchd team ignore " + PlayerUtils.getPlayerNameFromUUID(requester.level(), requester.getUUID()))
									)))
					);
				}
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.SENT_INVITE, PlayerUtils.getPlayerNameFromUUID(requester.level(), invited), team.getName()));
            }
            Level level = requester.level();

            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
	        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
        }
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param invited
     * @param remove
     */
    public static void handleSendInviteToPlayer(ServerPlayer requester, Player invited, boolean remove) {
        handleSendInviteToPlayer(requester, invited.getUUID(), remove);
    }

    public static void handleRequestToJoin(ServerPlayer requester, UUID teamMember, boolean remove) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        Player teamMemberPlayer = level.getPlayerByUUID(teamMember);
        if (teamMemberPlayer != null) {
            ResearchTeam team = getResearchTeam(teamMemberPlayer);
            if (team != null) {
                if (remove) {
                    team.removeReceivedInvite(requester.getUUID());
                } else {
                    team.addSentInvite(requester.getUUID());
                }
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
	            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            } else {
                requester.sendSystemMessage(Component.literal("The player you're trying to join is not in a team!").withStyle(ChatFormatting.RED));
            }
        } else {
            requester.sendSystemMessage(Component.literal("The player you're trying to join does not exist!").withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 2 player param wrapper
     *
     * @param requester
     * @param teamMember
     * @param remove
     */
    public static void handleRequestToJoin(ServerPlayer requester, Player teamMember, boolean remove) {
        handleRequestToJoin(requester, teamMember.getUUID(), remove);
    }

    public static ArrayList<String> getTeamMemberNames(Level level, Player player) {
        return new ArrayList<>(getResearchTeam(player).getMembers().stream().map(
                member -> level.getPlayerByUUID(member.player()).getName().getString()
        ).toList());
    }

    public static ArrayList<ResearchTeam> getTeams(Level level) {
        return new ArrayList<ResearchTeam>(ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getResearchTeams().values());
    }

    public static ArrayList<String> getTeamNames(Level level) {
        return new ArrayList<>(ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getResearchTeams().values().stream()
                .map(ResearchTeam::getName)
                .toList());
    }

    public static MutableComponent getFormattedDump(Level level) {
        ArrayList<MutableComponent> dump = new ArrayList<MutableComponent>();
        dump.add(Component.literal("---- RESEARCH'D DUMP - TEAMS ----").withStyle(ChatFormatting.AQUA));
        for (Map.Entry<UUID, ResearchTeam> entry : ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getResearchTeams().entrySet()) {
            dump.add(
                    Component.literal(PlayerUtils.getPlayerNameFromUUID(level, entry.getKey())).withStyle(ChatFormatting.WHITE)
                            .append(Component.literal(" -> ").withStyle(ChatFormatting.AQUA))
                            .append(Component.literal(entry.getValue().getName()).withStyle(ChatFormatting.WHITE))
            );
        }

        Map<UUID, ResearchTeam> map = ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getResearchTeams();
        for (ResearchTeam team : map.values()) {
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

    public static void initializeTeamResearches(LevelAccessor level) {
        ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData((Level) level);
        if (data == null) return;

        for (ResearchTeam team : data.getResearchTeams().values()) {
            Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearchProgress().researches();
            Map<ResourceKey<Research>, ResearchMethodProgress<?>> progress = team.getResearchProgress().progress();
            Set<GlobalResearch> globalResearches = new HashSet<>(CommonResearchCache.GLOBAL_RESEARCHES.values());

            for (GlobalResearch research : globalResearches) {
                if (progress.containsKey(research.getResearchKey())) continue;

                progress.put(research.getResearchKey(), ResearchMethodProgress.fromResearch(level.registryAccess(), research.getResearchKey()));
            }

            researches.values().stream().map(ResearchInstance::getResearch).toList().forEach(globalResearches::remove);
            for (GlobalResearch globalResearch : globalResearches) {
                researches.put(globalResearch.getResearchKey(), new ResearchInstance(globalResearch, ResearchStatus.LOCKED));
            }
        }
        ResearchdSavedData.TEAM_RESEARCH.get().setData((ServerLevel) level, data);
	    ResearchdSavedData.TEAM_RESEARCH.get().sync((ServerLevel) level);
    }

    public static void resolveGlobalResearches(ResearchTeamMap researchTeamMap) {
        for (ResearchTeam team : researchTeamMap.getResearchTeams().values()) {
            TeamResearchProgress researchProgress = team.getMetadata().getResearchProgress();
            for (Map.Entry<ResourceKey<Research>, ResearchInstance> entry : researchProgress.researches().entrySet()) {
                entry.setValue(new ResearchInstance(CommonResearchCache.GLOBAL_RESEARCHES.get(entry.getKey()), entry.getValue().getResearchStatus(), entry.getValue().getResearchedPlayer(), entry.getValue().getResearchedTime()));
            }
        }
    }
}
