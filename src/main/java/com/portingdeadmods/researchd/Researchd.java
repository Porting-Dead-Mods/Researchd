package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.registries.serializers.ResearchPackSerializers;
import com.portingdeadmods.researchd.registries.serializers.ResearchPredicateSerializers;
import com.portingdeadmods.researchd.registries.serializers.ResearchSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Researchd.MODID)
public class Researchd {
    public static final String MODID = "researchd";
    public static final String MODNAME = "Researchd";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Researchd(IEventBus modEventBus, ModContainer modContainer) {
        ResearchdAttachments.ATTACHMENTS.register(modEventBus);
        ResearchPackSerializers.SERIALIZERS.register(modEventBus);
        ResearchSerializers.SERIALIZERS.register(modEventBus);
        ResearchPredicateSerializers.SERIALIZERS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(ResearchdCapabilities.ENTITY, EntityType.PLAYER, (player, ctx) -> player.getData(ResearchdAttachments.ENTITY_RESEARCH));
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(ResearchdRegistries.RESEARCH_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_PACK_SERIALIZER);
        event.register(ResearchdRegistries.RESEARCH_PREDICATE_SERIALIZER);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_KEY, Research.CODEC);
        event.dataPackRegistry(ResearchdRegistries.RESEARCH_PACK_KEY, ResearchPack.CODEC);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
