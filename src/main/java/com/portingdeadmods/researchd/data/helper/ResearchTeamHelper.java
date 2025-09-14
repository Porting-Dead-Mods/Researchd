package com.portingdeadmods.researchd.data.helper;

import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.networking.team.RefreshResearchesPayload;
import com.portingdeadmods.researchd.networking.team.TransferOwnershipPayload;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import net.minecraft.ChatFormatting;
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

    public static ResearchTeam getResearchTeam(Player player) {
        UUID uuid = player.getUUID();
        return ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(uuid);
    }

    public static ResearchTeam getResearchTeamByUUID(Level level, UUID uuid) {
        return ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getTeamByMember(uuid);
    }

    public static boolean isInATeam(Player player) {
        UUID uuid = player.getUUID();
        return ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(uuid) != null;
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

        if (isInATeam(requester)) {
            requester.sendSystemMessage(Component.literal("You're already in a team!").withStyle(ChatFormatting.RED));
            return;
        }
        if (team != null && (team.getReceivedInvites().contains(requesterId))) {
            requester.sendSystemMessage(Component.literal("You successfully joined: " + team.getName() + "!").withStyle(ChatFormatting.GREEN));
            team.addMember(requesterId);
            team.removeSentInvite(requesterId);
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);

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

    public static void handleLeaveTeam(ServerPlayer requester, @Nullable UUID nextToLead) {
        MinecraftServer server = requester.getServer();
        ServerLevel level = server.overworld();
        UUID requesterId = requester.getUUID();

        ResearchTeamMap savedData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (!isInATeam(requester)) {
            requester.sendSystemMessage(Component.literal("You're not in a team!").withStyle(ChatFormatting.RED));
            return;
        }

        // Handle the case of transfering ownership
        if (isResearchTeamLeader(requester)) {
            if (getResearchTeam(requester).getMembers().size() <= 1) {
                savedData.getResearchTeams().remove(requesterId);
                requester.sendSystemMessage(Component.literal("You successfully abandoned your team!").withStyle(ChatFormatting.GREEN));

                savedData.getResearchTeams().put(requesterId, ResearchTeam.createDefaultTeam(requester));
            } else {
                if (nextToLead == null) {
                    requester.sendSystemMessage(Component.literal("You need to specify the next leader!").withStyle(ChatFormatting.RED));
                    return;
                }
                PacketDistributor.sendToServer(new TransferOwnershipPayload(nextToLead));
            }

            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            PacketDistributor.sendToPlayer(requester, new RefreshResearchesPayload());
            return;
        }

        if (getPermissionLevel(requester) == 1) {
            removeModFromTeam(requester);
        }
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
                requester.sendSystemMessage(Component.literal("Member " + PlayerUtils.getPlayerNameFromUUID(level, member) + " removed!").withStyle(ChatFormatting.GREEN));
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            } else {
                getResearchTeam(requester).addSentInvite(member);
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
            }
        } else {
            requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
        }
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
                    requester.sendSystemMessage(Component.literal("Moderator " + PlayerUtils.getPlayerNameFromUUID(level, moderator) + " removed!").withStyle(ChatFormatting.GREEN));
                    ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
                } else {
                    getResearchTeam(requester).addModerator(moderator);
                    requester.sendSystemMessage(Component.literal("Moderator " + PlayerUtils.getPlayerNameFromUUID(level, moderator) + " added!").withStyle(ChatFormatting.GREEN));
                    ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
                }
            } else {
                requester.sendSystemMessage(Component.literal("Player is not in the same team with you!").withStyle(ChatFormatting.RED));
            }
        } else {
            requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
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
            requester.sendSystemMessage(Component.literal("Team name changed from " + oldname + " to " + name).withStyle(ChatFormatting.GREEN));
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, savedData);
        } else {
            requester.sendSystemMessage(Component.literal("You don't have the permission to do that!").withStyle(ChatFormatting.RED));
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
                requester.sendSystemMessage(Component.literal("Ownership transfered to " + nextToLead).withStyle(ChatFormatting.GREEN));
            } else {
                requester.sendSystemMessage(Component.literal("You can't transfer ownership to someone who's not in your team!").withStyle(ChatFormatting.RED));
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
                requester.sendSystemMessage(Component.literal("Invite to " + PlayerUtils.getPlayerNameFromUUID(requester.level(), invited) + " removed").withStyle(ChatFormatting.GOLD));
            } else {
                team.addSentInvite(invited);
                requester.sendSystemMessage(Component.literal("Invite sent to " + PlayerUtils.getPlayerNameFromUUID(requester.level(), invited)).withStyle(ChatFormatting.GREEN));
            }
            Level level = requester.level();
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
        } else {
            requester.sendSystemMessage(Component.literal("You got to be in a team to do that! Create one with /researchd team create <name>"));
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
        for (ResearchTeam team : ResearchdSavedData.TEAM_RESEARCH.get().getData(level).getResearchTeams().values()) {
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
            Map<ResourceKey<Research>, List<ResearchMethodProgress<?>>> progress = team.getResearchProgress().progress();
            Set<GlobalResearch> globalResearches = new HashSet<>(CommonResearchCache.GLOBAL_RESEARCHES.values());

            for (GlobalResearch research : globalResearches) {
                if (progress.containsKey(research.getResearchKey())) continue;

                progress.put(research.getResearchKey(), ResearchMethodProgress.collectFromRoot(research.getResearch(level.registryAccess()).researchMethod()));
            }

            researches.values().stream().map(ResearchInstance::getResearch).toList().forEach(globalResearches::remove);
            for (GlobalResearch globalResearch : globalResearches) {
                researches.put(globalResearch.getResearchKey(), new ResearchInstance(globalResearch, ResearchStatus.LOCKED));
            }
        }
        ResearchdSavedData.TEAM_RESEARCH.get().setData((Level) level, data);
    }

    public static void resolveGlobalResearches(ResearchTeamMap researchTeamMap) {
        for (ResearchTeam team : researchTeamMap.getResearchTeams().values()) {
            TeamResearchProgress researchProgress = team.getMetadata().getResearchProgress();
            for (Map.Entry<ResourceKey<Research>, ResearchInstance> entry : researchProgress.researches().entrySet()) {
                entry.setValue(new ResearchInstance(CommonResearchCache.GLOBAL_RESEARCHES.get(entry.getKey()), entry.getValue().getResearchStatus()));
            }
        }
    }
}
