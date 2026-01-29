package com.portingdeadmods.researchd.resources.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;

import java.nio.file.Path;

public interface DatapackWriter {
    Result<Path, Exception> write(Path path, String packDescription, String namespace);

    default Result<Path, Exception> write(Path path, String packName, String packDescription, String namespace) {
        return this.write(path.resolve(packName), packDescription, namespace);
    }
}
