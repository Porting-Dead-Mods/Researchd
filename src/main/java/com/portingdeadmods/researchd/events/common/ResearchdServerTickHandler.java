package com.portingdeadmods.researchd.events.common;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
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

import java.util.UUID;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdServerTickHandler {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap teamMap = TeamSavedData.getData(level);

        if (teamMap != null) {
            for (ResearchTeam team : teamMap.getTeams()) {
                ResourceKey<Research> currentResearch = team.getCurrentResearch();

                if (currentResearch != null) {
                    ResearchProgress currentProgress = team.getCurrentProgress();

                    // Check progress of first research in queue
                    if (currentProgress != null) {
                        float oldProgress = currentProgress.getProgress();
                        currentProgress.checkProgress(currentResearch, level, new ResearchMethod.SimpleMethodContext(team, null));

                        // If progress has changed, we sync
                        if (oldProgress != currentProgress.getProgress()) {
                            for (TeamMember member : team.getMembers()) {
                                ServerPlayer player = server.getPlayerList().getPlayer(member.player());
                                PacketDistributor.sendToPlayer(player, new ResearchProgressSyncPayload(currentResearch, currentProgress));
                            }

                        }

                        if (currentProgress.isComplete()) {
                            // Research Complete Logic
                            long completionTime = server.overworld().getDayTime() * 50L;
                            team.setResearchCompleted(currentResearch, completionTime);
                            team.onCompleteResearch(currentResearch, completionTime, server.getPlayerList()::getPlayer);

                            team.getQueue().remove(0, false);
                        }
                    } else {
                        Researchd.LOGGER.error("Current research progress for research {} is null", currentResearch.location());
                    }

                }

            }
        }
    }
}
