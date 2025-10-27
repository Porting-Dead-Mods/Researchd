package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record TechList(UniqueArray<ResearchInstance> entries) {
    public TechList(Set<ResearchInstance> entries) {
        this(new UniqueArray<>(entries));
        this.sortTechList();
    }

    public static TechList getClientTechList() {
        return new TechList(new HashSet<>(ClientResearchTeamHelper.getTeam().getResearches().values()));
    }

    public void sortTechList() {
        List<ResearchInstance> sorted = this.entries.stream().sorted((a, b) -> {
            if (a.getResearchStatus() == b.getResearchStatus()) {
                return a.getKey().location().toString().compareTo(b.getKey().location().toString());
            }
            return a.getResearchStatus().getSortingValue() - b.getResearchStatus().getSortingValue();
        }).toList();

        this.entries.clear();
        this.entries.addAll(sorted);
    }

    public TechList getListForSearch(String searchVal, Level level) {
        Set<ResearchInstance> entries = new UniqueArray<>();
        String searchLower = searchVal.strip().toLowerCase();

        for (ResearchInstance entry : this.entries()) {
            // Search by resource location
            String resourceLocation = entry.getKey().location().toString().toLowerCase();

            // Search by localized display name
            String displayName = entry.getDisplayName(level).getString().toLowerCase();

            // Search by localized description
            String description = entry.getDescription(level).getString().toLowerCase();

            if (resourceLocation.contains(searchLower) ||
                displayName.contains(searchLower) ||
                description.contains(searchLower)) {
                entries.add(entry);
            }
        }
        return new TechList(entries);
    }

}
