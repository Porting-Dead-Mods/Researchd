package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.impl.data.ResearchedAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Researchd.MODID)
public class Researchd {
    public static final String MODID = "researchd";
    public static final String MODNAME = "Researchd";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Researchd(IEventBus modEventBus, ModContainer modContainer) {
        ResearchedAttachments.ATTACHMENTS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(ResearchdCapabilities.ENTITY, EntityType.PLAYER, (player, ctx) -> player.getData(ResearchedAttachments.ENTITY_RESEARCH));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
