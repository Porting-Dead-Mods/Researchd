package com.portingdeadmods.researchd.events.common;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.research.ResearchProgressSyncPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdServerTickHandler {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap teamMap = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (teamMap != null) {
            if (level.getGameTime() % 20 == 0) {
                // Logic here please
                handleResearchMethods(level, teamMap);
            }

            for (ResearchTeam team : teamMap.researchTeams().values()) {
                ResourceKey<Research> currentResearchKey = team.getCurrentResearch();

                if (currentResearchKey != null) {
                    ResearchProgress currentResearchProgress = team.getCurrentProgress();

                    if (currentResearchProgress != null) {
                        if (level.getGameTime() % 4 == 0) {
                            for (TeamMember memberUUID : team.getMembers()) {
                                ServerPlayer player = server.getPlayerList().getPlayer(memberUUID.player());
                                if (player == null) continue;

                                PacketDistributor.sendToPlayer(player, new ResearchProgressSyncPayload(currentResearchKey, currentResearchProgress));
                            }
                        }

                        // Research Complete Logic
                        if (currentResearchProgress.isComplete()) {
                            long completionTime = server.overworld().getDayTime() * 50L;
                            team.setResearchCompleted(currentResearchKey, completionTime);
                            team.onCompleteResearch(currentResearchKey, completionTime, server.getPlayerList()::getPlayer);

                            team.getQueue().remove(0, false);
                        }
                    }

                    // Save and sync the whole team researchPack teamMap every 20 ticks
                    if (level.getGameTime() % 20 == 0) {
                        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, teamMap);
                    }
                }
            }
        }
    }

    public static void handleResearchMethods(Level level, ResearchTeamMap teamMap) {
        for (ResearchTeam team : teamMap.researchTeams().values()) {
            ResourceKey<Research> firstInQueue = team.getQueue().getFirst();
            if (firstInQueue == null) continue;

            ResearchProgress rp = team.getResearchProgresses().get(firstInQueue);
            if (rp != null) {
                rp.checkProgress(firstInQueue, level, new ResearchMethod.SimpleMethodContext(team, null));
            }
        }
    }
}
