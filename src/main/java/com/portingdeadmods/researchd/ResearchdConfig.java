package com.portingdeadmods.researchd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Researchd.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ResearchdConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue EXAMPLE_RESEARCH = BUILDER
            .comment("Whether to enable the example research provided by Researchd")
            .define("enable_example_research", true);
    private static final ModConfigSpec.IntValue RESEARCH_QUEUE_LENGTH = BUILDER
            .comment("The length of the research queue")
            .defineInRange("research_queue_length", 7, 1, 99);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean exampleResearch;
    public static int researchQueueLength;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        exampleResearch = EXAMPLE_RESEARCH.get();
        researchQueueLength = RESEARCH_QUEUE_LENGTH.get();
    }
}
