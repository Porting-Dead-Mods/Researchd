package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

}
