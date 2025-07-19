package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.PDLSavedData;
import com.portingdeadmods.researchd.api.data.SavedDataHolder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import com.portingdeadmods.researchd.networking.SyncSavedDataPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperServer;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Researchd.MODID, value = Dist.DEDICATED_SERVER)
public final class ResearchdServerEvents {
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		MinecraftServer server = event.getServer();
		ServerLevel level = server.overworld();

		ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

		if (data != null) {
			for (Map.Entry<UUID, ResearchTeam> entry : data.getResearchTeams().entrySet()) {
				ResearchTeam team = entry.getValue();
				ResearchProgress researchProgress = team.getResearchProgress();

				// v Research Queue Logic v
				ResearchQueue queue = researchProgress.researchQueue();

				if (!queue.isEmpty()) {
					ResearchInstance instance = queue.getEntries().getFirst();
					Research research = ResearchHelperCommon.getResearch(instance.getResearch(), server.registryAccess());

					// TODO: Continue when the ground basis is set n done
				}


				if (level.getGameTime() % 10 == 0) {
					ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
				}
			}
		}
	}

	private static <T> void sendSavedDataSyncPayload(ServerPlayer serverPlayer, ResourceLocation id, PDLSavedData<?> savedData) {
		PDLSavedData<T> savedData1 = (PDLSavedData<T>) savedData;
		T data = savedData1.getData(serverPlayer.serverLevel());
		PacketDistributor.sendToPlayer(serverPlayer, new SyncSavedDataPayload<>(new SavedDataHolder<>(id, savedData1), data));
	}

	@SubscribeEvent
	private static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity().level().isClientSide()) return;
		if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

		MinecraftServer server = serverPlayer.server;
		ServerLevel level = server.overworld();
		ResearchTeamMap researchTeamMap = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
		researchTeamMap.initPlayer(serverPlayer);
		ResearchdSavedData.TEAM_RESEARCH.get().setData(level, researchTeamMap);

		for (Map.Entry<ResourceKey<PDLSavedData<?>>, PDLSavedData<?>> savedData : ResearchdRegistries.SAVED_DATA.entrySet()) {
			PDLSavedData<?> value = savedData.getValue();
			if (value.isSynced()) {
				sendSavedDataSyncPayload(serverPlayer, savedData.getKey().location(), value);
				value.onSyncFunction().accept(serverPlayer);
			}
		}

		// v Research Predicate Attachment v
		ResearchHelperCommon.refreshResearches(serverPlayer);
	}

	@SubscribeEvent
	public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();
		RegistryAccess registryAccess = server.registryAccess();
		HolderLookup.RegistryLookup<ResearchPack> packs = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY);
		Researchd.RESEARCH_PACKS.addAll(packs.listElements().map(Holder.Reference::value).toList());
		Researchd.debug("Researchd Constants Server", "Initialized research packs.", Researchd.RESEARCH_PACKS, "");

		Researchd.RESEARCH_PACK_COUNT.initialize((int) packs.listElements().count());
		Researchd.debug("Researchd Constants Server", "Initialized research pack count: ", Researchd.RESEARCH_PACK_COUNT.get());

		Researchd.RESEARCH_PACK_REGISTRY.initialize(registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY));
		Researchd.debug("Researchd Constants Server", "Initialized research pack registry LazyFinal. ");
	}
}
