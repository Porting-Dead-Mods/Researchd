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

	}
}
