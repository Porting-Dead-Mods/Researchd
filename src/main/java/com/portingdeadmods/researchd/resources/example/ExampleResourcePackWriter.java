package com.portingdeadmods.researchd.resources.example;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.resources.PackWriter;
import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExampleResourcePackWriter implements PackWriter {
    private boolean generateExamples;

    public ExampleResourcePackWriter(boolean generateExamples) {
        this.generateExamples = generateExamples;
    }

    public ExampleResourcePackWriter() {
        this(false);
    }

    public void setGenerateExamples(boolean generateExamples) {
        this.generateExamples = generateExamples;
    }

    @Override
    public Result<Path, Exception> write(Path resourcePackDir, String packDescription, String namespace) {
        try {
            if (Files.notExists(resourcePackDir)) {
                Files.createDirectories(resourcePackDir);
                Path packFile = resourcePackDir.resolve("pack.mcmeta");
                Files.writeString(packFile, PackWriter.getPackFile(packDescription, PackType.CLIENT_RESOURCES));

                Path assetsDir = resourcePackDir.resolve("assets");
                Files.createDirectory(assetsDir);

                if (generateExamples) {
                    Path packContentRootDir = assetsDir.resolve(namespace);

                    Path packResearchdAssetsDir = packContentRootDir.resolve("researchd");
                    Files.createDirectories(packResearchdAssetsDir);
                }
                return Result.ok(resourcePackDir);
            }
            return Result.err("Example Resource Pack already exists");
        } catch (IOException e) {
            Researchd.LOGGER.error("Encountered error while creating files and directories for example resourcepack", e);
            return Result.err("File/Directory creation failed");
        }
    }
}
