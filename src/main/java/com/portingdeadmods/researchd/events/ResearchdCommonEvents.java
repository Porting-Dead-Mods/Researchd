package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodProgress;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.compat.KubeJSIntegration;
import com.portingdeadmods.researchd.content.commands.ResearchdCommands;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.cache.ReceiveServerPlayers;
import com.portingdeadmods.researchd.networking.research.ResearchFinishedPayload;
import com.portingdeadmods.researchd.networking.research.ResearchMethodProgressSyncPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdCommonEvents {
    @SubscribeEvent
    private static void entityPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (entity instanceof Player player) {
            UUID uuid = player.getUUID();
            if (level.getBlockEntity(event.getPos()) != null)
                level.getBlockEntity(event.getPos()).setData(ResearchdAttachments.PLACED_BY_UUID, uuid);
        }
    }

    @SubscribeEvent
    private static void onCommandRegister(RegisterCommandsEvent event) {
        ResearchdCommands.register(event.getDispatcher(), event.getBuildContext());
    }

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
                ResourceKey<Research> research = team.getCurrentResearch();

                if (research != null) {
                    ResourceKey<Research> currentResearchKey = team.getCurrentResearch();
                    Research currentResearch = ResearchHelperCommon.getResearch(currentResearchKey, level.registryAccess());
                    ResearchMethodProgress<?> currentResearchProgress = team.getCurrentProgress();

                    if (currentResearchProgress != null) {
                        if (level.getGameTime() % 4 == 0) {
                            for (TeamMember memberUUID : team.getMembers()) {
                                ServerPlayer player = server.getPlayerList().getPlayer(memberUUID.player());
                                if (player == null) continue;

                                PacketDistributor.sendToPlayer(player, new ResearchMethodProgressSyncPayload(team.getCurrentResearch(), currentResearchProgress));
                            }
                        }

                        // Research Complete Logic
                        if (currentResearchProgress.isComplete()) {
                            team.completeResearch(research, server.overworld().getDayTime() * 50L, level);

                            for (TeamMember playerUUIDs : team.getMembers()) {
                                ServerPlayer player = server.getPlayerList().getPlayer(playerUUIDs.player());
                                if (player == null) continue;

                                PacketDistributor.sendToPlayer(player, new ResearchFinishedPayload(research, (int) server.overworld().getDayTime() * 50));

                                KubeJSIntegration.fireResearchCompletedEvent(player, research);

                                Researchd.debug("Researching", "Applying research effects for Research: " + team.getCurrentResearch() + " to player: " + player.getName().getString());
                                currentResearch.researchEffect().onUnlock(level, player, team.getCurrentResearch());
                            }

                            team.getQueue().remove(0, false);
                        }
                    }

                    // Save and sync the whole team research teamMap every 20 ticks
                    if (level.getGameTime() % 20 == 0) {
                        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, teamMap);
                    }
                }
            }
        }
    }

    public static void handleResearchMethods(Level level, ResearchTeamMap teamMap) {
        for (ResearchTeam team : teamMap.researchTeams().values()) {
            Map<ResourceKey<Research>, ResearchMethodProgress<?>> researchProgresses = team.getResearchProgresses();
            if (researchProgresses.isEmpty()) continue;

            for (Map.Entry<ResourceKey<Research>, ResearchMethodProgress<?>> entry : researchProgresses.entrySet()) {
                entry.getValue().checkProgress(level, team, entry.getKey());
            }

        }
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        RegistryAccess registryAccess = server.registryAccess();
        HolderLookup.RegistryLookup<SimpleResearchPack> packs = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY);

        if (!Researchd.RESEARCH_PACKS.isEmpty()) {
            Researchd.RESEARCH_PACKS.addAll(packs.listElements().map(Holder.Reference::value).sorted(Comparator.comparingInt(SimpleResearchPack::sorting_value)).toList());
            Researchd.debug("Researchd Constants Server", "Initialized research packs.", Researchd.RESEARCH_PACKS, "");
        }

        if (!Researchd.RESEARCH_PACK_COUNT.isInitialized()) {
            Researchd.RESEARCH_PACK_COUNT.initialize((int) packs.listElements().count());
            Researchd.debug("Researchd Constants Server", "Initialized research pack count: ", Researchd.RESEARCH_PACK_COUNT.get());
        }

        if (!Researchd.RESEARCH_PACK_REGISTRY.isInitialized()) {
            Researchd.RESEARCH_PACK_REGISTRY.initialize(registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY));
            Researchd.debug("Researchd Constants Server", "Initialized research pack registry LazyFinal. ");
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            // Initialize the research cache
            CommonResearchCache.initialize(event.getLevel());

            ResearchTeamHelper.resolveGlobalResearches(ResearchdSavedData.TEAM_RESEARCH.get().getData((Level) event.getLevel()));

            // Add new researches to teams in case new ones were added
            // TODO: Remove old researches from teams in cases ones were removed
            ResearchTeamHelper.initializeTeamResearches(event.getLevel());
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        // Reset the research cache
        CommonResearchCache.reset();
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent entity) {
        if (entity.getLevel().isClientSide()) return;
        if (entity.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
            data.initPlayer(player);
            ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
            ResearchdSavedData.TEAM_RESEARCH.get().syncToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onLoggingIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PacketDistributor.sendToAllPlayers(new ReceiveServerPlayers(List.of(sp.getGameProfile())));
        }
    }
}