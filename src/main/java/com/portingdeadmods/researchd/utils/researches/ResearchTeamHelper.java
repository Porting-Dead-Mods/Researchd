package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.*;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.cache.ClearGraphCachePayload;
import com.portingdeadmods.researchd.networking.team.RefreshPlayerManagementPayload;
import com.portingdeadmods.researchd.networking.team.manager.AddTeamPayload;
import com.portingdeadmods.researchd.networking.team.manager.RemoveTeamPayload;
import com.portingdeadmods.researchd.networking.team.manager.SyncTeamPayload;
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
    public static @NotNull ResearchTeam getTeamByMember(@NotNull Player player) {
        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(player.level());
        return teamManager.getTeamById(player.getUUID());
    }

    /**
     * Removes the player from their researchPack team.
     * Updates the team map
     *
     * @param player the player to be removed from their team
     */
    public static void removeMember(ServerPlayer player) {
        UUID uuid = player.getUUID();
        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(player.level());
        @NotNull ResearchTeam team = teamManager.getTeamByPlayerId(uuid);

        team.removeMember(uuid);
    }

    /**
     * Get the permission level of a team's member
     * corresponding to the permissions given
     * to them based on their role
     *
     * @param player The player whose permission level you want to get
     * @return integer value permission level ( {@link ResearchTeamRole#getPermissionLevel()} )
     */
    public static int getPermissionLevel(@NotNull Player player) {
        return getPermissionLevel(player.getUUID(), player.level());
    }

    public static int getPermissionLevel(UUID player, Level level) {
        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(level);
        ResearchTeam team = teamManager.getTeamByPlayerId(player);

        return team.getMember(player).role().getPermissionLevel();
    }

    public static boolean arePlayersSameTeam(@NotNull Level level, UUID uuid1, UUID uuid2) {
        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(level);

        ResearchTeam team1 = teamManager.getTeamByPlayerId(uuid1);
        ResearchTeam team2 = teamManager.getTeamByPlayerId(uuid2);

        return team1.getId().equals(team2.getId());
    }

    /* TEAM HANDLER METHODS
     * Operations:
     *
     * Role: EVERYONE
     * - Enter (If an invitation is present)
     *   - Will leave the team the player was in previously
     * - Leave
     *   - Will leave the team the player is currently in and delete it.
     *     Will then create a new default team for the player.
     *     REQUIRES CONFIRMATION
     *
     * Role: MODERATOR
     * - Remove Member
     *   - Will remove player and create a new default team for removed player
     * - Invite Member (assumes that player is not already member of the team)
     *   - Will send an invitation to specified player, allowing player to join
     *
     * Role: OWNER
     * - Transfer Ownership
     *   - Set specified player as owner and make current owner moderator
     * - Promote Member
     *   - Set member role of specified player to moderator
     * - Demote Member
     *   - Set member role of specified player to member
     */

    public static void handleEnterTeamSynced(@NotNull ServerPlayer requester, ResearchTeamImpl team) {
        Level level = requester.level();
        UUID requesterId = requester.getUUID();

        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(level);

        // Already in Team (with multiple people) -> Return with error msg
        if (getTeamByMember(requester).getMembers().size() > 1) {
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.ALREADY_IN_TEAM));
            return;
        }

        // Alone in team -> Enter the new team | TODO: Add Invite Syncs from FTB Teams for the sake of compat. Currently it should work without
        if (team != null && ((team.getSocialManager().containsSentInvite(requesterId)) || ResearchdCompatHandler.isFTBTeamsEnabled())) {
            ResearchTeamHelper.handleLeaveTeam(requester);

            teamManager.addTeam(team);
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.YOU_JOINED_TEAM, team.getName()));

            for (TeamMember member : team.getMembers()) {
                Player memberPlayer = level.getPlayerByUUID(member.player());
                if (memberPlayer != null) {
                    memberPlayer.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.PLAYER_JOINED_TEAM, PlayerUtils.getPlayerNameFromUUID(level, requesterId)));
                }
            }

            team.addMember(requesterId);
            team.getSocialManager().removeSentInvite(requesterId);
            team.setChanged();

            PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));

            refreshPlayerManagement(team, level);
            //PacketDistributor.sendToPlayer(requester, new RefreshResearchesPayload());
        }
    }


    public static void handleIgnoreTeam(@NotNull ServerPlayer requester, UUID memberOfTeam) {
        Level level = requester.level();

        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(level);
        ResearchTeamImpl team = (ResearchTeamImpl) teamManager.getTeamByPlayerId(memberOfTeam);

        if (team != null) {
            team.getSocialManager().addIgnore(requester.getUUID());
            team.setChanged();
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.IGNORE, team.getName()));

            PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));
        }
    }

    public static void handleLeaveTeam(@NotNull ServerPlayer requester, @Nullable UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();

        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(level);

        ResearchTeam team = getTeamByMember(requester);

        // Is Owner -> Handle cases of being alone / with multiple people
        if (team.isOwner(requesterId)) {
            // FIXME: Add warning, since this would reset all research progress the team has made
            if (team.getMembers().size() <= 1) {
                // Alone In Team -> Leaving creates default team for player

                teamManager.removeTeam(team.getId());
                PacketDistributor.sendToAllPlayers(new RemoveTeamPayload(team.getId()));

                if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.LEFT_TEAM));

                createTeamForPlayerSynced(level, requesterId, teamManager);
            } else {
                // Not Alone In Team -> Transfer Ownership

                // Team Leader Not Specified
                if (nextToLead == null || nextToLead.equals(PlayerUtils.EmptyUUID)) {
                    if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                        requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_NEXT_LEADER));
                    return;
                }

                // Team Leader Specified
                handleTransferOwnership(requester, nextToLead);
            }
        } else {
            // Is not Owner -> Just remove member out of the team and create a default one.
            removeMember(requester);
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.LEFT_TEAM));
            createTeamForPlayerSynced(level, requesterId, teamManager);
            PacketDistributor.sendToPlayer(requester, ClearGraphCachePayload.INSTANCE);

            refreshPlayerManagement(team, level);
        }

    }

    public static void handleLeaveTeam(@NotNull ServerPlayer requester) {
        ResearchTeamHelper.handleLeaveTeam(requester, PlayerUtils.EmptyUUID);
    }

    public static void handleManageMember(@NotNull ServerPlayer requester, UUID member, boolean remove) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap teamManager = (ResearchTeamMap) ResearchdApi.getTeamManager(level);

        // Error safety (handling yourself)
        if (requester.getUUID().equals(member)) {
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(getIllegalMessage());
            return;
        }

        // Error safety (players are not in same team)
        if (!arePlayersSameTeam(requester.level(), requester.getUUID(), member)) return;

        // Permission Check
        if (getPermissionLevel(requester) >= ResearchTeamRole.MODERATOR.getPermissionLevel() && (getPermissionLevel(requester) > getPermissionLevel(member, requester.level()))) {
            ResearchTeamImpl team = (ResearchTeamImpl) getTeamByMember(requester);
            if (remove) {
                // Remove member and put them into a default team with a status message

                team.removeMember(member);
                PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));

                if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.REMOVED, PlayerUtils.getPlayerNameFromUUID(level, member)));

                ServerPlayer kickedPlayer = server.getPlayerList().getPlayer(member);
                if (kickedPlayer != null) {
                    PacketDistributor.sendToPlayer(kickedPlayer, ClearGraphCachePayload.INSTANCE);
                    kickedPlayer.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.KICKED, getTeamByMember(requester).getName()));
                }

                createTeamForPlayerSynced(level, member, teamManager);
            } else {
                // Invite Member

                team.getSocialManager().addSentInvite(member);
                PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));
            }

            refreshPlayerManagement(getTeamByMember(requester), level);
        } else {
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    private static void createTeamForPlayerSynced(Level level, UUID member, ResearchTeamManager teamManager) {
        ResearchTeamImpl newTeam = (ResearchTeamImpl) teamManager.createDefaultTeam(member, level);
        teamManager.addTeam(newTeam);
        PacketDistributor.sendToAllPlayers(new AddTeamPayload(newTeam));
    }

    public static void handleManageModerator(@NotNull ServerPlayer requester, UUID moderator, boolean remove) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap teamManager = (ResearchTeamMap) ResearchdApi.getTeamManager(level);

        // Error Safety (handling yourself)
        if (requester.getUUID().equals(moderator)) {
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(getIllegalMessage());
            return;
        }

        // Permission Check (is Owner)
        if (getPermissionLevel(requester) == ResearchTeamRole.OWNER.getPermissionLevel()) {
            if (arePlayersSameTeam(requester.level(), requester.getUUID(), moderator)) {
                ResearchTeamImpl team = (ResearchTeamImpl) getTeamByMember(requester);

                if (remove) {
                    team.setRole(moderator, ResearchTeamRole.MEMBER);
                    if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                        requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.DEMOTED, PlayerUtils.getPlayerNameFromUUID(level, moderator)));
                } else {
                    team.setRole(moderator, ResearchTeamRole.MODERATOR);
                    if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                        requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.PROMOTED, PlayerUtils.getPlayerNameFromUUID(level, moderator)));
                }
                PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));

                refreshPlayerManagement(getTeamByMember(requester), level);
            } else {
                if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.BAD_INPUT));
            }
        } else {
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    public static void handleSetName(@NotNull ServerPlayer requester, String name) {
        UUID requesterId = requester.getUUID();
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap teamManager = (ResearchTeamMap) ResearchdApi.getTeamManager(level);

        // Permission Check (is Owner)
        if (getPermissionLevel(requester) == ResearchTeamRole.OWNER.getPermissionLevel()) {
            if (name.isEmpty()) {
                if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NAME_CANNOT_BE_EMPTY));
                return;
            }

            ResearchTeamImpl team = (ResearchTeamImpl)getTeamByMember(requester);
            String oldname = team.getName();
            team.setName(name);
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NEW_TEAM_NAME, oldname, name));

            PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));
            refreshPlayerManagement(team, level);
        } else {
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.NO_PERMS));
        }
    }

    public static void handleTransferOwnership(@NotNull ServerPlayer requester, UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();
        ResearchTeamMap teamManager = (ResearchTeamMap) ResearchdApi.getTeamManager(level);

        // Permission Check (is Owner)
        if (getPermissionLevel(requester) == ResearchTeamRole.OWNER.getPermissionLevel()) {
            if (arePlayersSameTeam(level, requesterId, nextToLead)) {
                ResearchTeamImpl team = (ResearchTeamImpl) getTeamByMember(requester);

                // Set the new leader
                team.setRole(nextToLead, ResearchTeamRole.OWNER);

                // Set the old leader as moderator
                team.setRole(requesterId, ResearchTeamRole.MODERATOR);

                PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));

                if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.TRANSFERRED_OWNERSHIP, PlayerUtils.getPlayerNameFromUUID(level, nextToLead)));
                refreshPlayerManagement(team, level);
            } else {
                if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                    requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.BAD_INPUT));
            }
        }
    }

    public static void handleListMembers(@NotNull ServerPlayer requester) {
        ResearchTeam team = getTeamByMember(requester);
        requester.sendSystemMessage(formatMembers(team, requester.level()));
    }

    public static Component formatMembers(ResearchTeam team, Level level) {
        MutableComponent formattedTeam = Component.literal(team.getName()).withStyle(ChatFormatting.AQUA);
        formattedTeam.append(Component.literal(" has %d member%s: ".formatted(team.getMembers().size(), team.getMembers().size() == 1 ? "" : "s")).withStyle(ChatFormatting.WHITE));

        for (TeamMember member : team.getMembers()) {
            Player player = level.getPlayerByUUID(member.player());
            if (player != null)
                formattedTeam.append(Component.literal(player.getName().getString() + " ").withStyle(ChatFormatting.AQUA));
        }

        return formattedTeam;
    }

    public static void handleSendInviteToPlayer(@NotNull ServerPlayer requester, UUID invited, boolean remove) {
        ResearchTeamImpl team = (ResearchTeamImpl) getTeamByMember(requester);
        ServerLevel level = requester.serverLevel();

        // Error Safety (inviting yourself)
        if (requester.getUUID().equals(invited)) {
            requester.sendSystemMessage(getIllegalMessage());
            return;
        }

        if (remove) {
            team.getSocialManager().removeSentInvite(invited);
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.REMOVED_INVITE, AllPlayersCache.getName(invited)));
            team.setChanged();
            PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));
        } else {
            team.getSocialManager().addSentInvite(invited);
            team.setChanged();
            ServerPlayer invitedPlayer = level.getServer().getPlayerList().getPlayer(invited);
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
            if (!ResearchdCompatHandler.isFTBTeamsEnabled())
                requester.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Team.SENT_INVITE, AllPlayersCache.getName(invited), team.getName()));

            PacketDistributor.sendToAllPlayers(new SyncTeamPayload(team));
        }

        refreshPlayerManagement(team, level);

    }

    public static Component getFormattedDump(Level level) {
        List<Component> dump = new ArrayList<>();

        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(level);


        dump.add(Component.literal("---- Researchd Teams ----").withStyle(ChatFormatting.GOLD));
        for (Iterator<ResearchTeam> iterator = teamManager.getTeams().iterator(); iterator.hasNext(); ) {
            ResearchTeam team = iterator.next();
            dump.add(Component.literal(ChatFormatting.GREEN + team.getName() + ChatFormatting.RESET).append(" with %s member%s".formatted(ChatFormatting.GREEN.toString() + team.getMembers().size() + ChatFormatting.RESET, team.getMembers().size() == 1 ? "" : "s")));
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
        for (ResearchTeam team : teamMap.getTeams()) {
            Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearches();
            Map<ResourceKey<Research>, ResearchInstance> newResearches = new HashMap<>();
            for (Map.Entry<ResourceKey<Research>, ResearchInstance> entry : researches.entrySet()) {
                if (ResearchdApi.getResearchManager().lookupResearch(entry.getKey(), level) != null && entry.getValue().getResearch() != null) {
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

    // TODO: Simplify?
    public static void initializeTeamResearches(ResearchTeamMap teamMap, Level level) {
        ResearchManager researchManager = ResearchdApi.getResearchManager();

        for (ResearchTeam team : teamMap.getTeams()) {
            Map<ResourceKey<Research>, ResearchInstance> teamResearches = team.getResearches();
            Map<ResourceKey<Research>, ResearchProgress> teamProgress = team.getResearchProgresses();
            Set<ResourceKey<Research>> allResearches = new HashSet<>(ResearchdApi.getResearchManager().getResearches());

            for (ResourceKey<Research> research : allResearches) {
                if (teamProgress.containsKey(research)) continue;

                teamProgress.put(research, ResearchProgress.forResearch(research, level));
            }

            // Set root researches as researchable
            teamResearches.values().stream().map(ResearchInstance::getResearch).forEach(allResearches::remove);

            for (ResearchInstance research : teamResearches.values()) {
                Research r = researchManager.lookupResearch(research.getResearch(), level);
                List<ResourceKey<Research>> pageRoots = researchManager.getRootsForPage(r.researchPage());

                if (research.getResearchStatus() == ResearchStatus.RESEARCHABLE_AFTER_QUEUE && pageRoots.contains(research.getResearch())) {
                    research.setResearchStatus(ResearchStatus.RESEARCHABLE);
                }
            }

            for (ResourceKey<Research> research : allResearches) {
                Research r = researchManager.lookupResearch(research, level);
                List<ResourceKey<Research>> pageRoots = researchManager.getRootsForPage(r.researchPage());
                ResearchStatus status;
                if (pageRoots.contains(research)) {
                    status = ResearchStatus.RESEARCHABLE;
                } else {
                    status = ResearchStatus.LOCKED;
                }
                teamResearches.put(research, new ResearchInstance(research, status));
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