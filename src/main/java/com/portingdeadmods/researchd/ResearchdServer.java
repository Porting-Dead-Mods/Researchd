package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.impl.research.ResearchPack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(value = ResearchdClient.MODID, dist = Dist.DEDICATED_SERVER)
public class ResearchdServer {
	public static final String MODID = "researchd";
	public static final String MODNAME = "Researchd";

	public ResearchdServer(IEventBus eventBus, ModContainer modContainer) {
		eventBus.addListener(this::onServerAboutToStart);
	}

	// Research Pack Syncing - Server Side
	public void onServerAboutToStart(ServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();
		RegistryAccess registryAccess = server.registryAccess();
		HolderLookup.RegistryLookup<ResearchPack> packs = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY);
		Researchd.RESEARCH_PACKS.addAll(packs.listElements().map(Holder.Reference::value).toList());
		Researchd.RESEARCH_PACK_COUNT.initialize((int) packs.listElements().count());
		Researchd.RESEARCH_PACK_REGISTRY.initialize(registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY));
	}
}
