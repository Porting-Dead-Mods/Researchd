package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = Researchd.MODID, value = Dist.DEDICATED_SERVER)
public final class ResearchdServerEvents {
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        // Pass
    }
}
