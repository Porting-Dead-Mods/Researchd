package com.portingdeadmods.researchd.client.utils;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import com.portingdeadmods.researchd.networking.team.*;
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

    public static List<GameProfile> getTeamMembers() {
        Minecraft mc = Minecraft.getInstance();
        ResearchTeam researchTeam = getTeam();

        List<GameProfile> players;
        if (!mc.isSingleplayer()) {
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

    public static List<GameProfile> getPlayersNotInTeam() {
        ResearchTeam team = getTeam();
        List<GameProfile> players = ClientPlayerUtils.getPlayers();
        return players.stream().filter(gameProfile -> !team.getMembers().contains(gameProfile.getId())).toList();
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
            Researchd.LOGGER.debug("Remove player {}", memberProfile.getName());
        }
    }

    public static void sendTeamInviteSynced(GameProfile profileToInvite) {
        UUID invited = profileToInvite.getId();
        boolean remove = getTeam().getSentInvites().contains(invited);
        LocalPlayer player = Minecraft.getInstance().player;

        ResearchTeam team = getTeam();
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

    public static void promoteTeamMemberSynced(GameProfile memberProfile) {
        switch (getPlayerRole(memberProfile.getId())) {
            case OWNER -> {
            }
            case MODERATOR -> {
            }
            case MEMBER -> {
                ResearchTeam team = getTeam(memberProfile.getId());
                team.addModerator(memberProfile.getId());
                PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.getId(), false));
            }
        }
        Researchd.LOGGER.debug("Promoted player {}", memberProfile.getName());
    }

    public static void demoteTeamMemberSynced(GameProfile memberProfile) {
        switch (getPlayerRole(memberProfile.getId())) {
            case OWNER -> {
            }
            case MODERATOR -> {
                ResearchTeam team = getTeam(memberProfile.getId());
                team.removeModerator(memberProfile.getId());
                PacketDistributor.sendToServer(new ManageModeratorPayload(memberProfile.getId(), true));
            }
            case MEMBER -> {
            }
        }
        Researchd.LOGGER.debug("Demoted player {}", memberProfile.getName());
    }

    public static void transferOwnershipSynced(GameProfile nextOwner) {
        ResearchTeam team = getTeam();
        team.setOwner(nextOwner.getId());
        PacketDistributor.sendToServer(new TransferOwnershipPayload(nextOwner.getId()));
    }

    public static void resolveInstances(ResearchTeam team) {
        HashMap<ResourceKey<Research>, ResearchInstance> researches = team.getResearchProgress().researches();

        for (Map.Entry<ResourceKey<Research>, ResearchInstance> research : researches.entrySet()) {
            GlobalResearch research1 = CommonResearchCache.GLOBAL_RESEARCHES.get(research.getKey());
            if (research1 != null) {
                research.setValue(research.getValue().withResearch(research1));
            } else {
                Researchd.LOGGER.debug("RESEARCH");
            }
        }

    }
}
