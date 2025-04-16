package com.portingdeadmods.researchd.api.capabilties;

import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;

import java.util.Set;

public interface EntityResearch {
    ResearchQueue researchQueue();

    Set<ResearchInstance> completedResearches();

    void completeResearch(ResearchInstance researchInstance);
}
