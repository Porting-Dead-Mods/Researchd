package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.impl.research.ResearchPack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public final class ResearchdEvents {
	@EventBusSubscriber(modid = Researchd.MODID, bus = EventBusSubscriber.Bus.GAME)
	public static class Game {
		// Research Pack Syncing - Client Side
		@SubscribeEvent
		public static void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
			LocalPlayer player = event.getPlayer();
			RegistryAccess registryAccess = player.registryAccess();
			HolderLookup.RegistryLookup<ResearchPack> packs = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY);
			Researchd.RESEARCH_PACKS.addAll(packs.listElements().map(Holder.Reference::value).toList());
			Researchd.RESEARCH_PACK_COUNT.initialize((int) packs.listElements().count());
			Researchd.RESEARCH_PACK_REGISTRY.initialize(registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY));
		}
	}
}
