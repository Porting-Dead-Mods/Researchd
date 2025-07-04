package com.portingdeadmods.researchd.client.utils;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayerUtils {
    public static List<GameProfile> getPlayers() {
        Minecraft mc = Minecraft.getInstance();

        List<GameProfile> players;
        if (!mc.isSingleplayer()){
            players = mc.getCurrentServer().players.sample();
        } else {
            players = new ArrayList<>();
            players.add(mc.player.getGameProfile());
            players.add(ResearchTeam.DEBUG_MEMBER);
        }

        return players;
    }
}
