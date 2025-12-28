package com.portingdeadmods.researchd;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.portingdeadlibs.api.resources.DynamicPack;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.compat.ftbteams.FTBTeamsCompat;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchPacksPayload;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchesPayload;
import com.portingdeadmods.researchd.networking.research.ResearchCacheReloadPayload;
import com.portingdeadmods.researchd.pdl.config.PDLConfigHelper;
import com.portingdeadmods.researchd.registries.*;
import com.portingdeadmods.researchd.registries.serializers.*;
import com.portingdeadmods.researchd.resources.ResearchdDynamicPackContents;
import com.portingdeadmods.researchd.resources.ResearchdExamplesSource;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Mod(Researchd.MODID)
public final class Researchd {
    public static final String MODID = "researchd";
    public static final String MODNAME = "Researchd";

	/**
	 * Mods will need to register all of their different ResearchEffectData entries here to be picked up by refreshResearches();
	 * Anything registered should only be of form {@code Supplier<AttachmentType<ResearchEffectData<?>>>}
	 * <br>
	 * Also PS: I will always hate generics (heart)
	 */
	public static final Set<Supplier<? extends AttachmentType<? extends ResearchEffectData<?>>>> RESEARCH_EFFECT_DATA_TYPES = new HashSet<>();

	public static void registerResearchEffectData(Supplier<? extends AttachmentType<? extends ResearchEffectData<?>>> data) {
		RESEARCH_EFFECT_DATA_TYPES.add(data);
	}

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
            LOGGER.debug(sb.toString());
        }
    }

    public Researchd(IEventBus modEventBus, ModContainer modContainer) {
        ResearchSerializers.SERIALIZERS.register(modEventBus);
        ResearchEffectSerializers.SERIALIZERS.register(modEventBus);
        ResearchMethodSerializers.SERIALIZERS.register(modEventBus);
        ResearchPackSerializers.SERIALIZERS.register(modEventBus);
        ResearchIconSerializers.SERIALIZERS.register(modEventBus);

        ResearchdAttachments.ATTACHMENTS.register(modEventBus);
        ResearchdItems.ITEMS.register(modEventBus);
        ResearchdDataComponents.COMPONENTS.register(modEventBus);
        ResearchdTab.TABS.register(modEventBus);
        ResearchdSavedData.SAVED_DATA.register(modEventBus);
        ResearchdBlocks.BLOCKS.register(modEventBus);
        ResearchdBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ResearchdMenuTypes.MENU_TYPES.register(modEventBus);
        ResearchMethodTypes.TYPES.register(modEventBus);
        ResearchEffectTypes.TYPES.register(modEventBus);
        ResearchdValueEffects.VALUE_EFFECTS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::addPackFinders);
		modEventBus.addListener(this::onCommonSetup);

        NeoForge.EVENT_BUS.addListener(this::onDatapacksSynced);

        PDLConfigHelper.registerConfig(ResearchdConfig.Client.class, ModConfig.Type.CLIENT)
                .register(modContainer);
        PDLConfigHelper.registerConfig(ResearchdConfig.Common.class, ModConfig.Type.COMMON)
                .register(modContainer);

		if (ResearchdCompatHandler.isFTBTeamsEnabled())
            FTBTeamsCompat.init();
    }

	private void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			registerResearchEffectData(ResearchdAttachments.DIMENSION_PREDICATE);
			registerResearchEffectData(ResearchdAttachments.ITEM_PREDICATE);
			registerResearchEffectData(ResearchdAttachments.RECIPE_PREDICATE);
		});
	}

    private void addPackFinders(AddPackFindersEvent event) {
        if (ResearchdConfig.Common.loadExamplesDatapack) {
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
        event.register(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_ICON_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER);
        event.register(ResearchdRegistries.VALUE_EFFECT);
        event.register(ResearchdRegistries.RESEARCH_METHOD_TYPE);
        event.register(ResearchdRegistries.RESEARCH_EFFECT_TYPE);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_KEY, Research.CODEC, Research.CODEC);
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPackImpl.CODEC, ResearchPackImpl.CODEC);
    }

    private void onDatapacksSynced(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        MinecraftServer server = event.getPlayerList().getServer();
        CommonResearchCache.initialize(server.overworld());

        ServerLevel overworld = server.overworld();
        ResearchTeamMap teamMap = ResearchdSavedData.TEAM_RESEARCH.get().getData(overworld);
        ResearchTeamHelper.resolveGlobalResearches(teamMap);

        // Add new researchPacks to teams in case new ones were added
        // TODO: Remove old researchPacks from teams in cases ones were removed
        ResearchTeamHelper.cleanupTeamResearches(teamMap, overworld);
        ResearchTeamHelper.initializeTeamResearches(teamMap, overworld);
        ResearchdSavedData.TEAM_RESEARCH.get().setData(overworld, teamMap);
        ResearchdSavedData.TEAM_RESEARCH.get().sync(overworld);

        if (player != null) {
            updateReloadableRegistries(player);
            PacketDistributor.sendToPlayer(player, ResearchCacheReloadPayload.INSTANCE);
        } else {
            event.getRelevantPlayers().forEach(Researchd::updateReloadableRegistries);
            event.getRelevantPlayers().forEach(p -> {
                PacketDistributor.sendToPlayer(p, ResearchCacheReloadPayload.INSTANCE);
            });
        }

    }

    private static void updateReloadableRegistries(ServerPlayer p) {
        PacketDistributor.sendToPlayer(p, new UpdateResearchesPayload(new HashMap<>(ResearchdManagers.getResearchesManager(p.level()).getByName())));
        PacketDistributor.sendToPlayer(p, new UpdateResearchPacksPayload(new HashMap<>(ResearchdManagers.getResearchPacksManager(p.level()).getByName())));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // Mod Helpers
    public static boolean isRecipeBlocked(Player player, ResourceLocation recipeId) {
        return player.getData(ResearchdAttachments.RECIPE_PREDICATE).blockedRecipes().contains(recipeId);
    }

    public static boolean isItemBlocked(Player player, ResourceLocation itemId) {
        return player.getData(ResearchdAttachments.ITEM_PREDICATE).blockedItems().stream().anyMatch(key -> key.location().equals(itemId));
    }

    public static boolean isItemBlocked(Player player, ResourceKey<Item> item) {
        return player.getData(ResearchdAttachments.ITEM_PREDICATE).blockedItems().contains(item);
    }

    public static boolean isItemBlocked(Player player, ItemLike item) {
        return player.getData(ResearchdAttachments.ITEM_PREDICATE).blockedItems().stream().anyMatch(key -> key.location().equals(BuiltInRegistries.ITEM.getKey(item.asItem())));
    }

    public static boolean isDimensionBlocked(Player player, ResourceLocation dimensionId) {
        return player.getData(ResearchdAttachments.DIMENSION_PREDICATE).blockedDimensions().stream().anyMatch(key -> key.location().equals(dimensionId));
    }

    public static boolean isDimensionBlocked(Player player, ResourceKey<DimensionType> dimension) {
        return player.getData(ResearchdAttachments.DIMENSION_PREDICATE).blockedDimensions().contains(dimension);
    }
}
