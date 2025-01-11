package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

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
    }
}
