package com.portingdeadmods.researchd.compat;

import net.neoforged.fml.ModList;

public class ResearchdCompatHandler {
    public static boolean isKubeJSLoaded() {
        return ModList.get().isLoaded("kubejs");
    }
}
