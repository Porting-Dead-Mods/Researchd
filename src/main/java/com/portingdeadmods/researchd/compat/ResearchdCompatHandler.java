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
		return isFTBTeamsLoaded() && ResearchdConfig.Server.useFTBTeams;
	}

    public static boolean isEmiLoaded() {
        return ModList.get().isLoaded("emi");
    }

    public static boolean isIELoaded() {
        return ModList.get().isLoaded("immersiveengineering");
    }

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded("create");
    }
}
