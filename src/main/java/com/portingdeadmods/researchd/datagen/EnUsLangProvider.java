package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.registries.ResearchdResearchPacks;
import com.portingdeadmods.researchd.registries.ResearchdResearches;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Map;

import static com.portingdeadmods.researchd.registries.ResearchdBlocks.RESEARCH_LAB_CONTROLLER;
import static com.portingdeadmods.researchd.registries.ResearchdBlocks.RESEARCH_LAB_PART;

public final class EnUsLangProvider extends LanguageProvider {
    public EnUsLangProvider(PackOutput output) {
        super(output, Researchd.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ResearchdTranslations.init();

        for (Map.Entry<String, String> entry : ResearchdTranslations.TRANSLATIONS.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }

        addBlock(RESEARCH_LAB_PART, "Research Lab Part");
        addBlock(RESEARCH_LAB_CONTROLLER, "Research Lab");

        addResearch(ResearchdResearches.COBBLESTONE, "Wonder what type of stone it is...");
        addResearch(ResearchdResearches.OVERWORLD_PACK, "The start of something");
        addResearch(ResearchdResearches.NETHER, "From below...");
        addResearch(ResearchdResearches.THE_END, "The start... of something?");
        addResearch(ResearchdResearches.BEACON, "The beam looks funny");
        addResearch(ResearchdResearches.END_CRYSTAL, "If you look at it, it spins...");

        addResearchPack(ResearchdResearchPacks.END, "End Research Pack");
        addResearchPack(ResearchdResearchPacks.NETHER, "Nether Research Pack");
        addResearchPack(ResearchdResearchPacks.OVERWORLD, "Overworld Research Pack");
    }

    private void addResearch(ResourceKey<Research> key, String name) {
        add(key.registry().getPath() + "." + key.location().getNamespace() + "." + key.location().getPath(), name);
    }

    private void addResearchDesc(ResourceKey<Research> key, String name) {
        add("research_desc." + key.location().getNamespace() + "." + key.location().getPath(), name);
    }

    private void addResearchMethod(ResourceLocation key, String name) {
        add("research_method." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addResearchPack(ResourceKey<SimpleResearchPack> key, String name) {
        add("item.researchd.research_pack_" + key.location().toString().replace(':', '_'), name);
    }
}
