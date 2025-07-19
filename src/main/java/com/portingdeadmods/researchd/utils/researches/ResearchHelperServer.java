package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.research.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

public class ResearchHelperServer {
	public static void refreshResearches(ServerPlayer player) {
		ServerLevel level;
		MinecraftServer server = player.server;
		level = server.overworld();

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
