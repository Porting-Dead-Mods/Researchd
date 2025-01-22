package com.portingdeadmods.researchd.api.capabilties;

import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;

import java.util.List;
import java.util.Set;

public interface EntityResearch {
    ResearchQueue researchQueue();

    Set<ResearchInstance> researches();

    void addResearch(ResearchInstance researchInstance);

    void removeResearch(ResearchInstance researchInstance);
}
