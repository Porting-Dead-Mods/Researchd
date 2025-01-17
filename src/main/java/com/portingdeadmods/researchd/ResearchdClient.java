package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = ResearchdClient.MODID, dist = Dist.CLIENT)
public class ResearchdClient {
    public static final String MODID = "researchd";

    public ResearchdClient(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::registerKeybinds);
        eventBus.addListener(this::registerColorHandlers);
    }

    private void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(ResearchdKeybinds.OPEN_RESEARCH_SCREEN.get());
    }

    private void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            ResearchPackComponent researchPackComponent = stack.get(ResearchdDataComponents.RESEARCH_PACK);
            RegistryAccess access = Minecraft.getInstance().level.registryAccess();
            return layer == 1 && researchPackComponent.researchPackKey().isPresent() ? access.holderOrThrow(researchPackComponent.researchPackKey().get()).value().color() : -1;
        }, ResearchdItems.RESEARCH_PACK);
    }
}
