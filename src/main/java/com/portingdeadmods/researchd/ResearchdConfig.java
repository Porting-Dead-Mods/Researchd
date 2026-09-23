package com.portingdeadmods.researchd;

import com.portingdeadmods.portingdeadlibs.api.config.ConfigValue;

public final class ResearchdConfig {
    public static class Common {
        @ConfigValue(
                name = "Research Queue Length",
                comment = "The length of the researchPack queue",
                range = {1, 99})
        public static int researchQueueLength = 7;

        @ConfigValue(
                name = "Load Default Datapack",
                comment = "Whether to load the default resourcepack that can be enabled in the world creation screen.")
        public static boolean loadDefaultDatapack = true;

        @ConfigValue(
                name = "Console Debug",
                comment = "Whether to enable console debug messages for Researchd",
                category = "debug")
        public static boolean consoleDebug = false;
    }

    public static class Server {
        @ConfigValue(
                name = "Use FTB Teams if present",
                comment =
                        "Whether to use FTB Teams' API for team logic instead of Researchd's own.\nThis disables the team screen and team command",
                key = "use_ftb_teams")
        public static boolean useFTBTeams = false;

        @ConfigValue(
                name = "Research Lab Energy Usage",
                comment =
                        "Energy consumed per tick by a Research Lab while it is researching. Zero disables the feature.",
                category = "energy",
                key = "research_lab_energy_usage",
                range = {0, Integer.MAX_VALUE / 2})
        public static int researchLabEnergyUsage = 0;

        @ConfigValue(
                name = "Research Lab Energy Capacity",
                comment = "Size of a Research Lab's energy buffer and its per-tick transfer limit. Min: 2 x Usage",
                category = "energy",
                key = "research_lab_energy_capacity",
                range = {1, Integer.MAX_VALUE})
        public static int researchLabEnergyCapacity = 100000;

        public static int getResearchLabEnergyUsage() {
            researchLabEnergyUsage = Math.max(0, Math.min(researchLabEnergyUsage, Integer.MAX_VALUE / 2));
            return researchLabEnergyUsage;
        }

        public static int getResearchLabEnergyCapacity() {
            int minimumCapacity = getResearchLabEnergyUsage() * 2;
            int enforcedCapacity = Math.max(Math.max(researchLabEnergyCapacity, 1), minimumCapacity);
            if (researchLabEnergyCapacity != enforcedCapacity) {
                Researchd.LOGGER.warn(
                        "Research Lab energy capacity {} is below 2 x Usage; using {} instead",
                        researchLabEnergyCapacity,
                        enforcedCapacity);
                researchLabEnergyCapacity = enforcedCapacity;
            }
            return researchLabEnergyCapacity;
        }
    }

    public static class Client {
        @ConfigValue(name = "Show Join Message", comment = "Whether to show the join message on world load")
        public static boolean showJoinMessage = true;
    }
}
