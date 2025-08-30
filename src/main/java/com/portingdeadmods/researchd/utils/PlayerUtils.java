package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class PlayerUtils {
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static Player getPlayerFromName(Level level, String name) {
        return level.getServer().getPlayerList().getPlayerByName(name);
    }

    public static UUID getPlayerUUIDFromName(Level level, String name) {
        return getPlayerFromName(level, name).getUUID();
    }

    public static String getPlayerNameFromUUID(Level level, UUID uuid) {
        return uuid.equals(ResearchTeam.DEBUG_MEMBER.getId()) ? ResearchTeam.DEBUG_MEMBER.getName() : level.getPlayerByUUID(uuid).getName().getString();
    }

}
