package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.api.client.TechList;
import com.portingdeadmods.researchd.api.research.ResearchRelations;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamSettingsScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementList;
import com.portingdeadmods.researchd.client.screens.team.widgets.TeamMembersList;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.networking.team.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ResearchTeamHelperClient {
    public static @Nullable ResearchTeam getTeam() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return null;

        return ResearchTeamHelperServer.getTeamByMember(player);
    }

    public static @Nullable ResearchTeam getTeam(UUID uuid) {
        ResearchTeamManager teamManager = ResearchdApi.getTeamManager(Minecraft.getInstance().level);
        return teamManager != null ? teamManager.getTeamByPlayerId(uuid) : null;
    }

    public static void setTeamNameSynced(String name) {
        ResearchTeam clientTeam = getTeam();
        if (clientTeam != null && !clientTeam.getName().equals(name)) {
            clientTeam.setName(name);
            PacketDistributor.sendToServer(new TeamSetNamePayload(name));
        }
    }

    public static @NotNull ResearchTeamRole getPlayerRole(UUID uuid) {
        ResearchTeam clientTeam = getTeam();
        if (clientTeam == null) return ResearchTeamRole.NOT_MEMBER;

        if (clientTeam.isOwner(uuid)) {
            return ResearchTeamRole.OWNER;
        } else if (clientTeam.isModerator(uuid)) {
            return ResearchTeamRole.MODERATOR;
        }
        return ResearchTeamRole.MEMBER;
    }

    public static @NotNull ResearchTeamRole getRole() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return ResearchTeamRole.NOT_MEMBER;

        return getPlayerRole(player.getUUID());
    }

    /**
     * Returns the permission level of the player. <br>
     * <span style="color:red">0 - Member</span> <br>
     * <span style="color:yellow">1 - Moderator</span> <br>
     * <span style="color:green">2 - Owner</span> <br>
     */
    public static int getPlayerPermissionLevel(UUID uuid) {
        return getPlayerRole(uuid).getPermissionLevel();
    }

    /**
     * Returns the permission level of the player. <br>
     * <span style="color:red">0 - Member</span> <br>
     * <span style="color:yellow">1 - Moderator</span> <br>
     * <span style="color:green">2 - Owner</span> <br>
     */
    public static int getPlayerPermissionLevel(Player player) {
        return getPlayerRole(player.getUUID()).getPermissionLevel();
    }

    public static @NotNull Collection<TeamMember> getTeamMembers() {
        ResearchTeam team = getTeam();
        return team != null ? team.getMembers() : List.of();
    }

    public static @NotNull List<TeamMember> getPlayersNotInTeam() {
        ResearchTeam team = getTeam();
		return AllPlayersCache.getUUIDs().stream().filter(uuid -> team == null || !team.hasMember(uuid)).map(uuid -> new TeamMember(uuid, ResearchTeamRole.NOT_MEMBER)).toList();
    }

    public static void removeTeamMemberSynced(TeamMember memberProfile) {
        UUID id = memberProfile.player();
        ResearchTeam team = getTeam(id);
        if (team == null) return;

        team.removeMember(id);
        PacketDistributor.sendToServer(new ManageMemberPayload(id, true));
        Researchd.LOGGER.debug("Remove player {}", PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, memberProfile.player()));
    }

    public static void sendTeamInviteSynced(TeamMember profileToInvite) {
        UUID invited = profileToInvite.player();
        ResearchTeamImpl team = (ResearchTeamImpl) getTeam();
        if (team == null) return;

        boolean remove = team.getSocialManager().containsSentInvite(invited);
        if (remove) {
            team.getSocialManager().removeSentInvite(invited);
        } else {
            team.getSocialManager().addSentInvite(invited);
        }
        team.setChanged();

        PacketDistributor.sendToServer(new InvitePlayerPayload(invited, remove));
    }

    public static void promoteTeamMemberSynced(TeamMember member) {
        if (Objects.requireNonNull(member.role()) == ResearchTeamRole.MEMBER) {
            ResearchTeam team = getTeam(member.player());
            if (team == null) return;

            team.setRole(member.player(), ResearchTeamRole.MODERATOR);
            PacketDistributor.sendToServer(new ManageModeratorPayload(member.player(), false));
        }
        Researchd.LOGGER.debug("Promoted player {}", PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, member.player()));
    }

    public static void demoteTeamMemberSynced(TeamMember memberProfile) {
        if (Objects.requireNonNull(memberProfile.role()) == ResearchTeamRole.MODERATOR) {
            ResearchTeam team = getTeam(memberProfile.player());
            if (team == null) return;

            team.setRole(memberProfile.player(), ResearchTeamRole.MEMBER);
            PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.player(), true));
        }
        Researchd.LOGGER.debug("Demoted player {}", memberProfile.player());
    }

    public static void transferOwnershipSynced(TeamMember nextOwner) {
        ResearchTeam team = getTeam();
        if (team == null) return;

        team.setRole(nextOwner.player(), ResearchTeamRole.OWNER);
        PacketDistributor.sendToServer(new TransferOwnershipPayload(nextOwner.player()));
    }

    public static void resolveInstances(@Nullable ResearchTeam team) {
        if (team == null || ResearchdApi.getResearchManager() == null) return;

        Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearches();

        for (Map.Entry<ResourceKey<Research>, ResearchInstance> entry : researches.entrySet()) {
            ResearchRelations researchRelations = ResearchdApi.getResearchManager().getRelationsForResearch(entry.getKey());
            if (researchRelations != null) {
                entry.setValue(entry.getValue().withResearch(entry.getKey()));
            }
        }
    }

    public static void refreshGraphData() {
        if (Minecraft.getInstance().screen instanceof ResearchScreen screen) {
            ResearchGraph graph = screen.getResearchGraph();
            if (graph != null) graph.nodes().forEach((key, node) -> node.fetchInstanceFromTeam());
        }

	    ResearchGraphCache.getAll().forEach(graph -> graph.nodes().forEach((key, node) -> node.fetchInstanceFromTeam()));
    }

    public static void refreshTechListData() {
        if (Minecraft.getInstance().screen instanceof ResearchScreen screen) {
            screen.getTechListWidget().setTechList(TechList.getClientTechList());
            screen.getTechList().sortTechList();
        }
    }

	public static void refreshResearchQueueData() {
		if (Minecraft.getInstance().screen instanceof ResearchScreen screen) {
			ResearchTeam team = getTeam();
			if (team == null) return;

			screen.getResearchQueueWidget().setQueue(team.getQueue());
		}
	}

    public static void refreshResearchScreenData() {
        refreshGraphData();
        refreshTechListData();
		refreshResearchQueueData();
    }

	public static void refreshTeamScreenData() {
		if (Minecraft.getInstance().screen instanceof ResearchTeamScreen screen) {
			PlayerManagementDraggableWidget inviteWidget = screen.getInviteWidget();
			if (inviteWidget != null && !inviteWidget.getManagementList().getItems().isEmpty()) {
				List<PlayerManagementList.Entry> entries = new ArrayList<>();
				ResearchTeamHelperClient.getPlayersNotInTeam().forEach(member -> entries.add(new PlayerManagementList.Entry(member, inviteWidget.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
				inviteWidget.getManagementList().refreshEntries(entries);
			}
			TeamMembersList teamMembersList = screen.getTeamMembersList();
			teamMembersList.getItems().clear();
			teamMembersList.getItems().addAll(ResearchTeamHelperClient.getTeamMembers());
			teamMembersList.resort();
            System.out.println(teamMembersList.getItems().size());
		}
	}

	public static void refreshTeamSettingsScreenData() {
		if (Minecraft.getInstance().screen instanceof ResearchTeamSettingsScreen screen) {
			PlayerManagementDraggableWidget playerManagementWindow = screen.getPlayerManagementWindow();
			if (playerManagementWindow != null && !playerManagementWindow.getManagementList().getItems().isEmpty()) {
				List<PlayerManagementList.Entry> entries = new ArrayList<>();
				ResearchTeamHelperClient.getTeamMembers().forEach(member -> entries.add(new PlayerManagementList.Entry(member, playerManagementWindow.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
				playerManagementWindow.getManagementList().refreshEntries(entries);
			}
			PlayerManagementDraggableWidget transferOwnershipWindow = screen.getTransferOwnershipWindow();
			if (transferOwnershipWindow != null && !transferOwnershipWindow.getManagementList().getItems().isEmpty()) {
				List<PlayerManagementList.Entry> entries = new ArrayList<>();
				ResearchTeamHelperClient.getTeamMembers().forEach(member -> entries.add(new PlayerManagementList.Entry(member, transferOwnershipWindow.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
				transferOwnershipWindow.getManagementList().refreshEntries(entries);
			}
		}
	}

	public static void refreshResearchTeamScreenData() {
		refreshTeamScreenData();
		refreshTeamSettingsScreenData();
	}
}
