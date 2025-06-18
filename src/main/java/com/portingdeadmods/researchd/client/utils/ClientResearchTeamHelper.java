package com.portingdeadmods.researchd.client.utils;

import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
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
        this.clientTeam.setName(name);
        PacketDistributor.sendToServer(new TeamSetNamePayload(name));
    }
}
