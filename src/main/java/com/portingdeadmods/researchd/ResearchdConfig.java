package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.pdl.config.ConfigValue;
import net.neoforged.fml.common.EventBusSubscriber;

public final class ResearchdConfig {
    public static class Common {
        @ConfigValue(name = "Research Queue Length", comment = "The length of the research queue", range = {1, 99})
        public static int researchQueueLength = 7;

        @ConfigValue(name = "Load Examples Datapack", comment = "Whether to load the examples resourcepack that can be enabled in the world creation screen.")
        public static boolean loadExamplesDatapack = true;

        @ConfigValue(name = "Console Debug", comment = "Whether to enable console debug messages for Researchd", category = "debug")
        public static boolean consoleDebug = false;
    }

    public static class Client {
        @ConfigValue(name = "Show Join Message", comment = "Whether to show the join message on world load")
        public static boolean showJoinMessage = true;
    }

}
