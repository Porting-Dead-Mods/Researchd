package com.portingdeadmods.researchd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Researchd.MODID)
public class ResearchdCommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue RESEARCH_QUEUE_LENGTH = BUILDER
            .comment("The length of the research queue")
            .defineInRange("research_queue_length", 7, 1, 99);

    // To be moved to client
    private static final ModConfigSpec.BooleanValue CONSOLE_DEBUG = BUILDER
            .comment("Whether to enable console debug messages for Researchd")
            .define("enable_console_debug", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int researchQueueLength;
    public static boolean consoleDebug;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        researchQueueLength = RESEARCH_QUEUE_LENGTH.get();
        consoleDebug = CONSOLE_DEBUG.get();
    }
}
