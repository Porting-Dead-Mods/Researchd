package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.client.editor.StandaloneEditorObject;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.RegistryDisplay;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.impl.editor.ResearchPackObject;
import com.portingdeadmods.researchd.client.impl.editor.effects.DimensionUnlockEffectObject;
import com.portingdeadmods.researchd.client.impl.editor.effects.ItemUnlockEffectObject;
import com.portingdeadmods.researchd.client.impl.editor.effects.RecipeUnlockEffectObject;
import com.portingdeadmods.researchd.client.impl.editor.effects.ValueEffectModifierObject;
import com.portingdeadmods.researchd.client.impl.icons.ClientItemResearchIcon;
import com.portingdeadmods.researchd.client.impl.editor.SimpleResearchObject;
import com.portingdeadmods.researchd.client.impl.info.effects.*;
import com.portingdeadmods.researchd.client.impl.icons.ClientSpriteResearchIcon;
import com.portingdeadmods.researchd.client.impl.icons.ClientTextResearchIcon;
import com.portingdeadmods.researchd.client.impl.info.methods.*;
import com.portingdeadmods.researchd.client.impl.editor.methods.CheckItemPresenceMethodObject;
import com.portingdeadmods.researchd.client.impl.editor.methods.ConsumeItemMethodObject;
import com.portingdeadmods.researchd.client.impl.editor.methods.ConsumePackMethodObject;
import com.portingdeadmods.researchd.client.renderers.ResearchLabBER;
import com.portingdeadmods.researchd.client.screens.lab.ResearchLabScreen;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.*;
import com.portingdeadmods.researchd.impl.research.icons.SpriteResearchIcon;
import com.portingdeadmods.researchd.impl.research.icons.TextResearchIcon;
import com.portingdeadmods.researchd.impl.research.method.*;
import com.portingdeadmods.researchd.registries.*;
import com.portingdeadmods.researchd.utils.WidgetConstructor;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import javax.lang.model.element.ModuleElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mod(value = Researchd.MODID, dist = Dist.CLIENT)
public final class ResearchdClient {
    public static final Map<ResourceLocation, WidgetConstructor<? extends ResearchMethod>> RESEARCH_METHOD_WIDGETS = new HashMap<>();
    public static final Map<ResourceLocation, WidgetConstructor<? extends ResearchEffect>> RESEARCH_EFFECT_WIDGETS = new HashMap<>();
    public static final Map<ResourceLocation, Function<ResearchIcon, ClientResearchIcon<?>>> RESEARCH_ICONS = new HashMap<>();
    public static final Map<ResourceLocation, StandaloneEditorObject<? extends Research>> CLIENT_RESEARCHES = new HashMap<>();
    public static final Map<ResourceLocation, StandaloneEditorObject<? extends ResearchPack>> CLIENT_RESEARCH_PACKS = new HashMap<>();
    public static final Map<ResourceLocation, TypedEditorObject<? extends ResearchMethod, ResearchMethodType>> CLIENT_RESEARCH_METHOD_TYPES = new HashMap<>();
    public static final ModelResourceLocation RESEARCH_LAB_MODEL = ModelResourceLocation.standalone(Researchd.rl("block/research_lab"));
    public static final Map<ResourceLocation, TypedEditorObject<? extends ResearchEffect, ResearchEffectType>> CLIENT_RESEARCH_EFFECT_TYPES = new HashMap<>();

    public static int previewRendererResearchPackColor = -1;

    public ResearchdClient(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::registerKeybinds);
        eventBus.addListener(this::registerColorHandlers);
        eventBus.addListener(this::registerMenus);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::registerAdditionalModels);
        eventBus.addListener(this::registerBER);

        NeoForge.EVENT_BUS.addListener(this::renderOutline);
        NeoForge.EVENT_BUS.addListener(this::addTooltip);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static TypedEditorObject<? extends ResearchEffect, ResearchEffectType> getClientEffectType(ResearchEffectType effectType) {
        return CLIENT_RESEARCH_EFFECT_TYPES.get(effectType.id());
    }

    public static TypedEditorObject<? extends ResearchMethod, ResearchMethodType> getClientMethodType(ResearchMethodType methodType) {
        return CLIENT_RESEARCH_METHOD_TYPES.get(methodType.id());
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            addMethodWidget(ConsumePackResearchMethod.ID, ConsumePackResearchMethodWidget::new);
            addMethodWidget(ConsumeItemResearchMethod.ID, ConsumeItemResearchMethodWidget::new);
            addMethodWidget(CheckItemPresenceResearchMethod.ID, CheckItemPresenceResearchMethodWidget::new);
            addMethodWidget(OrResearchMethod.ID, OrResearchMethodWidget::new);
            addMethodWidget(AndResearchMethod.ID, AndResearchMethodWidget::new);

            addEffectWidget(AndResearchEffect.ID, AndResearchEffectWidget::new);
            addEffectWidget(DimensionUnlockEffect.ID, DimensionUnlockEffectWidget::new);
            addEffectWidget(EmptyResearchEffect.ID, EmptyResearchEffectWidget::new);
            addEffectWidget(RecipeUnlockEffect.ID, RecipeUnlockEffectWidget::new);
            addEffectWidget(ItemUnlockEffect.ID, UnlockItemEffectWidget::new);

            addEffectWidgetUnsafe(IncreaseValueEffect.ID, ValueEffectModifierEffectWidget::new);
            addEffectWidgetUnsafe(DecreaseValueEffect.ID, ValueEffectModifierEffectWidget::new);
            addEffectWidgetUnsafe(MultiplyValueEffect.ID, ValueEffectModifierEffectWidget::new);
            addEffectWidgetUnsafe(DivideValueEffect.ID, ValueEffectModifierEffectWidget::new);

            addClientResearchIcon(ItemResearchIcon.ID, ClientItemResearchIcon::new);
            addClientResearchIcon(TextResearchIcon.ID, ClientTextResearchIcon::new);
            addClientResearchIcon(SpriteResearchIcon.ID, ClientSpriteResearchIcon::new);

            CLIENT_RESEARCHES.put(Researchd.rl(SimpleResearch.ID), SimpleResearchObject.INSTANCE);

            CLIENT_RESEARCH_PACKS.put(Researchd.rl(ResearchPackImpl.ID), ResearchPackObject.INSTANCE);

            CLIENT_RESEARCH_METHOD_TYPES.put(ConsumeItemResearchMethod.ID, ConsumeItemMethodObject.INSTANCE);
            CLIENT_RESEARCH_METHOD_TYPES.put(ConsumePackResearchMethod.ID, ConsumePackMethodObject.INSTANCE);
            CLIENT_RESEARCH_METHOD_TYPES.put(CheckItemPresenceResearchMethod.ID, CheckItemPresenceMethodObject.INSTANCE);

            CLIENT_RESEARCH_EFFECT_TYPES.put(DimensionUnlockEffectObject.ID, DimensionUnlockEffectObject.INSTANCE);
            CLIENT_RESEARCH_EFFECT_TYPES.put(ItemUnlockEffectObject.ID, ItemUnlockEffectObject.INSTANCE);
            CLIENT_RESEARCH_EFFECT_TYPES.put(RecipeUnlockEffectObject.ID, RecipeUnlockEffectObject.INSTANCE);

            CLIENT_RESEARCH_EFFECT_TYPES.put(IncreaseValueEffect.ID, new ValueEffectModifierObject(ResearchEffectTypes.INCREASE_VALUE.get(), IncreaseValueEffect::new));
            CLIENT_RESEARCH_EFFECT_TYPES.put(DecreaseValueEffect.ID, new ValueEffectModifierObject(ResearchEffectTypes.DECREASE_VALUE.get(), DecreaseValueEffect::new));
            CLIENT_RESEARCH_EFFECT_TYPES.put(DivideValueEffect.ID, new ValueEffectModifierObject(ResearchEffectTypes.DIVIDE_VALUE.get(), DivideValueEffect::new));
            CLIENT_RESEARCH_EFFECT_TYPES.put(MultiplyValueEffect.ID, new ValueEffectModifierObject(ResearchEffectTypes.MULTIPLE_VALUE.get(), MultiplyValueEffect::new));

            ItemBlockRenderTypes.setRenderLayer(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), RenderType.solid()); // Should fiddle with render types till it works ngl
        });
    }

    private static <I extends ResearchIcon> void addClientResearchIcon(ResourceLocation id, Function<I, ClientResearchIcon<?>> factory) {
        RESEARCH_ICONS.put(id, (Function<ResearchIcon, ClientResearchIcon<?>>) factory);
    }

    private static <T extends ResearchMethod> void addMethodWidget(ResourceLocation id, WidgetConstructor<T> constructor) {
        RESEARCH_METHOD_WIDGETS.put(id, constructor);
    }

    private static void addEffectWidgetUnsafe(ResourceLocation id, WidgetConstructor constructor) {
        RESEARCH_EFFECT_WIDGETS.put(id, constructor);
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
            if (layer == 1 && previewRendererResearchPackColor != -1) {
                return previewRendererResearchPackColor;
            }

            ResearchPackComponent researchPackComponent = stack.get(ResearchdDataComponents.RESEARCH_PACK);
            ClientLevel level = Minecraft.getInstance().level;
            if (layer == 1 && researchPackComponent.researchPackKey().isPresent()) {
                ResearchPack researchPack = ResearchHelperCommon.getResearchPack(researchPackComponent.researchPackKey().get(), level);
                if (researchPack != null) {
                    return researchPack.color();
                }
            }
            return -1;
        }, ResearchdItems.RESEARCH_PACK);
    }

    private void registerMenus(RegisterMenuScreensEvent event) {
        event.register(ResearchdMenuTypes.RESEARCH_LAB_MENU.get(), ResearchLabScreen::new);
    }

    private void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(RESEARCH_LAB_MODEL);
    }

    private void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), ResearchLabBER::new);
    }

    private void renderOutline(RenderHighlightEvent.Block event) {
//        if (event.getCamera().getEntity() instanceof LivingEntity living) {
//            Level world = living.level();
//            BlockHitResult rtr = event.getTarget();
//            BlockPos pos = rtr.getBlockPos();
//            Vec3 renderView = event.getCamera().getPosition();
//            BlockState targetBlock = world.getBlockState(rtr.getBlockPos());
//            if (targetBlock.getBlock() == ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get() || targetBlock.getBlock() == ResearchdBlocks.RESEARCH_LAB_PART.get()) {
//                ((LevelRendererMixin) event.getLevelRenderer()).callRenderHitOutline(
//                        event.getPoseStack(), event.getMultiBufferSource().getBuffer(ResearchdRenderTypes.LINES_NONTRANSLUCENT),
//                        living, renderView.x, renderView.y, renderView.z,
//                        pos, targetBlock
//                );
//                event.setCanceled(true);
//            }
//        }
    }

    private void addTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().has(ResearchdDataComponents.RESEARCH_PACK)) {
            Optional<ResourceKey<ResearchPack>> key = event.getItemStack().get(ResearchdDataComponents.RESEARCH_PACK).researchPackKey();
            if (key.isPresent()) {
                ResearchPack pack = ResearchHelperCommon.getResearchPack(key.get(), Minecraft.getInstance().level);
                if (pack instanceof RegistryDisplay<?> display) {
                    event.getToolTip().set(0, display.getDisplayNameUnsafe(key.get()));
                    event.getToolTip().add(1, display.getDisplayDescriptionUnsafe(key.get()));
                }
            }
        }
    }

}
