package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

public class ResearchHelperClient {
    public static void refreshResearches(Player player) {
        Level level;
        level = Minecraft.getInstance().level;

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team = researchData.getTeamByMember(player.getUUID());
        TeamResearchProgress progress = team.getResearchProgress();

        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = player.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                player.setData((AttachmentType<ResearchEffectData<?>>) entry.getValue(), effectData.getDefault(level));
            }
        }

        if (progress == null) return;
        for (ResearchInstance res : team.getResearchProgress().researches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                res.lookup(level.registryAccess()).researchEffect().onUnlock(level, player, res.getKey());
            }
        }
    }

    // Called at the end of the initialization phase of the research cache
    public static void initIconRenderers(LevelAccessor level) {
        CommonResearchCache.GLOBAL_RESEARCHES.forEach((k, v) -> {
            ResearchIcon icon = v.getResearch(level.registryAccess()).researchIcon();
            ResearchScreen.CLIENT_ICONS.put(k.location(), ResearchdClient.RESEARCH_ICONS.get(icon.id()).apply(icon));
        });
    }

    public static Research getResearch(ResourceKey<Research> researchKey) {
        return ResearchHelperCommon.getResearch(researchKey, Minecraft.getInstance().level.registryAccess());
    }

}
