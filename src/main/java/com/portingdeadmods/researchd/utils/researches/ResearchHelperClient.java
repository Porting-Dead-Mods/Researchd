package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.*;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.utils.SpaghettiClient;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class ResearchHelperClient {
    public static void reloadResearches(Level level) {
        ResearchManagerImpl.setNewInstance(level);

        ResearchTeamMap data = (ResearchTeamMap) ResearchdApi.getTeamManager(level);
        if (data != null) {
            for (ResearchTeam team : data.getTeams()) {
                ResearchTeamHelperClient.resolveInstances(team);
            }
        }

        // Update screen-related data

        ResearchHelperClient.initIconRenderers();
        ResearchGraphCache.clearCache();
        ResearchTeamHelperClient.refreshResearchScreenData();

        ResearchScreen screen = SpaghettiClient.tryGetResearchScreen();
        if (screen != null) {
            screen.initDefaultState();
        }
    }

    public static Map<ResourceKey<ResearchPack>, ResearchPack> getResearchPacks() {
        if (Minecraft.getInstance().level == null) {
            return new HashMap<>();
        }
        return ResearchHelperCommon.getResearchPacks(Minecraft.getInstance().level);
    }

    // Called at the end of the initialization phase of the research cache
    private static void initIconRenderers() {
        ResearchManager researchManager = ResearchdApi.getResearchManager();

        ResearchScreen.CLIENT_ICONS.clear();

        researchManager.getResearches().forEach((k) -> {
            Research research = researchManager.lookupResearch(k, Minecraft.getInstance().level);
            if (research == null) return;

            ResearchIcon icon = research.researchIcon();
            Function<ResearchIcon, ClientResearchIcon<?>> factory = ResearchdClient.RESEARCH_ICONS.get(icon.id());
            if (factory == null) {
                Researchd.error(
                        "Research Icons",
                        "Research %s uses icon type %s, which has no client renderer",
                        k.location(),
                        icon.id());
                return;
            }

            ResearchScreen.CLIENT_ICONS.put(k.location(), factory.apply(icon));
        });
    }
}
