package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchPacksPayload;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchesPayload;
import com.portingdeadmods.researchd.networking.research.ResearchCacheReloadPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;

public final class ResearchHelperServer {
    public static void reloadResearches(MinecraftServer server, ServerPlayer player, List<ServerPlayer> relevantPlayers) {
        CommonResearchCache.initialize(server.overworld());

        ServerLevel overworld = server.overworld();
        ResearchTeamMap teamMap = ResearchdSavedData.TEAM_RESEARCH.get().getData(overworld);
        ResearchTeamHelper.resolveGlobalResearches(teamMap);

        // Add new researchPacks to teams in case new ones were added
        // TODO: Remove old researchPacks from teams in cases ones were removed
        ResearchTeamHelper.cleanupTeamResearches(teamMap, overworld);
        ResearchTeamHelper.initializeTeamResearches(teamMap, overworld);
        ResearchdSavedData.TEAM_RESEARCH.get().setData(overworld, teamMap);
        ResearchdSavedData.TEAM_RESEARCH.get().sync(overworld);

        if (player != null) {
            updateReloadableRegistries(player);
            PacketDistributor.sendToPlayer(player, ResearchCacheReloadPayload.INSTANCE);
        } else {
            for (ServerPlayer relevantPlayer : relevantPlayers) {
                updateReloadableRegistries(relevantPlayer);
            }
            for (ServerPlayer relevantPlayer : relevantPlayers) {
                PacketDistributor.sendToPlayer(relevantPlayer, ResearchCacheReloadPayload.INSTANCE);
            }
        }
    }

    private static void updateReloadableRegistries(ServerPlayer p) {
        PacketDistributor.sendToPlayer(p, new UpdateResearchesPayload(new HashMap<>(ResearchdManagers.getResearchesManager(p.level()).getByName())));
        PacketDistributor.sendToPlayer(p, new UpdateResearchPacksPayload(new HashMap<>(ResearchdManagers.getResearchPacksManager(p.level()).getByName())));
    }

}
