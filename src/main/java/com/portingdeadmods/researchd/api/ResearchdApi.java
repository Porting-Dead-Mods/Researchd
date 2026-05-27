package com.portingdeadmods.researchd.api;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.client.ClientResearchdApi;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public final class ResearchdApi {
    /* Research Screen Api */
    public static void openScreen() {
        if (FMLEnvironment.dist.isClient()) {
            ClientResearchdApi.openResearchScreen();
        }
    }

    public static void openTeamScreen() {
        if (FMLEnvironment.dist.isClient()) {
            ClientResearchdApi.openTeamScreen();
        }
    }

    public static void openScreenForResearch(ResourceKey<Research> research) {
        if (FMLEnvironment.dist.isClient()) {
            ClientResearchdApi.openScreenForResearch(research);
        }
    }

    /* Research Team Api */
    // FIXME: Use currentServer#overworld and client level?
    public static ResearchTeamManager getTeamManager(Level level) {
        return ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
    }

}
