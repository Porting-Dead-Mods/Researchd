package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

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

}
