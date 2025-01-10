package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.Researchd;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Researchd.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkEvents {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Researchd.MODID);
        registrar.playToServer(
                ManageMemberPayload.TYPE,
                ManageMemberPayload.STREAM_CODEC,
                ManageMemberPayload::keyPressedAction
        );
    }
}
