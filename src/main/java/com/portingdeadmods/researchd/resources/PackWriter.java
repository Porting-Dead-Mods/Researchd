package com.portingdeadmods.researchd.resources;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import java.nio.file.Path;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;

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
                    "min_format": %2$d,
                    "max_format": %2$d
                  }
                }
                """
                .formatted(
                        desc,
                        SharedConstants.getCurrentVersion().packVersion(type).major());
    }
}
