package com.portingdeadmods.researchd.client.utils;

import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import com.portingdeadmods.researchd.networking.team.TeamSetNamePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

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

    public ResearchTeamRole getPlayerRole() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (this.clientTeam.isOwner(player.getUUID())) {
            return ResearchTeamRole.OWNER;
        } else if (this.clientTeam.isModerator(player.getUUID())) {
            return ResearchTeamRole.MODERATOR;
        }
        return ResearchTeamRole.MEMBER;
    }
}
