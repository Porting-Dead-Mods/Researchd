package com.portingdeadmods.researchd;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.registries.*;
import com.portingdeadmods.researchd.registries.serializers.ResearchEffectSerializers;
import com.portingdeadmods.researchd.registries.serializers.ResearchMethodSerializers;
import com.portingdeadmods.researchd.registries.serializers.ResearchSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

@Mod(Researchd.MODID)
public class Researchd {
    public static final String MODID = "researchd";
    public static final String MODNAME = "Researchd";

    // Static Variables
    public static final LazyFinal<Integer> RESEARCH_PACK_COUNT = LazyFinal.create();
    public static final UniqueArray<SimpleResearchPack> RESEARCH_PACKS = new UniqueArray<>();
    public static final LazyFinal<HolderLookup.RegistryLookup<SimpleResearchPack>> RESEARCH_PACK_REGISTRY = LazyFinal.create();

    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Prints a debug message to the console if the 'consoleDebug' config option is enabled.
     *
     * @param category The 'category' of the debug message, used to filter messages in the console
     * @param message Any number of objects that will be concatenated into a single message
     */
    public static void debug(String category, Object... message) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(category).append("] ");

        for (Object msg : message) {
            sb.append(msg.toString());
        }
        if (ResearchdCommonConfig.consoleDebug) {
            LOGGER.debug(sb.toString());
        }
    }

    public Researchd(IEventBus modEventBus, ModContainer modContainer) {
        ResearchdAttachments.ATTACHMENTS.register(modEventBus);
        ResearchSerializers.SERIALIZERS.register(modEventBus);
        ResearchEffectSerializers.SERIALIZERS.register(modEventBus);
        ResearchMethodSerializers.SERIALIZERS.register(modEventBus);
        ResearchdItems.ITEMS.register(modEventBus);
        ResearchdDataComponents.COMPONENTS.register(modEventBus);
        ResearchdTab.TABS.register(modEventBus);
        ResearchdSavedData.SAVED_DATA.register(modEventBus);
        ResearchdBlocks.BLOCKS.register(modEventBus);
        ResearchdBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ResearchdMenuTypes.MENU_TYPES.register(modEventBus);
        ResearchdValueEffects.VALUE_EFFECTS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);

        //modContainer.registerConfig(ModConfig.Type.CLIENT, ResearchdClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, ResearchdCommonConfig.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        //event.registerEntity(ResearchdCapabilities.ENTITY, EntityType.PLAYER, (player, ctx) -> new EntityResearchWrapper(player));
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(ResearchdRegistries.RESEARCH_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER);
        event.register(ResearchdRegistries.SAVED_DATA);
        event.register(ResearchdRegistries.VALUE_EFFECT);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_KEY, Research.CODEC, Research.CODEC);
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_PACK_KEY, SimpleResearchPack.CODEC, SimpleResearchPack.CODEC);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
