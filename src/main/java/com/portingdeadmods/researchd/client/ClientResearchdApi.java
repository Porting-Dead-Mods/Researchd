package com.portingdeadmods.researchd.client;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;

public class ClientResearchdApi {
    public static void openResearchScreen() {
        Minecraft.getInstance().setScreen(new ResearchScreen());
    }

    public static void openTeamScreen() {
        Minecraft.getInstance().setScreen(new ResearchTeamScreen());
    }

    public static void openScreenForResearch(ResourceKey<Research> research) {
        ResearchScreen screen = new ResearchScreen();
        Minecraft.getInstance().setScreen(screen);
        screen.setSelectedResearch(research);
    }
}
