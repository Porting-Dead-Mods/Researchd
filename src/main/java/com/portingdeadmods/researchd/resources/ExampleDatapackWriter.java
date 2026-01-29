package com.portingdeadmods.researchd.resources;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPacks;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearches;
import com.portingdeadmods.researchd.resources.editor.DatapackWriter;
import com.portingdeadmods.researchd.utils.ResourceUtils;
import com.portingdeadmods.researchd.utils.researches.ReloadableRegistryManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/*
 * Helpers to create the example datapack that can be created using a command
 */
public class ExampleDatapackWriter implements DatapackWriter {
    private boolean generateExamples;

    public ExampleDatapackWriter(boolean generateExamples) {
        this.generateExamples = generateExamples;
    }

    public ExampleDatapackWriter() {
        this(false);
    }

    public void setGenerateExamples(boolean generateExamples) {
        this.generateExamples = generateExamples;
    }

    @Override
    public Result<Path, Exception> write(Path datapackDir, String packDescription, String namespace) {
        try {
            if (Files.notExists(datapackDir)) {
                Files.createDirectories(datapackDir);
                Path packFile = datapackDir.resolve("pack.mcmeta");
                Files.writeString(packFile, ResourceUtils.getPackFile(packDescription, PackType.SERVER_DATA));

                Path dataDir = datapackDir.resolve("data");
                Files.createDirectory(dataDir);

                if (this.generateExamples) {
                    Path packContentRootDir = dataDir.resolve(namespace);

                    Path packResearchdRegistriesDir = packContentRootDir.resolve("researchd");
                    Files.createDirectories(packResearchdRegistriesDir);

                    Path packResearchesDir = packResearchdRegistriesDir.resolve("research");
                    Files.createDirectory(packResearchesDir);

                    createResearches(packResearchesDir);

                    Path packResearchPacksDir = packResearchdRegistriesDir.resolve("research_pack");
                    Files.createDirectory(packResearchPacksDir);

                    createResearchPacks(packResearchPacksDir);
                }
                return Result.ok(datapackDir);
            }
            return Result.err("Example Datapack already exists");
        } catch (IOException e) {
            Researchd.LOGGER.error("Encountered error while creating files and directories for example datapack", e);
            return Result.err("File/Directory creation failed");
        }
    }

    private static void createResearches(Path researchDir) {
        ResearchdResearches researches = new ResearchdResearches("rd_examples");
        researches.buildExampleDatapack();

        for (Map.Entry<ResourceKey<Research>, Research> entry : researches.contents().entrySet()) {
            Codec<Research> codec = Research.CODEC;
            writeToFile(researchDir, codec.encodeStart(JsonOps.INSTANCE, entry.getValue()), entry.getKey().location());
        }

    }

    private static void createResearchPacks(Path researchDir) {
        ResearchdResearchPacks packs = new ResearchdResearchPacks("rd_examples");
        packs.buildExampleDatapack();

        for (Map.Entry<ResourceKey<ResearchPack>, ResearchPack> entry : packs.contents().entrySet()) {
            Codec<ResearchPack> codec = ResearchPack.CODEC;
            writeToFile(researchDir, codec.encodeStart(JsonOps.INSTANCE, entry.getValue()), entry.getKey().location());
        }

    }

    private static void writeToFile(Path researchDir, DataResult<JsonElement> result, ResourceLocation location) {
        result.ifSuccess(json -> {
            try (FileWriter writer = new FileWriter(researchDir.resolve(location.getPath() + ".json").toFile())) {
                ReloadableRegistryManager.GSON.toJson(json, writer);
            } catch (IOException e) {
                Researchd.LOGGER.error("Failed to write json to file", e);
            }
        });
    }
}
