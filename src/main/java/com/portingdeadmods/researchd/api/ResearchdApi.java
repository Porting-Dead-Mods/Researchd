package com.portingdeadmods.researchd.api;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.client.ClientResearchdApi;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
    public static @Nullable ResearchTeamManager getTeamManager(Level level) {
        if (!level.isClientSide()) {
            return TeamSavedData.getData((ServerLevel) level);
        }
        return ResearchTeamCache.researchTeamMap;
    }

    /* Research Api */
    public static @Nullable ResearchManager getResearchManager() {
        return ResearchManagerImpl.getInstance();
    }

    public static ResearchEffectManager getResearchEffectManager(Level level) {
        if (!level.isClientSide()) {
            return TeamResearchEffectSavedData.getData((ServerLevel) level);
        }
        return ResearchTeamCache.teamResearchEffectDataMap;
    }

}
