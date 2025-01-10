package com.portingdeadmods.researchd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Researchd.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue DEFAULT_RESEARCH_IMPLEMENTATION = BUILDER
            .comment("Include the default research implementation")
            .define("defaultResearchImplementation", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean defaultResearchImplementation;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultResearchImplementation = DEFAULT_RESEARCH_IMPLEMENTATION.get();
    }
}
