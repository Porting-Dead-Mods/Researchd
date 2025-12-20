package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;

public interface ClientResearch {
    void buildLayout(RememberingLinearLayout layout, Context context);

    Research createResearch(RememberingLinearLayout layout);

    record Context(ResearchScreen parentScreen) {

    }

}
