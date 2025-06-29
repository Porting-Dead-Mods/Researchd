package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.registries.Researches;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class LangProvider extends LanguageProvider {
    public LangProvider(PackOutput output) {
        super(output, Researchd.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("researchd.research.queue.added", "%s added %s to the research queue!");
        add("researchd.research.queue.finished", "%s finished researching (%s)!");

        // Errors
        add("researchd.error.research_queue_desync", "Small desync happened, please relog. A research complete packed was emitted but your queue was empty");

        // Screen and contents
        add("screen.researchd.research_team.title", "Research Team");
        add("screen.researchd.research_team.buttons.invite", "Invite Player");
        add("screen.researchd.research_team.buttons.team_settings", "Team Settings");
        add("screen.researchd.research_team.buttons.leave_team", "Leave Team");
        add("screen.researchd.research_team.titles.members", "Members");
        add("screen.researchd.research_team.titles.recently_researched", "Recently Researched");

        addResearch(Researches.WOOD, "Wood");
        addResearch(Researches.STONE, "Stone");
        addResearch(Researches.COPPER, "Copper");
        addResearch(Researches.IRON, "Iron");
        addResearch(Researches.IRON_TOOLS, "Iron Tools");
        addResearch(Researches.IRON_ARMOR, "Iron Armor");
        addResearch(Researches.LIGHTNING_ROD, "Lightning Rod");
        addResearch(Researches.COPPER_BLOCK, "Copper Block");

        addResearchDesc(Researches.WOOD, "Punch those nasty trees");
        addResearchDesc(Researches.STONE, "Ooga booga cave man");
        addResearchDesc(Researches.COPPER, "A decent conductor");
        addResearchDesc(Researches.IRON, "Speedrunning human history");
        addResearchDesc(Researches.IRON_TOOLS, "Gearing up");
        addResearchDesc(Researches.IRON_ARMOR, "Isn't this an achievement already?");
        addResearchDesc(Researches.LIGHTNING_ROD, "I told ya it's a great conductor!");
        addResearchDesc(Researches.COPPER_BLOCK, "Efficient Storage");

        addResearchMethod(ConsumePackResearchMethod.ID, "Submit");
        addResearchMethod(ConsumeItemResearchMethod.ID, "Submit");
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
}
