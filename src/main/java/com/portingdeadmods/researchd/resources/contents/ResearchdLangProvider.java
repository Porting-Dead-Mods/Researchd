package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.ResearchdRegistries;
import java.util.Map;
import net.minecraft.resources.Identifier;

public interface ResearchdLangProvider {
    Map<String, String> getTranslations();

    default void addResearch(Identifier key, String name) {
        add(
                ResearchdRegistries.RESEARCH_KEY.identifier().getPath() + "." + key.getNamespace() + "." + key.getPath()
                        + "_name",
                name);
    }

    default void addResearchMethod(Identifier key, String name) {
        add("research_method." + key.getNamespace() + "." + key.getPath(), name);
    }

    default void addResearchPackName(Identifier key, String name) {
        add("research_pack." + key.toString().replace(':', '.') + "_name", name);
    }

    default void addResearchPackDescription(Identifier key, String name) {
        add("research_pack" + key.toString().replace(':', '.') + "_desc", name);
    }

    default void addResearchPageTitle(Identifier key, String title) {
        add("researchpage." + key.getNamespace() + "." + key.getPath() + ".title", title);
    }

    default void addResearchPageDescription(Identifier key, String description) {
        add("researchpage." + key.getNamespace() + "." + key.getPath() + ".description", description);
    }

    default void addResearchPage(Identifier key, String title, String description) {
        addResearchPageTitle(key, title);
        addResearchPageDescription(key, description);
    }

    default void add(String key, String name) {
        getTranslations().put(key, name);
    }
}
