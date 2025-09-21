package com.portingdeadmods.researchd.client.utils;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayerUtils {
    public static List<GameProfile> getPlayers() {
        Minecraft mc = Minecraft.getInstance();

        List<GameProfile> players;
	    if (!mc.isSingleplayer()) {
		    ServerData serverData = mc.getCurrentServer();
		    if (serverData != null)
			    if (serverData.players != null)
				    players = mc.getCurrentServer().players.sample();
			    else
				    players = new ArrayList<>();
		    else
			    players = new ArrayList<>();
	    } else {
		    players = new ArrayList<>();
		    players.add(mc.player.getGameProfile());
		    players.add(ResearchTeam.DEBUG_MEMBER);
	    }

        return players;
    }
}
