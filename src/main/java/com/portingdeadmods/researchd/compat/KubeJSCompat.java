package com.portingdeadmods.researchd.compat;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchCompletedKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchProgressKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchdEvents;
import com.portingdeadmods.researchd.utils.Result;
import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class KubeJSCompat {
    private static final String KUBEJS_MOD_ID = "kubejs";
    private static boolean kubeJSLoaded = false;
    private static boolean checked = false;

    public static boolean isKubeJSLoaded() {
        if (!checked) {
            kubeJSLoaded = ModList.get().isLoaded(KUBEJS_MOD_ID);
            checked = true;
        }
        return kubeJSLoaded;
    }

    public static final String EXAMPLE_CODE = """
            console.log("Researches :3")
            """;

    /**
     * @return The path the file was created or Exception if it failed
     */
    public static Result<Path, Exception> createExample() {
        Path directory = KubeJSPaths.SERVER_SCRIPTS;
        try {
            if (Files.exists(directory)) {
                Path exampleFile = directory.resolve("research_examples.js");
                if (Files.notExists(exampleFile)) {
                    Files.writeString(exampleFile, EXAMPLE_CODE);
                    return Result.ok(exampleFile);
                } else {
                    return Result.err(new Exception("File already exists"));
                }
            } else {
                return Result.err(new Exception("KubeJS server_scripts directory doesn't exist"));
            }
        } catch (IOException e) {
            Researchd.LOGGER.error("Failed to create KubeJS Examples", e);
            return Result.err(new Exception("File creation failed"));
        }
    }

    public static Path getServerScriptsDir() {
        return KubeJSPaths.SERVER_SCRIPTS;
    }

    public static void fireResearchCompletedEvent(ServerPlayer player, ResourceKey<Research> research) {
        if (isKubeJSLoaded()) {
            try {
                KubeJSEventHandler.fireResearchCompleted(player, research);
            } catch (Exception e) {
                Researchd.LOGGER.error("Failed to fire KubeJS research completed event", e);
            }
        }
    }

    public static void fireResearchProgressEvent(ServerPlayer player, ResourceKey<com.portingdeadmods.researchd.api.research.Research> research, double progress) {
        if (isKubeJSLoaded()) {
            try {
                KubeJSEventHandler.fireResearchProgress(player, research, progress);
            } catch (Exception e) {
                Researchd.LOGGER.error("Failed to fire KubeJS research progress event", e);
            }
        }
    }

    private static class KubeJSEventHandler {
        static void fireResearchCompleted(ServerPlayer player, ResourceKey<Research> research) {
            ResearchdEvents.RESEARCH_COMPLETED.post(
                new ResearchCompletedKubeEvent(player, research)
            );
        }

        static void fireResearchProgress(ServerPlayer player, ResourceKey<Research> research, double progress) {
            ResearchdEvents.RESEARCH_PROGRESS.post(
                new ResearchProgressKubeEvent(player, research, progress)
            );
        }
    }
}