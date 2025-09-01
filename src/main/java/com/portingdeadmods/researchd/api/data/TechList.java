package com.portingdeadmods.researchd.api.data;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;

import java.util.List;

public class TechList {
    public final UniqueArray<ResearchInstance> entries;

    public TechList(List<ResearchInstance> entries) {
        this.entries = new UniqueArray<>(entries);
        updateTechList();
    }

    public static TechList getClientTechList() {
        return new TechList(ClientResearchTeamHelper.getTeam().getMetadata().getResearchProgress().researches().values().stream().toList());
    }

    public void updateTechList() {
        List<ResearchInstance> sorted = this.entries.stream().sorted((a, b) -> {
            if (a.getResearchStatus() == b.getResearchStatus()) {
                return a.getKey().location().toString().compareTo(b.getKey().location().toString());
            }
            return a.getResearchStatus().getSortingValue() - b.getResearchStatus().getSortingValue();
        }).toList();

        this.entries.clear();
        this.entries.addAll(sorted);
    }

    public TechList getListForSearch(String searchVal) {
        List<ResearchInstance> entries = new UniqueArray<>();
        for (ResearchInstance entry : this.entries()) {
            if (entry.getDisplayName().getString().toLowerCase().contains(searchVal.strip().toLowerCase())) {
                entries.add(entry);
            }
        }
        return new TechList(entries);
    }

    public UniqueArray<ResearchInstance> entries() {
        return this.entries;
    }
}
