package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.commands.ResearchdCommands;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.networking.SyncEntityResearchPayload;
import com.portingdeadmods.researchd.utils.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class ResearchedEvents {
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
                PacketDistributor.sendToPlayer(serverPlayer, new SyncEntityResearchPayload(serverPlayer.getData(ResearchdAttachments.ENTITY_RESEARCH)));
            }
        }
    }

    @EventBusSubscriber(modid = Researchd.MODID, value = Dist.DEDICATED_SERVER)
    public static final class Server {

    }
}
