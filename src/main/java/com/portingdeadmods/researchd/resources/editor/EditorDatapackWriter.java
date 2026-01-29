package com.portingdeadmods.researchd.resources.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import com.portingdeadmods.researchd.utils.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EditorDatapackWriter implements DatapackWriter {
    private final Map<ResourceLocation, ResearchdDatagenProvider<?>> providers;

    public EditorDatapackWriter() {
        this.providers = new HashMap<>();
    }

    public <P extends ResearchdDatagenProvider<?>> P getOrAddProvider(P provider) {
        return (P) this.providers.computeIfAbsent(provider.registry().location(), key -> provider);
    }

    @Override
    public Result<Path, Exception> write(Path datapackDirectory, String packDescription, String namespace) {
        tryCreateDirectory(datapackDirectory);

        Path packFile = datapackDirectory.resolve("pack.mcmeta");
        tryWriteFile(packFile, ResourceUtils.getPackFile(packDescription, PackType.SERVER_DATA));

        Path dataDir = datapackDirectory.resolve("data");
        tryCreateDirectory(dataDir);

        Path packContentRootDir = dataDir.resolve(namespace);

        Path packResearchdRegistriesDir = packContentRootDir.resolve("researchd");
        tryCreateDirectory(packResearchdRegistriesDir);

        for (ResearchdDatagenProvider<?> provider : this.providers.values()) {
            provider.build();
            Path path = packResearchdRegistriesDir.resolve(provider.registry().location().getPath());
            tryCreateDirectory(path);
            Result<Unit, Exception> result = provider.write(path);
            if (result instanceof Result.Err<Unit, Exception>(Exception error)) {
                return Result.err(error);
            }
        }

        return Result.ok(datapackDirectory);
    }

    private static boolean tryCreateDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
                return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    private static boolean tryWriteFile(Path path, String contents) {
        try {
            Files.writeString(path, contents);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
