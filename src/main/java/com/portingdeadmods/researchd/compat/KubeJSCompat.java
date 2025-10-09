package com.portingdeadmods.researchd.compat;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.compat.kubejs.ResearchdKJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/*
 * Directly called in mod code, do not import KubeJS classes
 */
public final class KubeJSCompat {
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
            ResearchdEvents.registerResearchPacks(event => {
                event.create('kubejs:example_pack')
                    .colorRGB(255, 51, 51)
                    .sortingValue(1); // Progression/Sorting order. Higher -> Later in game
            });

            ResearchdEvents.registerResearches(event => {
                event.create('kubejs:start_research')
                    .icon('minecraft:book')
                    .consumeItem('minecraft:dirt', 1)
                    .effect(ResearchEffectHelper.empty())
                    .literalName('Getting Started')
                    .literalDescription('A simple starting research.')
                    .noParentRequired();
                
                event.create('kubejs:automation_research')
                    .icon('minecraft:iron_ingot')
                    .parent('kubejs:start_research')
                    .consumePack('kubejs:example_pack', 10, 5)
                    .effect(ResearchEffectHelper.unlockRecipe('minecraft:hopper'))
                    .literalName('Basic Automation')
                    .literalDescription('Learn the fundamentals of automated production.');
            });
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
                ResearchdKJSEvents.fireResearchCompleted(player, research);
            } catch (Exception e) {
                Researchd.LOGGER.error("Failed to fire KubeJS research completed event", e);
            }
        }
    }

    public static void fireResearchProgressEvent(ServerPlayer player, ResourceKey<com.portingdeadmods.researchd.api.research.Research> research, double progress) {
        if (isKubeJSLoaded()) {
            try {
                ResearchdKJSEvents.fireResearchProgress(player, research, progress);
            } catch (Exception e) {
                Researchd.LOGGER.error("Failed to fire KubeJS research progress event", e);
            }
        }
    }

    public static Map<ResourceLocation, Research> getKubeJSResearches() {
        if (!isKubeJSLoaded()) {
            return Map.of();
        }
        try {
            return ResearchdKJSEvents.fireRegisterResearchesEvent();
        } catch (Exception e) {
            Researchd.LOGGER.error("Failed to get KubeJS researches", e);
            return Map.of();
        }
    }

    public static Map<ResourceLocation, ResearchPack> getKubeJSResearchPacks() {
        if (!isKubeJSLoaded()) {
            return Map.of();
        }
        try {
            return ResearchdKJSEvents.fireRegisterResearchPacksEvent();
        } catch (Exception e) {
            Researchd.LOGGER.error("Failed to get KubeJS research packs", e);
            return Map.of();
        }
    }

    private static class KubeJSEventHandler {
    }



}