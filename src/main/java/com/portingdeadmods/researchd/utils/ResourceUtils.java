package com.portingdeadmods.researchd.utils;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;

public final class ResourceUtils {
    public static String getPackFile(String desc, PackType type) {
        return """
                {
                  "pack": {
                    "description": {
                      "text": "%s"
                    },
                    "pack_format": %d
                  }
                }
                """.formatted(desc, SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
    }
}
