package com.portingdeadmods.researchd.compat;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchCompletedKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchProgressKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchdEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

public class KubeJSIntegration {
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