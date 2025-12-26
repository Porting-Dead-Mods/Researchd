package com.portingdeadmods.researchd.resources.contents;

import java.util.HashMap;
import java.util.Map;

public class ResearchdLang implements ResearchdLangProvider {
    private final Map<String, String> translations;

    public ResearchdLang(String modid) {
        this.translations = new HashMap<>();
    }

    @Override
    public Map<String, String> getTranslations() {
        return translations;
    }

    public void build() {
        addResearch(ResearchdResearches.COBBLESTONE_LOC, "Wonder what type of stone it is...");
        addResearch(ResearchdResearches.OVERWORLD_PACK_LOC, "The start of something");
        addResearch(ResearchdResearches.NETHER_LOC, "From below...");
        addResearch(ResearchdResearches.END_LOC, "The start... of something?");
        addResearch(ResearchdResearches.BEACON_LOC, "The beam looks funny");
        addResearch(ResearchdResearches.END_CRYSTAL_LOC, "If you look at it, it spins...");
        addResearch(ResearchdResearches.STONE_LOC, "Nah it's just stone");
        addResearch(ResearchdResearches.DIFFERENT_ROCKS_LOC, "It's... just stone?");

        addResearchPackName(ResearchdResearchPacks.END_PACK_LOC, "End Research Pack");
        addResearchPackName(ResearchdResearchPacks.NETHER_PACK_LOC, "Nether Research Pack");
        addResearchPackName(ResearchdResearchPacks.OVERWORLD_PACK_LOC, "Overworld Research Pack");

		addResearchPage(ResearchdResearchPages.DEFAULT, "Welcome to Researchd", "This page contains example researches to showcase what the mod is capable of!");
		addResearchPage(ResearchdResearchPages.END_CRYSTAL, "Another Research Page", "Researches can be split into separate pages for organization!");
    }

    public Map<String, String> getContents() {
        return translations;
    }
}
