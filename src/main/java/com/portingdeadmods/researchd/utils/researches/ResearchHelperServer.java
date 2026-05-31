package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchPacksPayload;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchesPayload;
import com.portingdeadmods.researchd.networking.research.ResearchReloadPayload;
import com.portingdeadmods.researchd.networking.team.manager.SyncTeamDataPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;

public final class ResearchHelperServer {
    public static void onReloadResearches(MinecraftServer server, ServerPlayer player, List<ServerPlayer> relevantPlayers) {
        // Initialize new research manager for reloaded researches and relations
        ResearchManagerImpl.setNewInstance(server.overworld());

        ServerLevel overworld = server.overworld();
        ResearchTeamMap teamMap = TeamSavedData.getData(overworld);
        // FIXME: Reenable this if its causing issues, otherwise remove it
        //ResearchTeamHelper.resolveGlobalResearches(teamMap);

        // Add new researces to teams in case new ones were added
        // TODO: Remove old researches from teams in cases ones were removed
        ResearchTeamHelper.cleanupTeamResearches(teamMap, overworld);
        ResearchTeamHelper.initializeTeamResearches(teamMap, overworld);
        teamMap.setChanged();
        PacketDistributor.sendToAllPlayers(new SyncTeamDataPayload(teamMap));

        if (player != null) {
            syncReloadableRegistries(player);
            PacketDistributor.sendToPlayer(player, ResearchReloadPayload.INSTANCE);
        } else {
            for (ServerPlayer relevantPlayer : relevantPlayers) {
                syncReloadableRegistries(relevantPlayer);
            }
            for (ServerPlayer relevantPlayer : relevantPlayers) {
                PacketDistributor.sendToPlayer(relevantPlayer, ResearchReloadPayload.INSTANCE);
            }
        }
    }

    public static void syncReloadableRegistries(ServerPlayer p) {
        PacketDistributor.sendToPlayer(p, new UpdateResearchesPayload(new HashMap<>(ResearchdManagers.getResearchesManager(p.level()).getByName())));
        PacketDistributor.sendToPlayer(p, new UpdateResearchPacksPayload(new HashMap<>(ResearchdManagers.getResearchPacksManager(p.level()).getByName())));
    }

}
