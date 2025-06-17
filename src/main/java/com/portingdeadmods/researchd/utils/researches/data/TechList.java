package com.portingdeadmods.researchd.utils.researches.data;

import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.UniqueArray;

import java.util.List;

public class TechList {
    public final UniqueArray<ResearchInstance> entries;

    public TechList(List<ResearchInstance> entries) {
        this.entries = new UniqueArray<>(entries);
        updateTechList();
    }

    public void updateTechList() {
        List<ResearchInstance> sorted = this.entries.stream().sorted((a, b) -> {
            if (a.getResearchStatus() == b.getResearchStatus()) {
                return a.getResearch().location().toString().compareTo(b.getResearch().location().toString());
            }
            return a.getResearchStatus().getSortingValue() - b.getResearchStatus().getSortingValue();
        }).toList();

        this.entries.clear();
        this.entries.addAll(sorted);
    }

    public UniqueArray<ResearchInstance> entries() {
        return this.entries;
    }
}
