package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.impl.effects.AndResearchEffectWidget;
import com.portingdeadmods.researchd.client.impl.effects.DimensionUnlockEffectWidget;
import com.portingdeadmods.researchd.client.impl.methods.AndResearchMethodWidget;
import com.portingdeadmods.researchd.client.impl.methods.ConsumeItemResearchMethodWidget;
import com.portingdeadmods.researchd.client.impl.methods.ConsumePackResearchMethodWidget;
import com.portingdeadmods.researchd.client.impl.methods.OrResearchMethodWidget;
import com.portingdeadmods.researchd.client.screens.lab.ResearchLabScreen;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.registries.ResearchdMenuTypes;
import com.portingdeadmods.researchd.utils.WidgetConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod(value = ResearchdClient.MODID, dist = Dist.CLIENT)
public class ResearchdClient {
    public static final String MODID = "researchd";
    public static final String MODNAME = "Researchd";

    public static final Map<ResourceLocation, WidgetConstructor<? extends ResearchMethod>> RESEARCH_METHOD_WIDGETS = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, WidgetConstructor<? extends ResearchEffect>> RESEARCH_EFFECT_WIDGETS = new ConcurrentHashMap<>();

    public ResearchdClient(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::registerKeybinds);
        eventBus.addListener(this::registerColorHandlers);
        eventBus.addListener(this::registerMenus);
        eventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            addMethodWidget(ConsumePackResearchMethod.ID, ConsumePackResearchMethodWidget::new);
            addMethodWidget(ConsumeItemResearchMethod.ID, ConsumeItemResearchMethodWidget::new);
            addMethodWidget(OrResearchMethod.ID, OrResearchMethodWidget::new);
            addMethodWidget(AndResearchMethod.ID, AndResearchMethodWidget::new);

            addEffectWidget(AndResearchEffect.ID, AndResearchEffectWidget::new);
            addEffectWidget(DimensionUnlockEffect.ID, DimensionUnlockEffectWidget::new);

            ItemBlockRenderTypes.setRenderLayer(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), RenderType.solid()); // Should fiddle with render types till it works ngl
        });
    }

    private static <T extends ResearchMethod> void addMethodWidget(ResourceLocation id, WidgetConstructor<T> constructor) {
        RESEARCH_METHOD_WIDGETS.put(id, constructor);
    }

    private static <T extends ResearchEffect> void addEffectWidget(ResourceLocation id, WidgetConstructor<T> constructor) {
        RESEARCH_EFFECT_WIDGETS.put(id, constructor);
    }

    private void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(ResearchdKeybinds.OPEN_RESEARCH_SCREEN.get());
        event.register(ResearchdKeybinds.OPEN_RESEARCH_TEAM_SCREEN.get());
    }

    private void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            ResearchPackComponent researchPackComponent = stack.get(ResearchdDataComponents.RESEARCH_PACK);
            RegistryAccess access = Minecraft.getInstance().level.registryAccess();
            return layer == 1 && researchPackComponent.researchPackKey().isPresent() ? access.holderOrThrow(researchPackComponent.researchPackKey().get()).value().color() : -1;
        }, ResearchdItems.RESEARCH_PACK);
    }

    private void registerMenus(RegisterMenuScreensEvent event) {
        event.register(ResearchdMenuTypes.RESEARCH_LAB_MENU.get(), ResearchLabScreen::new);
    }

}
