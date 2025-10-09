package com.portingdeadmods.researchd.compat;

import net.neoforged.fml.ModList;

public final class ResearchdCompatHandler {
    public static boolean isKubeJSLoaded() {
        return ModList.get().isLoaded("kubejs");
    }

    public static boolean isJeiLoaded() {
        return ModList.get().isLoaded("jei");
    }

}
