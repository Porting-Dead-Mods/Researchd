package com.portingdeadmods.researchd.resources;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;

import java.nio.file.Path;

public interface PackWriter {
    Result<Path, Exception> write(Path path, String packDescription, String namespace);

    default Result<Path, Exception> write(Path path, String packName, String packDescription, String namespace) {
        return this.write(path.resolve(packName), packDescription, namespace);
    }

    static String getPackFile(String desc, PackType type) {
        return """
                {
                  "pack": {
                    "description": {
                      "text": "%s"
                    },
                    "pack_format": %d
                  }
                }
                """.formatted(desc, SharedConstants.getCurrentVersion().getPackVersion(type));
    }
}
