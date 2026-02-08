package com.portingdeadmods.researchd.client.utils;

import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.api.client.TechList;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamSettingsScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementList;
import com.portingdeadmods.researchd.client.screens.team.widgets.TeamMembersList;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.networking.team.*;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class ClientResearchTeamHelper {
    public static ResearchTeam getTeam() {
        LocalPlayer player = Minecraft.getInstance().player;
        return ResearchTeamHelper.getTeamByMember(player);
    }

    public static ResearchTeam getTeam(UUID uuid) {
        return ResearchTeamHelper.getTeamByMember(Minecraft.getInstance().level, uuid);
    }

    public static void setTeamNameSynced(String name) {
        ResearchTeam clientTeam = getTeam();
        if (!clientTeam.getName().equals(name)) {
            clientTeam.setName(name);
            PacketDistributor.sendToServer(new TeamSetNamePayload(name));
        }
    }

    public static ResearchTeamRole getPlayerRole(UUID uuid) {
        ResearchTeam clientTeam = getTeam();
        if (clientTeam.isOwner(uuid)) {
            return ResearchTeamRole.OWNER;
        } else if (clientTeam.isModerator(uuid)) {
            return ResearchTeamRole.MODERATOR;
        }
        return ResearchTeamRole.MEMBER;
    }

    public static ResearchTeamRole getRole() {
        LocalPlayer player = Minecraft.getInstance().player;
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

    public static Collection<TeamMember> getTeamMembers() {
        return getTeam().getMembers();
    }

    public static List<TeamMember> getPlayersNotInTeam() {
        ResearchTeam team = getTeam();
		return AllPlayersCache.getUUIDs().stream().filter(uuid -> !team.hasMember(uuid)).map(uuid -> new TeamMember(uuid, ResearchTeamRole.NOT_MEMBER)).toList();
    }

    public static void removeTeamMemberSynced(TeamMember memberProfile) {
        UUID id = memberProfile.player();
        ResearchTeam team = getTeam(id);
        team.removeMember(id);
        PacketDistributor.sendToServer(new ManageMemberPayload(id, true));
        Researchd.LOGGER.debug("Remove player {}", PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, memberProfile.player()));
    }

    public static void sendTeamInviteSynced(TeamMember profileToInvite) {
        UUID invited = profileToInvite.player();
        boolean remove = getTeam().getSocialManager().containsSentInvite(invited);
        LocalPlayer player = Minecraft.getInstance().player;

        ResearchTeam team = getTeam();
        if (remove) {
            team.getSocialManager().removeSentInvite(invited);
        } else {
            team.getSocialManager().addSentInvite(invited);
        }
        Level level = player.level();
        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));

        PacketDistributor.sendToServer(new InvitePlayerPayload(invited, remove));
    }

    public static void promoteTeamMemberSynced(TeamMember member) {
        if (Objects.requireNonNull(member.role()) == ResearchTeamRole.MEMBER) {
            ResearchTeam team = getTeam(member.player());
            team.setRole(member.player(), ResearchTeamRole.MODERATOR);
            PacketDistributor.sendToServer(new ManageModeratorPayload(member.player(), false));
        }
        Researchd.LOGGER.debug("Promoted player {}", PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, member.player()));
    }

    public static void demoteTeamMemberSynced(TeamMember memberProfile) {
        if (Objects.requireNonNull(memberProfile.role()) == ResearchTeamRole.MODERATOR) {
            ResearchTeam team = getTeam(memberProfile.player());
            team.setRole(memberProfile.player(), ResearchTeamRole.MEMBER);
            PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.player(), true));
        }
        Researchd.LOGGER.debug("Demoted player {}", memberProfile.player());
    }

    public static void transferOwnershipSynced(TeamMember nextOwner) {
        ResearchTeam team = getTeam();
        team.setRole(nextOwner.player(), ResearchTeamRole.OWNER);
        PacketDistributor.sendToServer(new TransferOwnershipPayload(nextOwner.player()));
    }

    public static void resolveInstances(ResearchTeam team) {
        Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearches();

        for (Map.Entry<ResourceKey<Research>, ResearchInstance> research : researches.entrySet()) {
            GlobalResearch globalResearch = CommonResearchCache.globalResearches.get(research.getKey());
            if (globalResearch != null) {
                research.setValue(research.getValue().withResearch(globalResearch));
            } else {
                Researchd.LOGGER.debug("RESEARCH");
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
			screen.getResearchQueueWidget().setQueue(getTeam().getQueue());
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
				ClientResearchTeamHelper.getPlayersNotInTeam().forEach(member -> entries.add(new PlayerManagementList.Entry(member, inviteWidget.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
				inviteWidget.getManagementList().refreshEntries(entries);
			}
			TeamMembersList teamMembersList = screen.getTeamMembersList();
			teamMembersList.getItems().clear();
			teamMembersList.getItems().addAll(ClientResearchTeamHelper.getTeamMembers());
			teamMembersList.resort();
            System.out.println(teamMembersList.getItems().size());
		}
	}

	public static void refreshTeamSettingsScreenData() {
		if (Minecraft.getInstance().screen instanceof ResearchTeamSettingsScreen screen) {
			PlayerManagementDraggableWidget playerManagementWindow = screen.getPlayerManagementWindow();
			if (playerManagementWindow != null && !playerManagementWindow.getManagementList().getItems().isEmpty()) {
				List<PlayerManagementList.Entry> entries = new ArrayList<>();
				ClientResearchTeamHelper.getTeamMembers().forEach(member -> entries.add(new PlayerManagementList.Entry(member, playerManagementWindow.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
				playerManagementWindow.getManagementList().refreshEntries(entries);
			}
			PlayerManagementDraggableWidget transferOwnershipWindow = screen.getTransferOwnershipWindow();
			if (transferOwnershipWindow != null && !transferOwnershipWindow.getManagementList().getItems().isEmpty()) {
				List<PlayerManagementList.Entry> entries = new ArrayList<>();
				ClientResearchTeamHelper.getTeamMembers().forEach(member -> entries.add(new PlayerManagementList.Entry(member, transferOwnershipWindow.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
				transferOwnershipWindow.getManagementList().refreshEntries(entries);
			}
		}
	}

	public static void refreshResearchTeamScreenData() {
		refreshTeamScreenData();
		refreshTeamSettingsScreenData();
	}
}
