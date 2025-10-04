package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.utils.researches.ReloadableRegistryManager;

public interface RegistryManagersGetter {
    ReloadableRegistryManager<Research> researchd$getResearchesManager();

    ReloadableRegistryManager<ResearchPack> researchd$getResearchPackManager();
}
