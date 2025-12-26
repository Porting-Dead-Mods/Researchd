package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.ResearchdRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface ResearchdLangProvider {
    Map<String, String> getTranslations();

    default void addResearch(ResourceLocation key, String name) {
        add(ResearchdRegistries.RESEARCH_KEY.location().getPath() + "." + key.getNamespace() + "." + key.getPath() + "_name", name);
    }

    default void addResearchMethod(ResourceLocation key, String name) {
        add("research_method." + key.getNamespace() + "." + key.getPath(), name);
    }

    default void addResearchPackName(ResourceLocation key, String name) {
        add("research_pack." + key.toString().replace(':', '.') + "_name", name);
    }

    default void addResearchPackDescription(ResourceLocation key, String name) {
        add("research_pack" + key.toString().replace(':', '.') + "_desc", name);
    }

    default void addResearchPageTitle(ResourceLocation key, String title) {
        add("researchpage." + key.getNamespace() + "." + key.getPath() + ".title", title);
    }

    default void addResearchPageDescription(ResourceLocation key, String description) {
        add("researchpage." + key.getNamespace() + "." + key.getPath() + ".description", description);
    }

    default void addResearchPage(ResourceLocation key, String title, String description) {
        addResearchPageTitle(key, title);
        addResearchPageDescription(key, description);
    }

    default void add(String key, String name) {
        getTranslations().put(key, name);
    }
}
