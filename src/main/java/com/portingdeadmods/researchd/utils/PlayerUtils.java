package com.portingdeadmods.researchd.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

//TODO: Put this in PDL
public class PlayerUtils {
    public static final UUID EmptyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static Player getPlayerFromName(Level level, String name) {
        return level.getServer().getPlayerList().getPlayerByName(name);
    }

    public static UUID getPlayerUUIDFromName(Level level, String name) {
        return getPlayerFromName(level, name).getUUID();
    }

    public static String getPlayerNameFromUUID(Level level, UUID uuid) {
        return level.getPlayerByUUID(uuid).getName().getString();
    }
}
