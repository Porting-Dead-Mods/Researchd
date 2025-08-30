package com.portingdeadmods.researchd;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ResearchdClient.MODID, dist = Dist.DEDICATED_SERVER)
public class ResearchdServer {
	public static final String MODID = "researchd";
	public static final String MODNAME = "Researchd";

	public ResearchdServer(IEventBus eventBus, ModContainer modContainer) {

	}
}
