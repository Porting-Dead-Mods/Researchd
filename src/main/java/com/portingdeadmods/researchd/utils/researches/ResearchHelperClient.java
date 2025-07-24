package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.research.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

public class ResearchHelperClient {
	public static void refreshResearches(LocalPlayer player) {
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

		for (ResearchInstance res : team.getResearchProgress().completedResearches()) {
			ResearchHelperCommon.getResearch(res.getResearch(), level.registryAccess()).researchEffects().forEach(
					eff -> eff.onUnlock(level, player, res.getResearch())
			);
		}
	}
}
