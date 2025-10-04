package com.portingdeadmods.researchd.utils.researches;

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
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

public final class ResearchHelperClient {
    public static void refreshResearches(Player player) {
        Level level;
        level = Minecraft.getInstance().level;

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team = researchData.getTeamByMember(player.getUUID());

        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = player.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                player.setData((AttachmentType<ResearchEffectData<?>>) entry.getValue(), effectData.getDefault(level));
            }
        }

        for (ResearchInstance res : team.getResearches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                ResearchEffect effect = res.lookup(level).researchEffect();
                effect.onUnlock(level, player, res.getKey());
            }
        }

    }

    public static Map<ResourceKey<ResearchPack>, ResearchPack> getResearchPacks() {
        return ResearchHelperCommon.getResearchPacks(Minecraft.getInstance().level);
    }

    // Called at the end of the initialization phase of the research cache
    public static void initIconRenderers(Level level) {
        CommonResearchCache.GLOBAL_RESEARCHES.forEach((k, v) -> {
            ResearchIcon icon = v.getResearch(level).researchIcon();
            ResearchScreen.CLIENT_ICONS.put(k.location(), ResearchdClient.RESEARCH_ICONS.get(icon.id()).apply(icon));
        });
    }

    public static Research getResearch(ResourceKey<Research> researchKey) {
        return ResearchHelperCommon.getResearch(researchKey, Minecraft.getInstance().level);
    }

}
