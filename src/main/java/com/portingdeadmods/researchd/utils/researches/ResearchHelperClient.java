package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ResearchHelperClient {
    public static void reloadResearches(Level level) {
        CommonResearchCache.initialize(level);

        ResearchHelperClient.initIconRenderers(level);
        ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        if (data != null) {
            for (SimpleResearchTeam team : data.researchTeams().values()) {
                ClientResearchTeamHelper.resolveInstances(team);
            }
        }
        ResearchGraphCache.clearCache();
        ClientResearchTeamHelper.refreshResearchScreenData();
        ResearchScreen screen = Spaghetti.tryGetResearchScreen();
        if (CommonResearchCache.rootResearch != null && screen != null) {
            screen.getResearchGraphWidget().setGraph(ResearchGraphCache.computeIfAbsent(CommonResearchCache.rootResearch.getResearchKey()));
        }
    }

    public static void refreshResearches(Player player) {
        Level level;
        level = Minecraft.getInstance().level;

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        ResearchTeam team = researchData.getTeamByMember(player.getUUID());

        for (Supplier<? extends AttachmentType<? extends ResearchEffectData<?>>> entry : Researchd.RESEARCH_EFFECT_DATA_TYPES) {
			AttachmentType<ResearchEffectData<?>> attachment = (AttachmentType<ResearchEffectData<?>>) entry.get();
	        ResearchEffectData<?> data = player.getData(attachment);
			player.setData(attachment, data.getDefault(level));
        }

        for (ResearchInstance res : team.getResearches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                ResearchEffect effect = res.lookup(level).researchEffect();
                effect.onUnlock(level, player, res.getKey());
            }
        }

    }

    public static Map<ResourceKey<ResearchPack>, ResearchPack> getResearchPacks() {
        if (Minecraft.getInstance().level == null) {
            return new HashMap<>();
        }
        return ResearchHelperCommon.getResearchPacks(Minecraft.getInstance().level);
    }

    // Called at the end of the initialization phase of the research cache
    public static void initIconRenderers(Level level) {
        CommonResearchCache.globalResearches.forEach((k, v) -> {
            ResearchIcon icon = v.getResearch(level).researchIcon();
            ResearchScreen.CLIENT_ICONS.put(k.location(), ResearchdClient.RESEARCH_ICONS.get(icon.id()).apply(icon));
        });
    }

    public static Research getResearch(ResourceKey<Research> researchKey) {
        return ResearchHelperCommon.getResearch(researchKey, Minecraft.getInstance().level);
    }

}
