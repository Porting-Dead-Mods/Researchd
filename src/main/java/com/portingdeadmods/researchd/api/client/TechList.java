package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.level.Level;

public record TechList(UniqueArray<ResearchInstance> entries) {
    public TechList(Set<ResearchInstance> entries) {
        this(new UniqueArray<>(entries));
        this.sortTechList();
    }

    public static TechList getClientTechList() {
        ResearchTeam team = ResearchTeamHelperClient.getTeam();
        if (team == null) return new TechList(new HashSet<>());

        ResearchManager researchManager = ResearchdApi.getResearchManager();
        Set<ResearchInstance> entries = new HashSet<>();
        for (ResearchInstance instance : team.getResearches().values()) {
            if (researchManager != null && researchManager.getRelationsForResearch(instance.getResearch()) == null)
                continue;

            entries.add(instance);
        }
        return new TechList(entries);
    }

    public void sortTechList() {
        List<ResearchInstance> sorted = this.entries.stream()
                .sorted((a, b) -> {
                    if (a.getResearchStatus() == b.getResearchStatus()) {
                        return a.getResearch()
                                .location()
                                .toString()
                                .compareTo(b.getResearch().location().toString());
                    }
                    return a.getResearchStatus().getSortingValue()
                            - b.getResearchStatus().getSortingValue();
                })
                .toList();

        this.entries.clear();
        this.entries.addAll(sorted);
    }

    public TechList getListForSearch(String searchVal, Level level) {
        Set<ResearchInstance> entries = new UniqueArray<>();
        String searchLower = searchVal.strip().toLowerCase();

        for (ResearchInstance entry : this.entries()) {
            // Search by resource location
            String resourceLocation = entry.getResearch().location().toString().toLowerCase();

            // Search by localized display name
            String displayName = entry.getDisplayName(level).getString().toLowerCase();

            // Search by localized desc
            String description = entry.getDescription(level).getString().toLowerCase();

            if (resourceLocation.contains(searchLower)
                    || displayName.contains(searchLower)
                    || description.contains(searchLower)) {
                entries.add(entry);
            }
        }
        return new TechList(entries);
    }
}
