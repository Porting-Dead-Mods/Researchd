package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;

public interface ClientResearchMethodType {
    void buildLayout(RememberingLinearLayout layout, ClientResearch.Context context);

    ResearchMethod createResearchMethod(RememberingLinearLayout layout);
}
