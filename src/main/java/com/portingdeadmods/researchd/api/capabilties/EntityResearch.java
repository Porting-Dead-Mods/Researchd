package com.portingdeadmods.researchd.api.capabilties;

import com.portingdeadmods.researchd.api.research.ResearchInstance;

import java.util.List;
import java.util.Set;

public interface EntityResearch {
    Set<ResearchInstance> researchQueue();

    Set<ResearchInstance> researches();

    void addResearch(ResearchInstance researchInstance);

    void removeResearch(ResearchInstance researchInstance);
}
