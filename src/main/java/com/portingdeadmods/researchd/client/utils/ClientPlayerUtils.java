package com.portingdeadmods.researchd.client.utils;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	public static List<UUID> getPlayerUUIDs() {
		Minecraft mc = Minecraft.getInstance();

		List<UUID> uuids = new ArrayList<>();
		if (!mc.isSingleplayer()) {
			ServerData serverData = mc.getCurrentServer();
			if (serverData != null)
				if (serverData.players != null)
					mc.getCurrentServer().players.sample().forEach(profile -> {
						uuids.add(profile.getId());
					});
		} else {
			uuids.add(mc.player.getUUID());
		}

		return uuids;
	}

	public static String getPlayerName(UUID uuid) {
		Minecraft mc = Minecraft.getInstance();
		for (GameProfile profile : getPlayers()) {
			if (profile.getId().equals(uuid)) {
				return profile.getName();
			}
		}
		return uuid.toString();
	}
}
