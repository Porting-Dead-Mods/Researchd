package com.portingdeadmods.researchd.client.utils;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import com.portingdeadmods.researchd.networking.team.ManageMemberPayload;
import com.portingdeadmods.researchd.networking.team.ManageModeratorPayload;
import com.portingdeadmods.researchd.networking.team.TeamSetNamePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientResearchTeamHelper {
    public static ResearchTeam getTeam() {
        LocalPlayer player = Minecraft.getInstance().player;
        return ResearchTeamHelper.getResearchTeam(player);
    }
    public static ResearchTeam getTeam(UUID uuid) {
        return ResearchTeamHelper.getResearchTeamByUUID(Minecraft.getInstance().level, uuid);
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

    /**
     * Returns the permission level of the player. <br>
     * <span color="red">0 - Member</span> <br>
     * <span color="yellow">1 - Moderator</span> <br>
     * <span color="green">2 - Owner</span> <br>
     */
    public static int getPlayerPermissionLevel(UUID uuid) {
        return getPlayerRole(uuid).getPermissionLevel();
    }

    /**
     * Returns the permission level of the player. <br>
     * <span color="red">0 - Member</span> <br>
     * <span color="yellow">1 - Moderator</span> <br>
     * <span color="green">2 - Owner</span> <br>
     */
    public static int getPlayerPermissionLevel(Player player) {
        return getPlayerRole(player.getUUID()).getPermissionLevel();
    }

    public static List<GameProfile> getTeamMembers() {
        Minecraft mc = Minecraft.getInstance();
        ResearchTeam researchTeam = getTeam();

        List<GameProfile> players;
        if (!mc.isSingleplayer()){
            players = mc.getCurrentServer().players.sample();
        } else {
            players = new ArrayList<>();
            players.add(mc.player.getGameProfile());
            players.add(ResearchTeam.DEBUG_MEMBER);
        }
        return researchTeam.getMembers().stream().map(uuid -> {
            for (GameProfile profile : players) {
                if (profile.getId().equals(uuid)) {
                    return profile;
                }
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    public static void removeTeamMemberSynced(GameProfile memberProfile) {
        UUID id = memberProfile.getId();
        ResearchTeam team = getTeam(id);
        if (team != null) {
            team.removeMember(id);
            if (getPlayerRole(id) == ResearchTeamRole.MODERATOR) {
                team.removeModerator(id);
            }
            PacketDistributor.sendToServer(new ManageMemberPayload(id, true));
            Researchd.LOGGER.debug("Remove member {}", memberProfile.getName());
        }
    }

    public static void promoteTeamMemberSynced(GameProfile memberProfile) {
        switch (getPlayerRole(memberProfile.getId())) {
            case OWNER -> {}
            case MODERATOR -> {}
            case MEMBER -> {
                ResearchTeam team = getTeam(memberProfile.getId());
                team.addModerator(memberProfile.getId());
                PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.getId(), false));
            }
        }
        Researchd.LOGGER.debug("Promoted member {}", memberProfile.getName());
    }

    public static void demoteTeamMemberSynced(GameProfile memberProfile) {
        switch (getPlayerRole(memberProfile.getId())) {
            case OWNER -> {}
            case MODERATOR -> {
                ResearchTeam team = getTeam(memberProfile.getId());
                team.removeModerator(memberProfile.getId());
                PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.getId(), true));
            }
            case MEMBER -> {
            }
        }
        Researchd.LOGGER.debug("Demoted member {}", memberProfile.getName());
    }

}
