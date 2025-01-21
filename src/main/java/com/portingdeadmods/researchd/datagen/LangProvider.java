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
        addResearch(Researches.COAL, "Coal");
        addResearch(Researches.WOOD, "Wood");
        addResearch(Researches.COPPER, "Copper");
        addResearch(Researches.STICK, "Stick");
        addResearch(Researches.WOODEN_PICKAXE, "Wooden Pickaxe");
        addResearch(Researches.STONE, "Stone");

        addResearchDesc(Researches.COAL, "Powerful Fuel for improving furnace factory capabilities");
        addResearchDesc(Researches.WOOD, "Allows you to punch those nasty Trees!");

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
