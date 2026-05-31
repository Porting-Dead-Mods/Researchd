package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.*;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.utils.SpaghettiClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ResearchHelperClient {
    public static void reloadResearches(Level level) {
        ResearchManagerImpl.setNewInstance(level);

        ResearchTeamMap data = (ResearchTeamMap) ResearchdApi.getTeamManager(level);
        if (data != null) {
            for (ResearchTeam team : data.getTeams()) {
                ClientResearchTeamHelper.resolveInstances(team);
            }
        }

        // Update screen-related data

        ResearchHelperClient.initIconRenderers();
        ResearchGraphCache.clearCache();
        ClientResearchTeamHelper.refreshResearchScreenData();

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

        researchManager.getResearches().forEach((k) -> {
            ResearchIcon icon = researchManager.lookupResearch(k, Minecraft.getInstance().level).researchIcon();
            ResearchScreen.CLIENT_ICONS.put(k.location(), ResearchdClient.RESEARCH_ICONS.get(icon.id()).apply(icon));
        });
    }

}
