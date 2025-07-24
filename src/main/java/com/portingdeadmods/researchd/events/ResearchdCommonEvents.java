package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.PDLClientSavedData;
import com.portingdeadmods.researchd.api.data.PDLSavedData;
import com.portingdeadmods.researchd.api.data.SavedDataHolder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.commands.ResearchdCommands;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import com.portingdeadmods.researchd.networking.SyncSavedDataPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
        ResearchdCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    private static void onLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        PDLClientSavedData.CLIENT_SAVED_DATA_CACHE.clear();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();

        ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        if (data != null) {
            for (Map.Entry<UUID, ResearchTeam> entry : data.getResearchTeams().entrySet()) {
                ResearchTeam team = entry.getValue();
                ResearchProgress researchProgress = team.getResearchProgress();

                // v Research Queue Logic v
                ResearchQueue queue = researchProgress.researchQueue();

                if (!queue.isEmpty()) {
                    ResearchInstance instance = queue.getEntries().getFirst();
                    Research research = ResearchHelperCommon.getResearch(instance.getResearch(), server.registryAccess());

                    // TODO: Continue when the ground basis is set n done
                }


                if (level.getGameTime() % 10 == 0) {
                    ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
                }
            }
        }
    }

    private static <T> void sendSavedDataSyncPayload(ServerPlayer serverPlayer, ResourceLocation id, PDLSavedData<?> savedData) {
        PDLSavedData<T> savedData1 = (PDLSavedData<T>) savedData;
        T data = savedData1.getData(serverPlayer.serverLevel());
        PacketDistributor.sendToPlayer(serverPlayer, new SyncSavedDataPayload<>(new SavedDataHolder<>(id, savedData1), data));
    }

    @SubscribeEvent
    private static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        MinecraftServer server = serverPlayer.server;
        ServerLevel level = server.overworld();
        ResearchTeamMap researchTeamMap = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
        researchTeamMap.initPlayer(serverPlayer);
        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, researchTeamMap);

        for (Map.Entry<ResourceKey<PDLSavedData<?>>, PDLSavedData<?>> savedData : ResearchdRegistries.SAVED_DATA.entrySet()) {
            PDLSavedData<?> value = savedData.getValue();
            if (value.isSynced()) {
                sendSavedDataSyncPayload(serverPlayer, savedData.getKey().location(), value);
                value.onSyncFunction().accept(serverPlayer);
            }
        }

        // v Research Predicate Attachment v
        ResearchHelperCommon.refreshResearches(serverPlayer);
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        RegistryAccess registryAccess = server.registryAccess();
        HolderLookup.RegistryLookup<ResearchPack> packs = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY);

        if (!Researchd.RESEARCH_PACKS.isEmpty()) {
            Researchd.RESEARCH_PACKS.addAll(packs.listElements().map(Holder.Reference::value).toList());
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

    public static void onJoinLevel(EntityJoinLevelEvent entity) {

    }
//
//        private static List<ResearchInstance> getChildren(Level level, ResearchInstance instance) {
//            List<ResearchInstance> children = new ArrayList<>();
//            for (Holder<Research> levelResearch : ResearchHelper.getLevelResearches(level)) {
//                if (levelResearch.value().parents().contains(instance.getResearch())) {
//                    ResearchStatus status;
//                    if (instance.getResearchStatus() == ResearchStatus.RESEARCHED) {
//                        status = ResearchStatus.RESEARCHABLE;
//                    } else if (ResearchdSavedData.PLAYER_RESEARCH.get().getData(level).isCompleted(levelResearch.getKey())) {
//                        status = ResearchStatus.RESEARCHED;
//                    }else {
//                        status = ResearchStatus.LOCKED;
//                    }
//                    children.add(new ResearchInstance(levelResearch.getKey(), status));
//                }
//            }
//            return children;
//        }
}