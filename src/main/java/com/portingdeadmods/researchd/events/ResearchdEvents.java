package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.PDLClientSavedData;
import com.portingdeadmods.researchd.api.data.PDLSavedData;
import com.portingdeadmods.researchd.api.data.SavedDataHolder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.commands.ResearchdCommands;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import com.portingdeadmods.researchd.networking.SyncSavedDataPayload;
import com.portingdeadmods.researchd.networking.research.ResearchFinishedPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ResearchdEvents {
    @EventBusSubscriber(modid = Researchd.MODID, value = Dist.CLIENT)
    public static final class Client {
        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Pre event) {
            if (ResearchdKeybinds.OPEN_RESEARCH_SCREEN.get().consumeClick()) {
                Minecraft.getInstance().setScreen(new ResearchScreen());
            }
        }
    }

    @EventBusSubscriber(modid = Researchd.MODID)
    public static final class Common {
        @SubscribeEvent
        private static void entityPlaceEvent(BlockEvent.EntityPlaceEvent event) {
            Entity entity = event.getEntity();
            Level level = entity.level();

            if (entity instanceof Player player && level instanceof ServerLevel serverLevel) {
                UUID uuid = player.getUUID();
                if (serverLevel.getBlockEntity(event.getPos()) != null)
                    serverLevel.getBlockEntity(event.getPos()).setData(ResearchdAttachments.PLACED_BY_UUID, uuid);
            }
        }

        @SubscribeEvent
        private static void onCommandRegister(RegisterCommandsEvent event) {
            ResearchdCommands.register(event.getDispatcher());
        }

        @SubscribeEvent
        private static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                ResearchHelper.initResearches(serverPlayer);
                for (Map.Entry<ResourceKey<PDLSavedData<?>>, PDLSavedData<?>> savedData : ResearchdRegistries.SAVED_DATA.entrySet()) {
                    PDLSavedData<?> value = savedData.getValue();
                    if (value.isSynced()) {
                        sendSavedDataSyncPayload(serverPlayer, savedData.getKey().location(), value);
                        value.onSyncFunction().accept(serverPlayer);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) {
            PDLClientSavedData.CLIENT_SAVED_DATA_CACHE.clear();
        }

        private static <T> void sendSavedDataSyncPayload(ServerPlayer serverPlayer, ResourceLocation id, PDLSavedData<?> savedData) {
            PDLSavedData<T> savedData1 = (PDLSavedData<T>) savedData;
            T data = savedData1.getData(serverPlayer.serverLevel());
            PacketDistributor.sendToPlayer(serverPlayer, new SyncSavedDataPayload<>(new SavedDataHolder<>(id, savedData1), data));
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Level level = Minecraft.getInstance().level;
            EntityResearchImpl data = ResearchdSavedData.PLAYER_RESEARCH.get().getData(level);
            if (data != null) {
                ResearchQueue queue = data.researchQueue();
                if (!queue.isEmpty()) {
                    if (queue.getMaxResearchProgress() > queue.getResearchProgress()) {
                        queue.setResearchProgress(queue.getResearchProgress() + 1);
                    } else {
                        queue.setResearchProgress(0);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event) {
            MinecraftServer server = event.getServer();
            ServerLevel level = server.overworld();
            EntityResearchImpl data = ResearchdSavedData.PLAYER_RESEARCH.get().getData(level);
            if (data != null) {
                ResearchQueue queue = data.researchQueue();
                if (!queue.isEmpty()) {
                    if (queue.getMaxResearchProgress() > queue.getResearchProgress()) {
                        queue.setResearchProgress(queue.getResearchProgress() + 1);
                    } else {
                        queue.setResearchProgress(0);
                        ResearchInstance first = queue.getEntries().getFirst();
                        first.setResearchStatus(ResearchStatus.RESEARCHED);
                        queue.remove(0);
                        data.completeResearch(first);
                        ResearchdSavedData.PLAYER_RESEARCH.get().setData(level, data);
                        PacketDistributor.sendToAllPlayers(ResearchFinishedPayload.INSTANCE);
                    }
                }
            }
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

}
