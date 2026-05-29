package com.portingdeadmods.researchd.events.common;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.research.ResearchCacheReloadPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdLifecycleHandler {
    @SubscribeEvent
    private static void onWorldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            // Initialize the research cache
            ResearchManagerImpl.setNewInstance(event.getLevel().getServer().overworld());
        }
    }

    @SubscribeEvent
    private static void onWorldUnload(LevelEvent.Unload event) {
        // Reset the research cache
        ResearchManagerImpl.reset();
    }

    @SubscribeEvent
    private static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide()) {
            PacketDistributor.sendToPlayer(((ServerPlayer) event.getEntity()), new ResearchCacheReloadPayload());
            ResearchHelperServer.syncReloadableRegistries((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    private static void onDatapacksSynced(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        MinecraftServer server = event.getPlayerList().getServer();
        List<ServerPlayer> relevantPlayers = event.getPlayer() == null ? event.getPlayerList().getPlayers() : List.of(event.getPlayer());
        ResearchHelperServer.onReloadResearches(server, player, relevantPlayers);
    }

    @SubscribeEvent
    private static void onJoinLevel(EntityJoinLevelEvent entity) {
        if (!entity.getLevel().isClientSide()) {
            if (entity.getEntity() instanceof ServerPlayer player) {
                ServerLevel level = player.serverLevel();
                ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
                data.initPlayer(player);
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
                ResearchdSavedData.TEAM_RESEARCH.get().syncToPlayer(player);
            }
        }
    }
}