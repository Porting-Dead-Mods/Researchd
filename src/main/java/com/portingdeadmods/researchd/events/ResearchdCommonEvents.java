package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.ResearchQueue;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.researchd.api.data.team.TeamMember;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.content.commands.ResearchdCommands;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.integration.KubeJSIntegration;
import com.portingdeadmods.researchd.networking.research.ResearchFinishedPayload;
import com.portingdeadmods.researchd.networking.research.ResearchMethodProgressSyncPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
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
        ResearchdCommands.register(event.getDispatcher());
    }

    public static void consumeItemResearchMethodLogic(@NotNull ResearchTeamMap data, MinecraftServer server) {
        for (ResearchTeam team : data.getResearchTeams().values()) {
            TeamResearchProgress teamProgress = team.getResearchProgress();
            List<ResearchMethodProgress<ConsumeItemResearchMethod>> progressList = teamProgress.getAllIncompleteMethodProgress(ConsumeItemResearchMethod.class);
            if (progressList == null) continue;
            if (progressList.isEmpty()) continue;
            if (team.getResearchProgressInQueue() == null) continue; // Useless line, but just make the IDE shut up

            Researchd.debug("ConsumeItemResearchMethodLogic", "Current progress on root: " + team.getResearchProgressInQueue().getProgress() + "/" + team.getResearchProgressInQueue().getMaxProgress(), " ROOT UUID: ", team.getResearchProgressInQueue().DEBUG_UUID());
            for (ResearchMethodProgress<ConsumeItemResearchMethod> progress : progressList) {
                ConsumeItemResearchMethod method = progress.getMethod();
                Ingredient ingredient = method.toConsume();
                int needed = (int) progress.getRemainingProgress();
                Researchd.debug("ConsumeItemResearchMethodLogic", "Current progress on possible method: " + progress.getProgress() + "/" + progress.getMaxProgress());
                if (progress.getParentAsOptional().isPresent())
                    Researchd.debug("ConsumeItemResearchMethodLogic", "PARENT UUID: ", progress.getParent().DEBUG_UUID());
                else
                    Researchd.debug("ConsumeItemResearchMethodLogic", "NO PARENT");

                Researchd.debug("ConsumeItemResearchMethodLogic", "Needed " + needed + " for method: " + method);

                for (TeamMember memberUUID : team.getMembers()) {
                    ServerPlayer player = server.getPlayerList().getPlayer(memberUUID.player());
                    if (player == null) continue;

                    int found = 0;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (needed <= 0) break;

                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.isEmpty()) continue;

                        if (ingredient.test(stack)) {
                            int toConsume = Math.min(stack.getCount(), needed);
                            stack.shrink(toConsume);
                            found += toConsume;
                            needed -= toConsume;
                        }
                    }
                    Researchd.debug("ConsumeItemResearchMethodLogic", "Found " + found + " for player: " + player.getName().getString());

                    progress.progress(found);

                    if (found > 0) {
                        double progressPercent = team.getResearchProgressInQueue().getProgress() / (double) team.getResearchProgressInQueue().getMaxProgress();
                        KubeJSIntegration.fireResearchProgressEvent(player, team.getFirstQueueResearch(), progressPercent);
                    }

                    Researchd.debug("ConsumeItemResearchMethodLogic", "New progress on possible method: " + progress.getProgress() + "/" + progress.getMaxProgress(), " | IS COMPLETE: ", progress.isComplete() ? "YES" : "NO");
                    Researchd.debug("ConsumeItemResearchMethodLogic", "New progress on root: " + team.getResearchProgressInQueue().getProgress() + "/" + team.getResearchProgressInQueue().getMaxProgress(), " ROOT UUID: ", team.getResearchProgressInQueue().DEBUG_UUID());

                    if (needed <= 0) break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (data != null) {
            if (level.getGameTime() % 20 == 0) {
                // Logic here please
                consumeItemResearchMethodLogic(data, server);
            }

            for (ResearchTeam team : data.getResearchTeams().values()) {
                TeamResearchProgress teamProgress = team.getResearchProgress();

                ResearchQueue queue = teamProgress.researchQueue();
                if (queue.isEmpty()) continue;
                ResourceKey<Research> research = queue.current();

                ResourceKey<Research> currentResearchKey = team.getFirstQueueResearch();
                Research currentResearch = ResearchHelperCommon.getResearch(currentResearchKey, level.registryAccess());
                ResearchMethodProgress currentResearchProgress = team.getResearchProgressInQueue();

                if (currentResearchProgress != null) {
                    if (level.getGameTime() % 2 == 0)
                        for (TeamMember memberUUID : team.getMembers()) {
                            ServerPlayer player = server.getPlayerList().getPlayer(memberUUID.player());
                            if (player == null) continue;

                            PacketDistributor.sendToPlayer(player, new ResearchMethodProgressSyncPayload(team.getFirstQueueResearch(), team.getAllQueueProgresses()));
                        }

                    // Research Complete Logic
                    if (currentResearchProgress.isComplete()) {
                        teamProgress.completeResearch(research, server.overworld().getDayTime() * 50L, level);

                        for (TeamMember playerUUIDs : team.getMembers()) {
                            ServerPlayer player = server.getPlayerList().getPlayer(playerUUIDs.player());
                            if (player == null) continue;

                            PacketDistributor.sendToPlayer(player, new ResearchFinishedPayload(research,  (int) server.overworld().getDayTime() * 50));

                            KubeJSIntegration.fireResearchCompletedEvent(player, research);

                            Researchd.debug("Researching", "Applying research effects for Research: " + team.getFirstQueueResearch() + " to player: " + player.getName().getString());
                            currentResearch.researchEffect().onUnlock(level, player, team.getFirstQueueResearch());
                        }

                        queue.getEntries().removeFirst();
                    }
                }

                // Save and sync the whole team research data every 20 ticks
                if (level.getGameTime() % 20 == 0) {
                    ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
                }
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
        }
    }
}