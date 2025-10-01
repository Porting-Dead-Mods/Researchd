package com.portingdeadmods.researchd.client.utils;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import com.portingdeadmods.researchd.networking.team.*;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class ClientResearchTeamHelper {
    public static SimpleResearchTeam getTeam() {
        LocalPlayer player = Minecraft.getInstance().player;
        return ResearchTeamHelper.getResearchTeam(player);
    }

    public static SimpleResearchTeam getTeam(UUID uuid) {
        return ResearchTeamHelper.getResearchTeamByUUID(Minecraft.getInstance().level, uuid);
    }

    public static void setTeamNameSynced(String name) {
        SimpleResearchTeam clientTeam = getTeam();
        if (!clientTeam.getName().equals(name)) {
            clientTeam.setName(name);
            PacketDistributor.sendToServer(new TeamSetNamePayload(name));
        }
    }

    public static ResearchTeamRole getPlayerRole(UUID uuid) {
        SimpleResearchTeam clientTeam = getTeam();
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
        SimpleResearchTeam team = getTeam();
        List<UUID> uuids = ClientPlayerUtils.getPlayerUUIDs();
        List<TeamMember> playersNotInTeam = new ArrayList<>();
        for (UUID uuid : uuids.stream().filter(uuid -> !team.hasMember(uuid)).toList()) {
            playersNotInTeam.add(new TeamMember(uuid, ResearchTeamRole.MODERATOR));
        }

        return playersNotInTeam;
    }

    public static void removeTeamMemberSynced(TeamMember memberProfile) {
        UUID id = memberProfile.player();
        SimpleResearchTeam team = getTeam(id);
        if (team != null) {
            team.removeMember(id);
            PacketDistributor.sendToServer(new ManageMemberPayload(id, true));
            Researchd.LOGGER.debug("Remove player {}", PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, memberProfile.player()));
        }
    }

    public static void sendTeamInviteSynced(TeamMember profileToInvite) {
        UUID invited = profileToInvite.player();
        boolean remove = getTeam().getSentInvites().contains(invited);
        LocalPlayer player = Minecraft.getInstance().player;

        SimpleResearchTeam team = getTeam();
        if (team != null) {
            if (remove) {
                team.removeSentInvite(invited);
            } else {
                team.addSentInvite(invited);
            }
            Level level = player.level();
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, ResearchdSavedData.TEAM_RESEARCH.get().getData(level));
        }

        PacketDistributor.sendToServer(new InvitePlayerPayload(invited, remove));
    }

    public static void promoteTeamMemberSynced(TeamMember member) {
        if (Objects.requireNonNull(member.role()) == ResearchTeamRole.MEMBER) {
            SimpleResearchTeam team = getTeam(member.player());
            team.setRole(member.player(), ResearchTeamRole.MODERATOR);
            PacketDistributor.sendToServer(new ManageModeratorPayload(member.player(), false));
        }
        Researchd.LOGGER.debug("Promoted player {}", PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, member.player()));
    }

    public static void demoteTeamMemberSynced(TeamMember memberProfile) {
        if (Objects.requireNonNull(memberProfile.role()) == ResearchTeamRole.MODERATOR) {
            SimpleResearchTeam team = getTeam(memberProfile.player());
            team.setRole(memberProfile.player(), ResearchTeamRole.MEMBER);
            PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.player(), true));
        }
        Researchd.LOGGER.debug("Demoted player {}", memberProfile.player());
    }

    public static void transferOwnershipSynced(TeamMember nextOwner) {
        SimpleResearchTeam team = getTeam();
        team.setRole(nextOwner.player(), ResearchTeamRole.OWNER);
        PacketDistributor.sendToServer(new TransferOwnershipPayload(nextOwner.player()));
    }

    public static void resolveInstances(SimpleResearchTeam team) {
        Map<ResourceKey<Research>, ResearchInstance> researches = team.getResearches();

        for (Map.Entry<ResourceKey<Research>, ResearchInstance> research : researches.entrySet()) {
            GlobalResearch globalResearch = CommonResearchCache.GLOBAL_RESEARCHES.get(research.getKey());
            if (globalResearch != null) {
                research.setValue(research.getValue().withResearch(globalResearch));
            } else {
                Researchd.LOGGER.debug("RESEARCH");
            }
        }

    }
}
