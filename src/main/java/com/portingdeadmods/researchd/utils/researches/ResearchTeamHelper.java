package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import com.portingdeadmods.researchd.networking.cache.ClearGraphCachePayload;
import com.portingdeadmods.researchd.networking.team.RefreshPlayerManagementPayload;
import com.portingdeadmods.researchd.networking.team.RefreshResearchesPayload;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class ResearchTeamHelper {
    /**
     * Get the team that a specified player is a member of
     *
     * @param player The player whose team you want to get
     * @return The team the player is a member of
     */
    public static @NotNull ResearchTeam getTeamByMember(Player player) {
        UUID uuid = player.getUUID();
        return getTeamByMember(player.level(), uuid);
    }

    /**
     * Get the team that a player with the specified uuid is a member of
     *
     * @param uuid The uuid of the player whose team you want to get
     * @return The team the player is a member of
     */
    public static @NotNull ResearchTeam getTeamByMember(Level level, UUID uuid) {
        ResearchTeamMap map = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        return map.getTeamByMemberOrThrow(uuid);
    }

    /**
     * Removes the player from their research team.
     * Updates the team map
     *
     * @param player the player to be removed from their team
     */
    public static void removeMember(ServerPlayer player) {
        UUID uuid = player.getUUID();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level());
        SimpleResearchTeam team = savedData.getTeamByMember(uuid);

        if (team != null) {
            team.removeMember(uuid);
        }

        ResearchdSavedData.TEAM_RESEARCH.get().setData(player.level(), savedData);
    }

    /**
     * Get the permission level of a team's member
     * corresponding to the permissions given
     * to them based on their role
     *
     * @param player The player whose permission level you want to get
     * @return integer value permission level ( {@link ResearchTeamRole#getPermissionLevel()} )
     */
    public static int getPermissionLevel(Player player) {
        SimpleResearchTeam team = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(player.getUUID());
		if (team == null) return -1;

        return team.getMember(player.getUUID()).role().getPermissionLevel();
    }

	public static int getPermissionLevel(UUID player, Level level) {
		SimpleResearchTeam team = ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getTeamByMember(player);
		if (team == null) return -1;

		return team.getMember(player).role().getPermissionLevel();
	}

    public static boolean arePlayersSameTeam(Player player1, Player player2) {
        UUID uuid1 = player1.getUUID();
        UUID uuid2 = player2.getUUID();

        return arePlayersSameTeam(player1.level(), uuid1, uuid2);
    }

    public static boolean arePlayersSameTeam(Player player1, UUID uuid2) {
        UUID uuid1 = player1.getUUID();

        return arePlayersSameTeam(player1.level(), uuid1, uuid2);
    }

    public static boolean arePlayersSameTeam(Level level, UUID uuid1, UUID uuid2) {
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team1 = savedData.getTeamByMemberOrThrow(uuid1);
        ResearchTeam team2 = savedData.getTeamByMemberOrThrow(uuid2);

        return team1.getId().equals(team2.getId());
    }

    public static void handleEnterTeam(ServerPlayer requester, UUID memberOfTeam) {
        Level level = requester.level();
        UUID requesterId = requester.getUUID();

        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        SimpleResearchTeam team = savedData.getTeamByMember(memberOfTeam);

		// Already in Team (with multiple people) -> Return with error msg
        if (getTeamByMember(requester).getMembersAmount() > 1) {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.ALREADY_IN_TEAM));
            return;
        }

	    // Alone in team -> Enter the new team
        if (team != null && (team.getSocialManager().containsSentInvite(requesterId))) {
            ResearchTeamHelper.handleLeaveTeam(requester);

            savedData.researchTeams().put(requesterId, team);
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.YOU_JOINED_TEAM, team.getName()));

            for (TeamMember member : team.getMembers()) {
                Player memberPlayer = level.getPlayerByUUID(member.player());
                if (memberPlayer != null) {
                    memberPlayer.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.PLAYER_JOINED_TEAM, PlayerUtils.getPlayerNameFromUUID(level, requesterId)));
                }
            }

            team.addMember(requesterId);
            team.getSocialManager().removeSentInvite(requesterId);
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);

            refreshPlayerManagement(team, level);
            PacketDistributor.sendToPlayer(requester, new RefreshResearchesPayload());
        }
    }


    public static void handleIgnoreTeam(ServerPlayer requester, UUID memberOfTeam) {
        Level level = requester.level();

        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        SimpleResearchTeam team = savedData.getTeamByMember(memberOfTeam);

        if (team != null) {
            team.getSocialManager().addIgnore(requester.getUUID());
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.IGNORE, team.getName()));
        }
    }

    public static void handleLeaveTeam(ServerPlayer requester, @Nullable UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();

        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team = getTeamByMember(requester);

		// Is Owner -> Handle cases of being alone / with multiple people
        if (team.isOwner(requesterId)) {

			// Alone In Team -> Leaving creates default team for player
            if (team.getMembersAmount() <= 1) {
                savedData.researchTeams().remove(requesterId);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.LEFT_TEAM));

                savedData.researchTeams().put(requesterId, SimpleResearchTeam.createDefaultTeam(requester));
            }

			// Not Alone In Team -> Transfer Ownership
			else {

				// Team Leader Not Specified
                if (nextToLead == null || nextToLead.equals(PlayerUtils.EmptyUUID)) {
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_NEXT_LEADER));
                    return;
                }

				// Team Leader Specified
                handleTransferOwnership(requester, nextToLead);
            }

            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            PacketDistributor.sendToPlayer(requester, new RefreshResearchesPayload());
        }

	    // Is not Owner -> Just remove member out of the team and create a default one.
		else {
            removeMember(requester);
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.LEFT_TEAM));
            savedData.researchTeams().put(requesterId, SimpleResearchTeam.createDefaultTeam(requester));
            PacketDistributor.sendToPlayer(requester, ClearGraphCachePayload.INSTANCE);
            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            refreshPlayerManagement(team, level);
        }

    }

	/**
	 *
	 * @param requester
	 */
	public static void handleLeaveTeam(ServerPlayer requester) {
		ResearchTeamHelper.handleLeaveTeam(requester, PlayerUtils.EmptyUUID);
	}

    public static void handleManageMember(ServerPlayer requester, UUID member, boolean remove) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

		// Error safety (handling yourself)
        if (requester.getUUID().equals(member)) {
            requester.sendSystemMessage(getIllegalMessage());
            return;
        }

		// Error safety
	    if (!arePlayersSameTeam(requester, member)) return;

		// Permission Check
        if (getPermissionLevel(requester) >= 1 && (getPermissionLevel(requester) > getPermissionLevel(member, requester.level()))) {

			// Remove member and put them into a default team with a status message
            if (remove) {
                getTeamByMember(requester).removeMember(member);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.REMOVED, PlayerUtils.getPlayerNameFromUUID(level, member)));

				ServerPlayer kickedPlayer = server.getPlayerList().getPlayer(member);
                if (kickedPlayer != null) {
                    PacketDistributor.sendToPlayer(kickedPlayer, ClearGraphCachePayload.INSTANCE);
					kickedPlayer.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.KICKED, getTeamByMember(requester).getName()));
                }

				savedData.setDefaultTeam(member, requester.level());
            }

			// Invite Member
			else {
                getTeamByMember(requester).getSocialManager().addSentInvite(member);
            }

            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            refreshPlayerManagement(getTeamByMember(requester), level);
        } else {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
    }

    public static void handleManageModerator(ServerPlayer requester, UUID moderator, boolean remove) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

		// Error Safety (handling yourself)
        if (requester.getUUID().equals(moderator)) {
            requester.sendSystemMessage(getIllegalMessage());
            return;
        }

		// Permission Check (is Owner)
        if (getPermissionLevel(requester) == 2) {
            if (arePlayersSameTeam(requester, moderator)) {
                ResearchTeam team = getTeamByMember(requester);

                if (remove) {
                    team.setRole(moderator, ResearchTeamRole.MEMBER);
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.DEMOTED, PlayerUtils.getPlayerNameFromUUID(level, moderator)));
                } else {
                    team.setRole(moderator, ResearchTeamRole.MODERATOR);
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.PROMOTED, PlayerUtils.getPlayerNameFromUUID(level, moderator)));
                }

                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
                ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
                refreshPlayerManagement(getTeamByMember(requester), level);
            } else {
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.BAD_INPUT));
            }
        } else {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    public static void handleSetName(ServerPlayer requester, String name) {
        UUID requesterId = requester.getUUID();
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

	    // Permission Check (is Owner)
        if (getPermissionLevel(requester) == 2) {
			if (name.isEmpty()) {
				requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NAME_CANNOT_BE_EMPTY));
				return;
			}

            String oldname = getTeamByMember(requester).getName();
            getTeamByMember(requester).setName(name);
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NEW_TEAM_NAME, oldname, name));
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            refreshPlayerManagement(getTeamByMember(requester), level);
        } else {
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    public static void handleTransferOwnership(ServerPlayer requester, UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();
        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(requester.level());

	    // Permission Check (is Owner)
        if (getPermissionLevel(requester) == 2) {
            if (arePlayersSameTeam(level, requesterId, nextToLead)) {
                ResearchTeam team = getTeamByMember(requester);

                // Set the new leader
                team.setRole(nextToLead, ResearchTeamRole.OWNER);

                // Set the old leader as moderator
                team.setRole(requesterId, ResearchTeamRole.MODERATOR);

                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
                ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.TRANSFERRED_OWNERSHIP, PlayerUtils.getPlayerNameFromUUID(level, nextToLead)));
                refreshPlayerManagement(team, level);
            } else {
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.BAD_INPUT));
            }
        }
    }

    public static void handleListMembers(ServerPlayer requester) {
        ResearchTeam team = getTeamByMember(requester);
        requester.sendSystemMessage(formatMembers(team, requester.level()));
    }

    public static Component formatMembers(ResearchTeam team, Level level) {
        MutableComponent formattedTeam = Component.literal(team.getName()).withStyle(ChatFormatting.AQUA);
        formattedTeam.append(Component.literal(" has %d member%s: ".formatted(team.getMembersAmount(), team.getMembersAmount() == 1 ? "" : "s")).withStyle(ChatFormatting.WHITE));

        for (TeamMember member : team.getMembers()) {
            Player player = level.getPlayerByUUID(member.player());
            if (player != null)
                formattedTeam.append(Component.literal(player.getName().getString() + " ").withStyle(ChatFormatting.AQUA));
        }

        return formattedTeam;
    }

    public static void handleSendInviteToPlayer(ServerPlayer requester, UUID invited, boolean remove) {
        ResearchTeam team = getTeamByMember(requester);

		// Error Safety (inviting yourself)
	    if (requester.getUUID().equals(invited)) {
		    requester.sendSystemMessage(getIllegalMessage());
		    return;
	    }

        if (remove) {
            team.getSocialManager().removeSentInvite(invited);
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.REMOVED_INVITE, AllPlayersCache.getName(invited)));
        } else {
            team.getSocialManager().addSentInvite(invited);
            ServerPlayer invitedPlayer = (ServerPlayer) requester.serverLevel().getPlayerByUUID(invited);
            if (invitedPlayer != null) {

				// Accept / Decline prompt
                invitedPlayer.sendSystemMessage(
                        ResearchdTranslations.component(ResearchdTranslations.Team.RECEIVED_INVITE, team.getName())
                                .append(Component.literal("\n"))
                                .append("     ")
                                .append(ResearchdTranslations.component(ResearchdTranslations.Team.ACCEPT).withStyle(style -> style.withClickEvent(
                                        new net.minecraft.network.chat.ClickEvent(ClickEvent.Action.RUN_COMMAND, "/researchd team join " + AllPlayersCache.getName(requester.getUUID()))
                                )))
                                .append("     ")
                                .append(ResearchdTranslations.component(ResearchdTranslations.Team.DECLINE).withStyle(style -> style.withClickEvent(
                                        new net.minecraft.network.chat.ClickEvent(ClickEvent.Action.RUN_COMMAND, "/researchd team ignore " + AllPlayersCache.getName(requester.getUUID()))
                                )))
                );
            }
            requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.SENT_INVITE, AllPlayersCache.getName(invited), team.getName()));
        }
        Level level = requester.level();

        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
        ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
        refreshPlayerManagement(team, level);

    }

    public static Collection<? extends ResearchTeam> getTeams(Level level) {
        return ResearchdSavedData.TEAM_RESEARCH.get().getData(level).researchTeams().values();
    }

    public static Component getFormattedDump(Level level) {
        List<Component> dump = new ArrayList<>();
	    Set<SimpleResearchTeam> uniqueTeams = new HashSet<>(ResearchdSavedData.TEAM_RESEARCH.get().getData(level).researchTeams().values());

        dump.add(Component.literal("---- Researchd Teams ----").withStyle(ChatFormatting.GOLD));
        for (Iterator<SimpleResearchTeam> iterator = uniqueTeams.iterator(); iterator.hasNext(); ) {
            ResearchTeam team = iterator.next();
            dump.add(Component.literal(ChatFormatting.GREEN + team.getName() + ChatFormatting.RESET).append(" with %s member%s".formatted(ChatFormatting.GREEN.toString() + team.getMembersAmount() + ChatFormatting.RESET, team.getMembersAmount() == 1 ? "" : "s")));
            for (TeamMember member : team.getMembers()) {
                dump.add(Component.literal("┣ ").append(member.getName()).withStyle(ChatFormatting.GRAY));
            }
            if (iterator.hasNext()) {
                dump.add(Component.literal("┣━━").withStyle(ChatFormatting.GRAY));
            } else {
                dump.add(Component.literal("┗━━").withStyle(ChatFormatting.GRAY));
            }
        }

        MutableComponent ret = Component.empty();
        for (int i = 0; i < dump.size(); i++) {
            Component component = dump.get(i);
            ret.append(component);
            if (i < dump.size() - 1) {
                ret.append("\n");
            }
        }

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

    public static void cleanupTeamResearches(ResearchTeamMap teamMap, Level level) {
        for (SimpleResearchTeam team : teamMap.researchTeams().values()) {
            Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearches();
            Map<ResourceKey<Research>, ResearchInstance> newResearches = new HashMap<>();
            for (Map.Entry<ResourceKey<Research>, ResearchInstance> entry : researches.entrySet()) {
                if (ResearchHelperCommon.getResearch(entry.getKey(), level) != null && entry.getValue().getKey() != null) {
                    newResearches.put(entry.getKey(), entry.getValue());
                }

            }
            researches.clear();
            researches.putAll(newResearches);
        }
    }

    public enum HelpPage {
        TEAM,
    }

    public static void sendHelpMessage(Consumer<Component> sendMessageFunction) {
        sendMessageFunction.accept(Component.literal("> Researchd Teams").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        sendMessageFunction.accept(helpMessage("team", "create <name>", description("Create a new team with the specified name.")));
        sendMessageFunction.accept(helpMessage("team", "list", description("List all teams.")));
        sendMessageFunction.accept(helpMessage("team", "members", description("List all members of your team.")));
        sendMessageFunction.accept(helpMessage("team", "invite <player>", description("Invite a player to your team.")));
        sendMessageFunction.accept(helpMessage("team", "join <player>", description("Join a team that you have been invited to.")));
        sendMessageFunction.accept(helpMessage("team", "leave <next_to_lead>", paramDescription("next_to_lead", "Put 'none' if there's no-one to lead or you're not the leader."), description("Leave your current team.")));
        sendMessageFunction.accept(helpMessage("team", "promote <player>", description("Promote a player to moderator. You got to be the leader to do this.")));
        sendMessageFunction.accept(helpMessage("team", "demote <player>", description("Demote a player from moderator. You got to be the leader to do this.")));
        sendMessageFunction.accept(helpMessage("team", "kick <player>", description("Kick a player from your team. You got to be a moderator or the leader to do this.")));
        sendMessageFunction.accept(helpMessage("team", "transfer-ownership <player>", description("Transfer ownership of the team to another player.")));
        sendMessageFunction.accept(helpMessage("team", "set-name <name>", description("Set a new name for your team.")));
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

    public static void initializeTeamResearches(ResearchTeamMap teamMap, Level level) {
        for (SimpleResearchTeam team : teamMap.researchTeams().values()) {
            Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearches();
            Map<ResourceKey<Research>, ResearchProgress> progress = team.getResearchProgresses();
            Set<GlobalResearch> globalResearches = new HashSet<>(CommonResearchCache.GLOBAL_RESEARCHES.values());

            for (GlobalResearch research : globalResearches) {
                if (progress.containsKey(research.getResearchKey())) continue;

                progress.put(research.getResearchKey(), ResearchProgress.forResearch(research.getResearchKey(), level));
            }

            researches.values().stream().map(ResearchInstance::getResearch).forEach(globalResearches::remove);
            for (GlobalResearch globalResearch : globalResearches) {
                ResearchStatus status = CommonResearchCache.ROOT_RESEARCH != null && CommonResearchCache.ROOT_RESEARCH.is(globalResearch.getResearchKey())
                        ? ResearchStatus.RESEARCHABLE
                        : ResearchStatus.LOCKED;
                researches.put(globalResearch.getResearchKey(), new ResearchInstance(globalResearch, status));
            }
        }
    }

    public static void resolveGlobalResearches(ResearchTeamMap researchTeamMap) {
        for (ResearchTeam team : researchTeamMap.researchTeams().values()) {
            for (Map.Entry<ResourceKey<Research>, ResearchInstance> entry : team.getResearches().entrySet()) {
                entry.setValue(new ResearchInstance(CommonResearchCache.GLOBAL_RESEARCHES.get(entry.getKey()), entry.getValue().getResearchStatus(), entry.getValue().getResearchedPlayer(), entry.getValue().getResearchedTime()));
            }
        }
    }

    public static void refreshPlayerManagement(ResearchTeam team, Level level) {
        if (team == null) return;
        for (TeamMember member : team.getMembers()) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(member.player());
            if (player != null) {
                PacketDistributor.sendToPlayer(player, RefreshPlayerManagementPayload.INSTANCE);
            }
        }
    }
}