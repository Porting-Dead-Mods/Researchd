package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.ConsumePackResearchMethod;
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
