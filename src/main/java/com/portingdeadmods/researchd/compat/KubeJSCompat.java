package com.portingdeadmods.researchd.compat;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.compat.kubejs.ResearchdKJSEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

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