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
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.commands.ResearchdCommands;
import com.portingdeadmods.researchd.content.predicates.RecipePredicateData;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.SyncSavedDataPayload;
import com.portingdeadmods.researchd.networking.research.ResearchFinishedPayload;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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

            if (ResearchdKeybinds.OPEN_RESEARCH_TEAM_SCREEN.get().consumeClick()) {
                Minecraft.getInstance().setScreen(new ResearchTeamScreen());
            }
        }

        @SubscribeEvent
        public static void onToolTipEvent(ItemTooltipEvent event) {
            if (event.getEntity() == null) return;
            LocalPlayer player = (LocalPlayer) event.getEntity();
            Item item = event.getItemStack().getItem();

            RecipePredicateData recipeData = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());
            UniqueArray<Item> blockedItems = new UniqueArray<>();

            recipeData.blockedRecipes().forEach(recipe -> {
                blockedItems.add(recipe.value().getResultItem(player.registryAccess()).getItem());
            });

            if (blockedItems.contains(item)) {
                event.getToolTip().add(Component.literal("")); // Add a blank line for spacing
                event.getToolTip().add(Component.literal("This item is blocked by a research!").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
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
                ResearchHelper.refreshResearches(serverPlayer);
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
            LocalPlayer player = Minecraft.getInstance().player;

            if (player != null && level != null) {
                ResearchTeamMap map = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
                if (map != null) {
                    ResearchProgress researchProgress = map.getTeamByPlayer(player).getResearchProgress();
                    if (researchProgress != null) {
                        ResearchQueue queue = researchProgress.researchQueue();
                        if (!queue.isEmpty()) {
                            if (queue.getMaxResearchProgress() > queue.getResearchProgress()) {
                                queue.setResearchProgress(queue.getResearchProgress() + 1);
                            } else {
                                queue.setResearchProgress(0);
                            }
                        }
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
                for (Map.Entry<UUID, ResearchTeam> entry : data.getResearchTeams().entrySet()) {
                    ResearchTeam team = entry.getValue();
                    ResearchProgress researchProgress = team.getResearchProgress();

                    // v Research Queue Logic v
                    ResearchQueue queue = researchProgress.researchQueue();

                    if (!queue.isEmpty()) {
                        ResearchInstance instance = queue.getEntries().getFirst();
                        Research research = ResearchHelper.getResearch(instance.getResearch(), server.registryAccess());

                        // TODO: Continue when the ground basis is set n done
                    }


                    if (level.getGameTime() % 10 == 0) {
                        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
                    }
                }
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

}
