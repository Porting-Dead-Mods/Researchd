package com.portingdeadmods.researchd.events.common;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.compat.ftbteams.FTBTeamsCompat;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.research.ResearchReloadPayload;
import com.portingdeadmods.researchd.networking.team.manager.AddTeamPayload;
import com.portingdeadmods.researchd.networking.team.manager.SyncTeamDataPayload;
import com.portingdeadmods.researchd.networking.team.manager.SyncTeamEffectDataPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdLifecycleHandler {
    // Server starts -> server config is now loaded; wire up FTB Teams compat if enabled
    @SubscribeEvent
    private static void onServerStarting(ServerStartingEvent event) {
        if (ResearchdCompatHandler.isFTBTeamsEnabled()) {
            FTBTeamsCompat.init();
        }
    }

    // World loads -> Get researches for static research manager
    @SubscribeEvent
    private static void onWorldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            // Initialize the research cache
            ResearchManagerImpl.setNewInstance(event.getLevel().getServer().overworld());
        }
    }

    // World unloads -> Remove researches from static research manager
    @SubscribeEvent
    private static void onWorldUnload(LevelEvent.Unload event) {
        // Reset the research cache
        ResearchManagerImpl.reset();
    }

    // Dimension changes -> reload researches in manager and teams
    //                   -> sync reloadable registries to client
    @SubscribeEvent
    private static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            ResearchHelperServer.syncReloadableRegistries(player);
            PacketDistributor.sendToPlayer(player, ResearchReloadPayload.INSTANCE);
        }
    }

    // Datapacks reload -> reload researches in manager and teams
    @SubscribeEvent
    private static void onDatapacksSynced(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        MinecraftServer server = event.getPlayerList().getServer();
        List<ServerPlayer> relevantPlayers = event.getPlayer() == null ? event.getPlayerList().getPlayers() : List.of(event.getPlayer());
        ResearchHelperServer.onReloadResearches(server, player, relevantPlayers);
    }

    // Player logs in -> Sync teams and team effect data
    @SubscribeEvent
    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResearchTeamMap map = TeamSavedData.getData(player.serverLevel());
            PacketDistributor.sendToPlayer(player, new SyncTeamDataPayload(map));
            PacketDistributor.sendToPlayer(player, new SyncTeamEffectDataPayload(TeamResearchEffectSavedData.getData(player.serverLevel())));

            // Create default team if player isn't in a team
            if (map.getTeamByPlayer(player) == null) {
                ResearchTeamImpl newTeam = (ResearchTeamImpl) map.createDefaultTeam(player);
                map.addTeam(newTeam);

                PacketDistributor.sendToAllPlayers(new AddTeamPayload(newTeam));
            }
        }
    }

    // Player joins -> create default team for player if player is not in a team yet
    @SubscribeEvent
    private static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
        }
    }
}