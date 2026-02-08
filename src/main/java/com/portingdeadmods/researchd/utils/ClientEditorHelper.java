package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.editor.EditModeSettings;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class ClientEditorHelper {
    public static EditModeSettings getEditModeSettings() {
        return Minecraft.getInstance().player.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
    }

    public static @Nullable ResourceKey<ResearchPack> getDefaultResearchPack() {
        return ResearchHelperClient.getResearchPacks().keySet().stream().findFirst().orElse(null);
    }

    public static Result<PrettyPath, Exception> createResourcePack(String name, String description, String namespace, boolean generateExamples) {
        Path resourcePackDir = Minecraft.getInstance().getResourcePackDirectory();
        Path exampleResourcePackDir = resourcePackDir.resolve(name);
        try {
            if (Files.notExists(exampleResourcePackDir)) {
                Files.createDirectories(exampleResourcePackDir);
                Path packFile = exampleResourcePackDir.resolve("pack.mcmeta");
                Files.writeString(packFile, ResourceUtils.getPackFile(description, PackType.CLIENT_RESOURCES));

                Path dataDir = exampleResourcePackDir.resolve("data");
                Files.createDirectory(dataDir);

                if (generateExamples) {
                    Path packContentRootDir = dataDir.resolve(namespace);

                    Path packResearchdRegistriesDir = packContentRootDir.resolve("researchd");
                    Files.createDirectories(packResearchdRegistriesDir);

                    Path packResearchesDir = packResearchdRegistriesDir.resolve("research");
                    Files.createDirectory(packResearchesDir);

                   // createResearches(packResearchesDir);

                    Path packResearchPacksDir = packResearchdRegistriesDir.resolve("research_pack");
                    Files.createDirectory(packResearchPacksDir);

                    //createResearchPacks(packResearchPacksDir);
                }
                return Result.ok(new PrettyPath(exampleResourcePackDir, Path.of("..." + exampleResourcePackDir.toString().substring(resourcePackDir.toString().length() - "resourcepacks".length() - 1))));
            }
            return Result.err("Example Resource Pack already exists");
        } catch (IOException e) {
            Researchd.LOGGER.error("Encountered error while creating files and directories for example resourcepack", e);
            return Result.err("File/Directory creation failed");
        }
    }

    public static DisplayImpl createDisplay(EditBox nameEditBox, EditBox descEditBox) {
        Component name = null;
        Component description = null;
        if (!nameEditBox.getValue().isEmpty()) {
            name = Component.literal(nameEditBox.getValue());
        }
        if (!descEditBox.getValue().isEmpty()) {
            description = Component.literal(descEditBox.getValue());
        }
        return new DisplayImpl(Optional.ofNullable(name), Optional.ofNullable(description));
    }

    public static Inventory getPlayerInventory() {
        return Minecraft.getInstance().player.getInventory();
    }
}
