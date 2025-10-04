package com.portingdeadmods.researchd;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.portingdeadlibs.api.resources.DynamicPack;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchPacksPayload;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchesPayload;
import com.portingdeadmods.researchd.networking.research.ResearchCacheReloadPayload;
import com.portingdeadmods.researchd.registries.*;
import com.portingdeadmods.researchd.registries.serializers.ResearchEffectSerializers;
import com.portingdeadmods.researchd.registries.serializers.ResearchMethodSerializers;
import com.portingdeadmods.researchd.registries.serializers.ResearchSerializers;
import com.portingdeadmods.researchd.resources.ResearchdDynamicPackContents;
import com.portingdeadmods.researchd.resources.ResearchdExamplesSource;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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
        modEventBus.addListener(this::addPackFinders);

        NeoForge.EVENT_BUS.addListener(this::onDatapacksSynced);

        //modContainer.registerConfig(ModConfig.Type.CLIENT, ResearchdClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, ResearchdCommonConfig.SPEC);
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (ResearchdCommonConfig.loadExamplesDatapack) {
            DynamicPack pack = new DynamicPack(Researchd.rl("example_researches"), event.getPackType(), PackSource.FEATURE);
            switch (event.getPackType()) {
                case CLIENT_RESOURCES -> {
                    ResearchdDynamicPackContents.writeAssets(pack);
                }
                case SERVER_DATA -> {
                    ResearchdDynamicPackContents.writeData(pack);
                }
            }
            event.addRepositorySource(new ResearchdExamplesSource(pack.packId(), event.getPackType(), Pack.Position.BOTTOM, pack));
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ResearchdBlockEntityTypes.RESEARCH_LAB_PART.get(), ResearchLabPartBE::exposeItemHandler);
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(ResearchdRegistries.RESEARCH_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER);
        event.register(ResearchdRegistries.VALUE_EFFECT);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_KEY, Research.CODEC, Research.CODEC);
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPack.CODEC, ResearchPack.CODEC);
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
}
