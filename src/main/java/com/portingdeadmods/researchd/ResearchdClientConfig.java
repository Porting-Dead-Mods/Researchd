package com.portingdeadmods.researchd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SHOW_JOIN_MESSAGE = BUILDER
            .comment("Whether to show the join message on world load")
            .define("show_join_message", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean showJoinMessage;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) return;
        showJoinMessage = SHOW_JOIN_MESSAGE.get();
    }
}


