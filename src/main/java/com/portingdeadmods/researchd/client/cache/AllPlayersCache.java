package com.portingdeadmods.researchd.client.cache;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO: Move to PDL
public class AllPlayersCache {
    private static final List<UUID> UUIDS = new UniqueArray<>();
    private static final Map<UUID, String> NAMES = new HashMap<>();
    private static final Map<UUID, PlayerSkin> SKINS = new HashMap<>();

    public static void add(UUID uuid, String name, PlayerSkin skin) {
        if (!UUIDS.contains(uuid)) {
            UUIDS.add(uuid);
        }
        NAMES.put(uuid, name);
        SKINS.put(uuid, skin);
    }

    public static String getName(UUID uuid) {
        String name = NAMES.get(uuid);
        if (name == null) name = "!Unknown Player!";
        return name;
    }

    public static PlayerSkin getSkin(UUID uuid) {
        PlayerSkin skin = SKINS.get(uuid);
        if (skin == null) {
            skin = DefaultPlayerSkin.get(uuid);
            Researchd.LOGGER.error("Skin not found in cache for UUID: {}, using default skin.", uuid);
        };
        return skin;
    }

    public static List<UUID> getUUIDs() {
        return UUIDS;
    }

    public static void clearCache() {
        UUIDS.clear();
        NAMES.clear();
        SKINS.clear();
    }
}
