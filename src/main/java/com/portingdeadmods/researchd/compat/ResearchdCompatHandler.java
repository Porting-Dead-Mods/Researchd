package com.portingdeadmods.researchd.compat;

import com.portingdeadmods.researchd.ResearchdConfig;
import net.neoforged.fml.ModList;

public final class ResearchdCompatHandler {
    public static boolean isKubeJSLoaded() {
        return ModList.get().isLoaded("kubejs");
    }

    public static boolean isJeiLoaded() {
        return ModList.get().isLoaded("jei");
    }

	public static boolean isFTBTeamsLoaded() { return ModList.get().isLoaded("ftbteams"); }

	public static boolean isFTBTeamsEnabled() {
		return isFTBTeamsLoaded() && ResearchdConfig.Common.useFTBTeams;
	}
}
