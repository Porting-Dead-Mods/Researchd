package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.ResearchdRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ResearchdLang {
    private final Map<String, String> translations;
    private final String modid;

    public ResearchdLang(String modid) {
        this.modid = modid;
        this.translations = new HashMap<>();
    }

    public void build() {
        addResearch(ResearchdResearches.COBBLESTONE_LOC, "Wonder what type of stone it is...");
        addResearch(ResearchdResearches.OVERWORLD_PACK_LOC, "The start of something");
        addResearch(ResearchdResearches.NETHER_LOC, "From below...");
        addResearch(ResearchdResearches.END_LOC, "The start... of something?");
        addResearch(ResearchdResearches.BEACON_LOC, "The beam looks funny");
        addResearch(ResearchdResearches.END_CRYSTAL_LOC, "If you look at it, it spins...");

        addResearchPack(ResearchdResearchPacks.END_PACK_LOC, "End Research Pack");
        addResearchPack(ResearchdResearchPacks.NETHER_PACK_LOC, "Nether Research Pack");
        addResearchPack(ResearchdResearchPacks.OVERWORLD_PACK_LOC, "Overworld Research Pack");
    }

    private void addResearch(ResourceLocation key, String name) {
        add(ResearchdRegistries.RESEARCH_KEY.location().getPath() + "." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addResearchMethod(ResourceLocation key, String name) {
        add("research_method." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addResearchPack(ResourceLocation key, String name) {
        add("item.researchd.research_pack_" + key.toString().replace(':', '_'), name);
    }

    private void add(String key, String name) {
        this.translations.put(key, name);
    }

    public Map<String, String> getContents() {
        return translations;
    }
}
