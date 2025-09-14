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

        addResearch(ResearchdResearches.WOOD, "Wood");
        addResearch(ResearchdResearches.STONE, "Stone");
        addResearch(ResearchdResearches.COPPER, "Copper");
        addResearch(ResearchdResearches.IRON, "Iron");
        addResearch(ResearchdResearches.IRON_TOOLS, "Iron Tools");
        addResearch(ResearchdResearches.IRON_ARMOR, "Iron Armor");
        addResearch(ResearchdResearches.LIGHTNING_ROD, "Lightning Rod");
        addResearch(ResearchdResearches.COPPER_BLOCK, "Copper Block");

        addResearchDesc(ResearchdResearches.WOOD, "Punch those nasty trees");
        addResearchDesc(ResearchdResearches.STONE, "Ooga booga cave man");
        addResearchDesc(ResearchdResearches.COPPER, "A decent conductor");
        addResearchDesc(ResearchdResearches.IRON, "Speedrunning human history");
        addResearchDesc(ResearchdResearches.IRON_TOOLS, "Gearing up");
        addResearchDesc(ResearchdResearches.IRON_ARMOR, "Isn't this an achievement already?");
        addResearchDesc(ResearchdResearches.LIGHTNING_ROD, "I told ya it's a great conductor!");
        addResearchDesc(ResearchdResearches.COPPER_BLOCK, "Efficient Storage");

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
