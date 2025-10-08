package com.portingdeadmods.researchd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdCommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue RESEARCH_QUEUE_LENGTH = BUILDER
            .comment("The length of the research queue")
            .defineInRange("research_queue_length", 7, 1, 99);

    private static final ModConfigSpec.BooleanValue LOAD_EXAMPLES_RESOURCES = BUILDER
            .comment("Whether to load the examples resourcepack that can be enabled in the world creation screen.")
            .define("load_examples_datapack", true);

    private static final ModConfigSpec.BooleanValue CONSOLE_DEBUG = BUILDER
            .comment("Whether to enable console debug messages for Researchd")
            .define("enable_console_debug", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int researchQueueLength;
    public static boolean consoleDebug;
    public static boolean loadExamplesDatapack;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) return;
        researchQueueLength = RESEARCH_QUEUE_LENGTH.get();
        consoleDebug = CONSOLE_DEBUG.get();
        loadExamplesDatapack = LOAD_EXAMPLES_RESOURCES.get();
    }
}
