package com.portingdeadmods.researchd;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.portingdeadlibs.api.config.PDLConfigHelper;
import com.portingdeadmods.portingdeadlibs.api.resources.DynamicPack;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdDataComponents;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.registries.*;
import com.portingdeadmods.researchd.registries.serializers.*;
import com.portingdeadmods.researchd.resources.contents.ResearchdDynamicPackContents;
import com.portingdeadmods.researchd.resources.example.ResearchdExamplesSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

@Mod(Researchd.MODID)
public final class Researchd {
    public static final String MODID = "researchd";
    public static final String MODNAME = "Researchd";

    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Prints a debug message to the console if the 'consoleDebug' config option is enabled.
     *
     * @param category The 'category' of the debug message, used to filter messages in the console
     * @param message  Any number of objects that will be concatenated into a single message
     */
    public static void debug(String category, Object... message) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(category).append("] ");

        for (Object msg : message) {
            sb.append(msg.toString());
        }
        if (ResearchdConfig.Common.consoleDebug) {
            LOGGER.info(sb.toString());
        }
    }

	public static void log(String category, Object... message) {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(category).append("] ");

		for (Object msg : message) {
			sb.append(msg.toString());
		}

		LOGGER.info(sb.toString());
	}

    public Researchd(IEventBus modEventBus, ModContainer modContainer) {
        ResearchSerializers.SERIALIZERS.register(modEventBus);
        ResearchEffectSerializers.SERIALIZERS.register(modEventBus);
        ResearchMethodSerializers.SERIALIZERS.register(modEventBus);
        ResearchPackSerializers.SERIALIZERS.register(modEventBus);
        ResearchIconSerializers.SERIALIZERS.register(modEventBus);
        ResearchdEffectDataTypes.TYPES.register(modEventBus);

        ResearchdAttachments.ATTACHMENTS.register(modEventBus);
        ResearchdItems.ITEMS.register(modEventBus);
        ResearchdDataComponents.COMPONENTS.register(modEventBus);
        ResearchdTab.TABS.register(modEventBus);
        ResearchdBlocks.BLOCKS.register(modEventBus);
        ResearchdBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ResearchdMenuTypes.MENU_TYPES.register(modEventBus);
        ResearchMethodTypes.TYPES.register(modEventBus);
        ResearchEffectTypes.TYPES.register(modEventBus);
        ResearchdValueEffects.VALUE_EFFECTS.register(modEventBus);
        ResearchdCommandArguments.ARGUMENT_TYPE_INFOS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::addPackFinders);

        PDLConfigHelper.registerConfig(ResearchdConfig.Common.class, ModConfig.Type.COMMON, modContainer);
        PDLConfigHelper.registerConfig(ResearchdConfig.Server.class, ModConfig.Type.SERVER, modContainer);
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (ResearchdConfig.Common.loadDefaultDatapack) {
            DynamicPack pack = new DynamicPack(Researchd.rl("example_researches"), event.getPackType(), PackSource.FEATURE);
            switch (event.getPackType()) {
                case CLIENT_RESOURCES -> ResearchdDynamicPackContents.writeAssets(pack);
                case SERVER_DATA -> ResearchdDynamicPackContents.writeData(pack);
            }
            event.addRepositorySource(new ResearchdExamplesSource(pack.packId(), event.getPackType(), Pack.Position.BOTTOM, pack));
        }

		event.addPackFinders(rl("assets/researchd/darkmode"), PackType.CLIENT_RESOURCES, Component.literal("Researchd Dark Mode Assets"), PackSource.BUILT_IN, false, Pack.Position.TOP);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ResearchdBlockEntityTypes.RESEARCH_LAB_PART.get(), (be, dir) -> be.getControllerItemHandler());
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(ResearchdRegistries.RESEARCH_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_PACK_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE);
        event.register(ResearchdRegistries.RESEARCH_ICON_SERIALIZER);

        event.register(ResearchdRegistries.VALUE_EFFECT);
        event.register(ResearchdRegistries.RESEARCH_METHOD_TYPE);
        event.register(ResearchdRegistries.RESEARCH_EFFECT_TYPE);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_KEY, Research.CODEC, Research.CODEC);
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPackImpl.CODEC, ResearchPackImpl.CODEC);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
