package com.portingdeadmods.researchd.client.utils;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import com.portingdeadmods.researchd.networking.team.TeamSetNamePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientResearchTeamHelper {
    private final ResearchTeam clientTeam;

    public ClientResearchTeamHelper() {
        LocalPlayer player = Minecraft.getInstance().player;
        this.clientTeam = ResearchTeamHelper.getResearchTeam(player);
    }

    public void setTeamNameSynced(String name) {
        if (!this.clientTeam.getName().equals(name)) {
            this.clientTeam.setName(name);
            PacketDistributor.sendToServer(new TeamSetNamePayload(name));
        }
    }

    public ResearchTeamRole getPlayerRole(UUID uuid) {
        if (this.clientTeam.isOwner(uuid)) {
            return ResearchTeamRole.OWNER;
        } else if (this.clientTeam.isModerator(uuid)) {
            return ResearchTeamRole.MODERATOR;
        }
        return ResearchTeamRole.MEMBER;
    }

    public List<GameProfile> getPlayers() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(Objects.requireNonNull(player));

        List<GameProfile> players;
        if (!mc.isSingleplayer()){
            players = mc.getCurrentServer().players.sample();
        } else {
            players = List.of(mc.player.getGameProfile());
        }
        return researchTeam.getMembers().stream().map(uuid -> {
            for (GameProfile profile : players) {
                if (profile.getId().equals(uuid)) {
                    return profile;
                }
            }
            return null;
        }).toList();
    }

}
